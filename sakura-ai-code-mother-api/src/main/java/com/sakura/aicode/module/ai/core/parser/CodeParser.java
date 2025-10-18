package com.sakura.aicode.module.ai.core.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 代码解析器
 * @param <T> 要返回的类型
 *
 * @author Sakura
 */
public interface CodeParser<T> {

    // 解析HTML代码 正则表达式
    static final Pattern HTML_CODE_PATTERN = Pattern.compile("```HTML\\s*\\n([\\s\\S]*?)```", Pattern.CASE_INSENSITIVE);
    // 解析CSS代码 正则表达式
    static final Pattern CSS_CODE_PATTERN = Pattern.compile("```css\\s*\\n([\\s\\S]*?)```", Pattern.CASE_INSENSITIVE);
    // 解析JS代码 正则表达式
    static final Pattern JS_CODE_PATTERN = Pattern.compile("```(?:js|javascript)\\s*\\n([\\s\\S]*)?```", Pattern.CASE_INSENSITIVE);

    /**
     * 解析代码
     * @param code 代码
     * @return 返回 <T> 解析的内容
     */
    T parseCode(String code);

    default String extractCodeByPattern(Pattern pattern, String codeContent) {
        Matcher matcher = pattern.matcher(codeContent);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
