package com.sakura.aicode.module.ai.core;

import com.sakura.aicode.module.ai.model.HtmlCodeResult;
import com.sakura.aicode.module.ai.model.MutiFileHtmlCodeResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 代码解析器
 * 解析AI响应回来的代码
 * @author Sakura
 */
@Deprecated
public class CodeParser {

    // 解析HTML代码 正则表达式
    private static final Pattern HTML_CODE_PATTERN = Pattern.compile("```HTML\\s*\\n([\\s\\S]*?)```", Pattern.CASE_INSENSITIVE);
    // 解析CSS代码 正则表达式
    private static final Pattern CSS_CODE_PATTERN = Pattern.compile("```css\\s*\\n([\\s\\S]*?)```", Pattern.CASE_INSENSITIVE);
    // 解析JS代码 正则表达式
    private static final Pattern JS_CODE_PATTERN = Pattern.compile("```(?:js|javascript)\\s*\\n([\\s\\S]*)?```", Pattern.CASE_INSENSITIVE);


    /**
     * 解析 单个HTML代码
     * @param codeContent 代码内容
     * @return 返回 {@link HtmlCodeResult}
     */
    public static HtmlCodeResult parserHtmlCode(String codeContent) {
        HtmlCodeResult htmlCodeResult = new HtmlCodeResult();
        String htmlCode = extractHtmlCode(codeContent);
        if (htmlCode != null && !htmlCode.trim().isEmpty()) {
            htmlCodeResult.setHtmlCode(htmlCode.trim());
        } else {
            htmlCodeResult.setHtmlCode(codeContent.trim());
        }
        return htmlCodeResult;
    }

    /**
     * 解析 单个HTML代码
     * @param codeContent 代码内容
     * @return 返回 {@link MutiFileHtmlCodeResult}
     */
    public static MutiFileHtmlCodeResult parserMultiFileCode(String codeContent) {
        MutiFileHtmlCodeResult result = new MutiFileHtmlCodeResult();
        String htmlCode = extractCodeByPattern(HTML_CODE_PATTERN, codeContent);
        String cssCode = extractCodeByPattern(CSS_CODE_PATTERN, codeContent);
        String jsCode = extractCodeByPattern(JS_CODE_PATTERN, codeContent);
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

    private static String extractHtmlCode(String codeContent) {
        Matcher matcher = HTML_CODE_PATTERN.matcher(codeContent);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }

    private static String extractCodeByPattern(Pattern pattern, String codeContent) {
        Matcher matcher = pattern.matcher(codeContent);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
