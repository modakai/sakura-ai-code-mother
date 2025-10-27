package com.sakura.aicode.module.ai.core.parser;

import com.sakura.aicode.module.ai.core.model.MutiFileHtmlCodeResult;

/**
 * 多文件原生解析
 * @author Sakura
 */
public class MultiFileCodeParser implements CodeParser<MutiFileHtmlCodeResult>{

    @Override
    public MutiFileHtmlCodeResult parseCode(String code) {
        MutiFileHtmlCodeResult result = new MutiFileHtmlCodeResult();
        String htmlCode = extractCodeByPattern(HTML_CODE_PATTERN, code);
        String cssCode = extractCodeByPattern(CSS_CODE_PATTERN, code);
        String jsCode = extractCodeByPattern(JS_CODE_PATTERN, code);
        if (htmlCode != null && !htmlCode.trim().isEmpty()) {
            result.setHtmlCode(htmlCode.trim());
        }
        if (cssCode != null && !cssCode.trim().isEmpty()) {
            result.setCssCode(cssCode.trim());
        }
        if (jsCode != null && !jsCode.trim().isEmpty()) {
            result.setJsCode(jsCode.trim());
        }
        return result;
    }
}
