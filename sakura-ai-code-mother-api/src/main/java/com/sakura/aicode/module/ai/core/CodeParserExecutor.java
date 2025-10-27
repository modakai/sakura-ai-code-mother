package com.sakura.aicode.module.ai.core;

import com.sakura.aicode.common.ErrorCode;
import com.sakura.aicode.common.enums.CodeGenTypeEnum;
import com.sakura.aicode.exception.BusinessException;
import com.sakura.aicode.module.ai.core.parser.HtmlCodeParser;
import com.sakura.aicode.module.ai.core.parser.MultiFileCodeParser;

/**
 * 代码解析执行器
 * @author Sakura
 */
public final class CodeParserExecutor {

    private static final HtmlCodeParser htmlCodeParser = new HtmlCodeParser();
    private static final MultiFileCodeParser multiFileCodeParser = new MultiFileCodeParser();

    /**
     *  执行解析diamond
     * @param codeGenType 代码生成类型
     * @param code 代码
     * @return 解析好的代码
     */
    public static Object executorCodeParser(CodeGenTypeEnum codeGenType, String code) {
        return switch (codeGenType) {
            case HTML -> htmlCodeParser.parseCode(code);
            case MULTI_FILE -> multiFileCodeParser.parseCode(code);
            case VUE_PROJECT -> new Object();
            default -> throw new BusinessException(ErrorCode.OPERATION_ERROR, "不支持的生成类型：" + codeGenType.getValue());
        };
    }
}
