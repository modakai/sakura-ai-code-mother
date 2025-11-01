package com.sakura.aicode.module.ai.core;

import com.sakura.aicode.common.enums.CodeGenTypeEnum;
import com.sakura.aicode.module.ai.core.handler.JsonTextStreamHandler;
import com.sakura.aicode.module.ai.core.handler.SimpleTextStreamHandler;
import com.sakura.aicode.module.auth.domain.vo.LoginUserVO;
import com.sakura.aicode.module.history.service.ChatHistoryOriginalService;
import com.sakura.aicode.module.history.service.ChatHistoryService;
import reactor.core.publisher.Flux;

/**
 * 流式消息处理器
 * @author Sakura
 */
public class StreamHandlerExecutor {

    private static final SimpleTextStreamHandler  simpleTextStreamHandler = new SimpleTextStreamHandler();
    private static final JsonTextStreamHandler jsonTextStreamHandler = new JsonTextStreamHandler();


    public static Flux<String> execStream(Flux<String> originFlux,
                                          CodeGenTypeEnum codeGenTypeEnum,
                                          ChatHistoryService chatHistoryService,
                                          ChatHistoryOriginalService chatHistoryOriginalService,
                                          long appId, LoginUserVO loginUser) {
        return switch (codeGenTypeEnum) {
            case VUE_PROJECT -> jsonTextStreamHandler.handle(originFlux, chatHistoryService, chatHistoryOriginalService, appId, loginUser);
            case HTML, MULTI_FILE -> simpleTextStreamHandler.handle(originFlux, chatHistoryService, appId, loginUser);
        };
    }
}
