package com.sakura.aicode.module.ai.service;

import com.sakura.aicode.module.ai.core.model.HtmlCodeResult;
import com.sakura.aicode.module.ai.core.model.MutiFileHtmlCodeResult;
import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.UserMessage;
import reactor.core.publisher.Flux;

/**
 * Ai代码生成服务
 */
public interface AiCodeGeneratorService {

    /**
     * 生成HTML代码
     * @param userMessage 用户 prompt
     * @return 生成的代码结果
     */
    @SystemMessage(fromResource = "prompt/codegen-html-system-prompt.txt")
    HtmlCodeResult generateHtmlCode(@MemoryId int memoryId, @UserMessage String userMessage);

    /**
     * 生成多文件HTML代码
     * @param userMessage 用户 prompt
     * @return 生成的代码结果
     */
    @SystemMessage(fromResource = "prompt/codegen-multi-file-system-prompt.txt")
    MutiFileHtmlCodeResult generateMultiFileHtmlCode(@MemoryId long memoryId, @UserMessage String userMessage);

    /**
     * 生成Vue文件的代码
     *
     * @param userMessage 用户消息
     * @param memoryId 记忆id
     * @return 生成的代码结果
     */
    @SystemMessage(fromResource = "prompt/codegen-vue-system-prompt.txt")
    TokenStream generateVueStream(@MemoryId long memoryId, @UserMessage String userMessage);

    /**
     * 生成 HTML 代码（流式）
     *
     * @param userMessage 用户消息
     * @param memoryId 记忆id
     * @return 生成的代码结果
     */
    @SystemMessage(fromResource = "prompt/codegen-html-system-prompt.txt")
    Flux<String> generateHtmlCodeStream(@MemoryId long memoryId, @UserMessage String userMessage);

    /**
     * 生成多文件代码（流式）
     *
     * @param userMessage 用户消息
     * @param memoryId 记忆id
     * @return 生成的代码结果
     */
    @SystemMessage(fromResource = "prompt/codegen-multi-file-system-prompt.txt")
    Flux<String> generateMultiFileCodeStream(@MemoryId long memoryId, @UserMessage String userMessage);

}
