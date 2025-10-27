package com.sakura.aicode.module.ai.facade;

import cn.hutool.core.util.StrUtil;
import com.sakura.aicode.common.ErrorCode;
import com.sakura.aicode.common.enums.CodeGenTypeEnum;
import com.sakura.aicode.exception.BusinessException;
import com.sakura.aicode.module.ai.core.CodeFileSaverExecutor;
import com.sakura.aicode.module.ai.core.CodeParserExecutor;
import com.sakura.aicode.module.ai.core.model.HtmlCodeResult;
import com.sakura.aicode.module.ai.core.model.MutiFileHtmlCodeResult;
import com.sakura.aicode.module.ai.core.model.message.AiResponseMessage;
import com.sakura.aicode.module.ai.core.model.message.ToolExecutedMessage;
import com.sakura.aicode.module.ai.core.model.message.ToolRequestMessage;
import com.sakura.aicode.module.ai.service.AiCodeGeneratorService;
import com.sakura.aicode.utils.JsonUtils;
import dev.langchain4j.agent.tool.ToolExecutionRequest;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.tool.ToolExecution;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;

/**
 * Ai生成代码门面 门面模块
 * 隐藏内部调用 AI 生成代码 -》并根据不同的模式保存代码的流程
 *
 * @author Sakura
 */
@Service
@RequiredArgsConstructor
@Slf4j(topic = "ai.code.generator")
public class AiCodeGeneratorFacade {

    private final AiCodeGeneratorService aiCodeGeneratorService;

    /**
     * 统一入口：根据类型生成并保存对应的代码
     *
     * @param userMessage     用户提示词
     * @param codeGenTypeEnum 代码生成的类型
     * @param appId           应用id
     * @return 返回生成文件的目录
     */
    public File generatorCodeAndSave(String userMessage, CodeGenTypeEnum codeGenTypeEnum, Long appId) {
        if (StrUtil.isBlank(userMessage)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "prompt为空");
        }
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "代码生成类型为空");
        }
        return switch (codeGenTypeEnum) {
            // 单HTML
            case HTML -> {
                HtmlCodeResult result = aiCodeGeneratorService.generateHtmlCode(1, userMessage);
                yield CodeFileSaverExecutor.executeSaver(result, codeGenTypeEnum, appId);
            }
            // 原生 HTML css js
            case MULTI_FILE -> {
                MutiFileHtmlCodeResult result = aiCodeGeneratorService.generateMultiFileHtmlCode(appId, userMessage);
                yield CodeFileSaverExecutor.executeSaver(result, codeGenTypeEnum, appId);
            }
            default -> {
                String error = "不支持生成类型：" + codeGenTypeEnum.getValue();
                throw new BusinessException(ErrorCode.PARAMS_ERROR, error);
            }
        };
    }

    /**
     * 统一入口(流式)：根据类型生成并保存对应的代码
     *
     * @param userMessage     用户提示词
     * @param codeGenTypeEnum 代码生成的类型
     * @param appId           应用id
     * @return 返回生成文件的目录
     */
    public Flux<String> generatorCodeAndSaveWithStream(String userMessage, CodeGenTypeEnum codeGenTypeEnum, Long appId) {
        if (StrUtil.isBlank(userMessage)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "prompt为空");
        }
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "代码生成类型为空");
        }
        return switch (codeGenTypeEnum) {
            case VUE_PROJECT -> {
                TokenStream tokenStream = aiCodeGeneratorService.generateVueStream(appId, userMessage);
                yield processTokenStream(tokenStream);
            }
            // 单HTML
            case HTML -> {
                Flux<String> result = aiCodeGeneratorService.generateHtmlCodeStream(appId, userMessage);
                yield processCodeStream(result, codeGenTypeEnum, appId);
            }
            // 原生 HTML css js
            case MULTI_FILE -> {
                Flux<String> result = aiCodeGeneratorService.generateMultiFileCodeStream(appId, userMessage);
                yield processCodeStream(result, codeGenTypeEnum, appId);
            }
            default -> {
                String error = "不支持生成类型：" + codeGenTypeEnum.getValue();
                throw new BusinessException(ErrorCode.PARAMS_ERROR, error);
            }
        };
    }

    private Flux<String> processCodeStream(Flux<String> result, CodeGenTypeEnum codeGenType, Long appId) {
        StringBuilder codeBuilder = new StringBuilder();
        // 实时收集代码片段
        return result.doOnNext(codeBuilder::append).doOnComplete(() -> {
            // 流式返回完成保存代码
            String codeContent = codeBuilder.toString();
            // 解析执行器解析code
            Object codeParser = CodeParserExecutor.executorCodeParser(codeGenType, codeContent);
            // 保存执行器 保存code
            File file = CodeFileSaverExecutor.executeSaver(codeParser, codeGenType, appId);
            log.info("保存成功：路径为：{}", file.getAbsolutePath());
        });
    }

    /**
     * 将 TokenStream 转换为 Flux<String>，并传递工具调用信息
     *
     * @param tokenStream TokenStream 对象
     * @return Flux<String> 流式响应
     */
    private Flux<String> processTokenStream(TokenStream tokenStream) {
        return Flux.create(sink -> {
            tokenStream.onPartialResponse((String partialResponse) -> {
                        AiResponseMessage aiResponseMessage = new AiResponseMessage(partialResponse);
                        sink.next(JsonUtils.toJson(aiResponseMessage));
                    })
                    // 调用工具之前的执行
                    .beforeToolExecution((beforeToolExecution) -> {
                        ToolExecutionRequest request = beforeToolExecution.request();
                        ToolRequestMessage toolRequestMessage = new ToolRequestMessage(request);
                        sink.next(JsonUtils.toJson(toolRequestMessage));
                    })
                    .onToolExecuted((ToolExecution toolExecution) -> {
                        ToolExecutedMessage toolExecutedMessage = new ToolExecutedMessage(toolExecution);
                        sink.next(JsonUtils.toJson(toolExecutedMessage));
                    })
                    .onCompleteResponse((ChatResponse response) -> {
                        sink.complete();
                    })
                    .onError((Throwable error) -> {
                        error.printStackTrace();
                        sink.error(error);
                    })
                    .start();
        });
    }


}
