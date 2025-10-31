package com.sakura.aicode.module.ai.core.handler;

import com.sakura.aicode.module.auth.domain.vo.LoginUserVO;
import com.sakura.aicode.module.history.service.ChatHistoryService;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 简单文本流处理器
 * 处理 HTML 和 MULTI_FILE 类型的流式响应
 */
@Slf4j
public class SimpleTextStreamHandler {

    /**
     * 处理传统流（HTML, MULTI_FILE）
     * 直接收集完整的文本响应
     *
     * @param originFlux         原始流
     * @param chatHistoryService 聊天历史服务
     * @param appId              应用ID
     * @param loginUser          登录用户
     * @return 处理后的流
     */
    public Flux<String> handle(Flux<String> originFlux,
                               ChatHistoryService chatHistoryService,
                               long appId, LoginUserVO loginUser) {
        StringBuilder builder = new StringBuilder();
        return originFlux
                .map(chunk -> {
                    builder.append(chunk);
                    return chunk;
                })
                .doOnComplete(() -> chatHistoryService.saveAiMessage(appId, builder.toString(), loginUser.getId(), List.of()))
                .doOnError(e -> chatHistoryService.saveAiMessage(appId, "AI回复错误：" + e.getMessage(), loginUser.getId(), List.of()));
    }
}
