package com.sakura.aicode.module.ai.core.handler;

import cn.hutool.core.util.StrUtil;
import com.sakura.aicode.common.constant.AiConstant;
import com.sakura.aicode.module.ai.core.builder.VueProjectBuilder;
import com.sakura.aicode.module.ai.core.model.message.*;
import com.sakura.aicode.module.ai.tools.BaseTool;
import com.sakura.aicode.module.ai.tools.ToolFactory;
import com.sakura.aicode.module.auth.domain.vo.LoginUserVO;
import com.sakura.aicode.module.history.common.enums.MessageTypeEnum;
import com.sakura.aicode.module.history.domain.entity.ChatHistoryOriginal;
import com.sakura.aicode.module.history.service.ChatHistoryOriginalService;
import com.sakura.aicode.module.history.service.ChatHistoryService;
import com.sakura.aicode.utils.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.io.File;
import java.util.*;

/**
 * JSON 消息流处理器
 * 处理 VUE_PROJECT 类型的复杂流式响应，包含工具调用信息
 */
@Slf4j
public class JsonTextStreamHandler {


    /**
     * 处理 TokenStream（VUE_PROJECT）
     * 解析 JSON 消息并重组为完整的响应格式
     *
     * @param originFlux         原始流
     * @param chatHistoryService 聊天历史服务
     * @param appId              应用ID
     * @param loginUser          登录用户
     * @return 处理后的流
     */
    public Flux<String> handle(Flux<String> originFlux,
                               ChatHistoryService chatHistoryService,
                               ChatHistoryOriginalService chatHistoryOriginalService,
                               long appId, LoginUserVO loginUser) {
        // 收集数据用于生成后端记忆格式
        StringBuilder chatHistoryStringBuilder = new StringBuilder();
        // 收集用于恢复对话记忆的数据
        StringBuilder aiResponseStringBuilder = new StringBuilder();
        // 每个 Flux 流可能包含多条工具调用和 AI_RESPONSE 响应信息，统一收集之后批量入库
        List<ChatHistoryOriginal> originalChatHistoryList = new ArrayList<>();
        // 用于跟踪已经见过的工具ID，判断是否是第一次调用
        Set<String> seenToolIds = new HashSet<>();
        return originFlux
                .map(chunk -> {
                    // 解析每个 JSON 消息块
                    return handleJsonMessageChunk(chunk, chatHistoryStringBuilder, aiResponseStringBuilder, originalChatHistoryList, seenToolIds);
                })
                .filter(StrUtil::isNotEmpty) // 过滤空字串
                .doOnComplete(() -> {
                    // 工具调用信息入库
                    if (!originalChatHistoryList.isEmpty()) {
                        // 完善 ChatHistoryOriginal 信息
                        originalChatHistoryList.forEach(chatHistory -> {
                            chatHistory.setAppId(appId);
                            chatHistory.setUserId(loginUser.getId());
                        });
                        // 批量入库
                        chatHistoryOriginalService.addOriginalChatMessageBatch(originalChatHistoryList);
                    }
                    // Ai response 入库(两种情况：1. 没有进行工具调用。2. 工具调用结束之后 AI 一般还会有一句返回)
                    String aiResponseStr = aiResponseStringBuilder.toString();
                    chatHistoryOriginalService.addOriginalChatMessage(appId, aiResponseStr, MessageTypeEnum.AI.getValue(), loginUser.getId());

                    chatHistoryService.saveAiMessage(appId, chatHistoryStringBuilder.toString(), loginUser.getId());
                    // 构建项目
                    String projectPath = AiConstant.CODE_OUTPUT_ROOT_DIR + File.separator + AiConstant.VUE_PROJECT_PATH + appId;
                    VueProjectBuilder.buildAsync(projectPath);
                })
                .doOnError(e -> {
                    String errorMessage = "AI回复失败: " + e.getMessage();
                    chatHistoryService.saveAiMessage(appId, errorMessage, loginUser.getId());
                    chatHistoryOriginalService.addOriginalChatMessage(appId, errorMessage, MessageTypeEnum.AI.getValue(), loginUser.getId());
                });
    }

