package com.sakura.aicode.module.ai.service;

import dev.langchain4j.service.SystemMessage;

public interface AiCodeGeneratorService {

    /**
     * 生成HTML代码
     * @param userMessage 用户 prompt
     * @return 生成的代码结果
     */
    @SystemMessage(fromResource = "prompt/codegen-html-system-prompt.txt")
    String generateHtmlCode(String userMessage);

    /**
     * 生成多文件HTML代码
     * @param userMessage 用户 prompt
     * @return 生成的代码结果
     */
    @SystemMessage(fromResource = "prompt/codegen-multi-file-system-prompt.txt")
    String generateMultiFileHtmlCode(String userMessage);
}
