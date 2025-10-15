package com.sakura.aicode.module.ai.service;

import com.sakura.aicode.module.ai.model.HtmlCodeResult;
import com.sakura.aicode.module.ai.model.MutiFileHtmlCodeResult;
import dev.langchain4j.service.SystemMessage;

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
    HtmlCodeResult generateHtmlCode(String userMessage);

    /**
     * 生成多文件HTML代码
     * @param userMessage 用户 prompt
     * @return 生成的代码结果
     */
    @SystemMessage(fromResource = "prompt/codegen-multi-file-system-prompt.txt")
    MutiFileHtmlCodeResult generateMultiFileHtmlCode(String userMessage);
}
