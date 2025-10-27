package com.sakura.aicode.module.ai.core.parser;

import com.sakura.aicode.module.ai.core.model.HtmlCodeResult;

/**
 * HTML代码解析器具体实现
 * @author Sakura
 */
public class HtmlCodeParser implements CodeParser<HtmlCodeResult>{

    @Override
    public HtmlCodeResult parseCode(String code) {
        HtmlCodeResult htmlCodeResult = new HtmlCodeResult();
        String htmlCode = extractCodeByPattern(HTML_CODE_PATTERN, code);
        if (htmlCode != null && !htmlCode.trim().isEmpty()) {
            htmlCodeResult.setHtmlCode(htmlCode.trim());
        } else {
            htmlCodeResult.setHtmlCode(code.trim());
        }
        return htmlCodeResult;
    }
}