    /**
     * 解析并收集 TokenStream 数据
     */
    private String handleJsonMessageChunk(String chunk, StringBuilder chatHistoryStringBuilder,
                                          StringBuilder aiResponseStringBuilder, List<ChatHistoryOriginal> originalChatHistoryList ,
                                          Set<String> seenToolIds) {
        // 解析 JSON
        StreamMessage streamMessage = JsonUtils.fromJson(chunk, StreamMessage.class);
        StreamMessageTypeEnum typeEnum = StreamMessageTypeEnum.getEnumByValue(streamMessage.getType());
        switch (typeEnum) {
            case AI_RESPONSE -> {
                AiResponseMessage aiMessage = JsonUtils.fromJson(chunk, AiResponseMessage.class);
                String data = aiMessage.getData();
                // 直接拼接响应
                chatHistoryStringBuilder.append(data);
                aiResponseStringBuilder.append(data);
                return data;
            }
            case TOOL_REQUEST -> {
                ToolRequestMessage toolRequestMessage = JsonUtils.fromJson(chunk, ToolRequestMessage.class);
                String toolId = toolRequestMessage.getId();
                // 检查是否是第一次看到这个工具 ID
                if (toolId != null && !seenToolIds.contains(toolId)) {
                    // 第一次调用这个工具，记录 ID 并完整返回工具信息
                    seenToolIds.add(toolId);
                    BaseTool tool = ToolFactory.getTool(toolRequestMessage.getName());
                    return tool.generateToolResult();
                } else {
                    // 不是第一次调用这个工具，直接返回空
                    return "";
                }
            }
            case TOOL_EXECUTED -> {
                // 处理工具调用信息
                processToolExecutionMessage(aiResponseStringBuilder, chunk, originalChatHistoryList);

                ToolExecutedMessage toolExecutedMessage = JsonUtils.fromJson(chunk, ToolExecutedMessage.class);
                String toolName = toolExecutedMessage.getName();
                Map<String, String> argumentsMap = JsonUtils.fromJsonToMap(toolExecutedMessage.getArguments(), String.class, String.class);

                BaseTool tool = ToolFactory.getTool(toolName);
                String result = tool.generateToolExecutedResult(argumentsMap);
                // 输出前端和要持久化的内容
                String output = String.format("\n\n%s\n\n", result);
                chatHistoryStringBuilder.append(output);
                return output;
            }
            default -> {
                log.error("不支持的消息类型: {}", typeEnum);
                return "";
            }
        }
    }

    /**
     * 解析处理工具调用相关信息
     * @param aiResponseStringBuilder
     * @param chunk
     * @param originalChatHistoryList
     */
    private void processToolExecutionMessage(StringBuilder aiResponseStringBuilder, String chunk, List<ChatHistoryOriginal> originalChatHistoryList) {
        // 解析 chunk
        ToolExecutedMessage toolExecutedMessage = JsonUtils.fromJson(chunk, ToolExecutedMessage.class);
        // 构造工具调用请求对象(工具调用结果的数据就是从调用请求里拿的，所以直接在这里处理调用请求信息)
        String aiResponseStr = aiResponseStringBuilder.toString();
        ToolRequestMessage toolRequestMessage = new ToolRequestMessage();
        toolRequestMessage.setId(toolExecutedMessage.getId());
        toolRequestMessage.setName(toolExecutedMessage.getName());
        toolRequestMessage.setArguments(toolExecutedMessage.getArguments());
        toolRequestMessage.setText(aiResponseStr);
        // 转换成 JSON
        String toolRequestJsonStr = JsonUtils.toJson(toolRequestMessage);
        // 构造 ChatHistory 存入列表
        ChatHistoryOriginal toolRequestHistory = ChatHistoryOriginal.builder()
                .message(toolRequestJsonStr)
                .messageType(MessageTypeEnum.TOOL_EXECUTION_REQUEST.getValue())
                .build();
        originalChatHistoryList.add(toolRequestHistory);
        ChatHistoryOriginal toolResultHistory = ChatHistoryOriginal.builder()
                .message(chunk)
                .messageType(MessageTypeEnum.TOOL_EXECUTION_RESULT.getValue())
                .build();
        originalChatHistoryList.add(toolResultHistory);
        // AI 响应内容暂时结束，置空 aiResponseStringBuilder
        aiResponseStringBuilder.setLength(0);
    }
}

