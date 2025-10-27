package com.sakura.aicode.module.ai.core.model;

import dev.langchain4j.model.output.structured.Description;
import lombok.Data;

/**
 * 生成多个文件HTML代码结果
 */
@Description("生成多个文件HTML代码结果")
@Data
public class MutiFileHtmlCodeResult {

    /**
     * HTML代码
     */
    @Description("HTML代码")
    private String htmlCode;

    /**
     * CSS代码
     */
    @Description("CSS代码")
    private String cssCode;

    /**
     * JS代码
     */
    @Description("JS代码")
    private String jsCode;

    @Description("生成代码的描述")
    private String description;

}
