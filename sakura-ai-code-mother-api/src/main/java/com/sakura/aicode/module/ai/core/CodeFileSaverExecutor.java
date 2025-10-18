package com.sakura.aicode.module.ai.core;

import com.sakura.aicode.common.ErrorCode;
import com.sakura.aicode.common.enums.CodeGenTypeEnum;
import com.sakura.aicode.exception.BusinessException;
import com.sakura.aicode.module.ai.core.saver.HtmlCodeFilerSaverTemplate;
import com.sakura.aicode.module.ai.core.saver.MutieFileSaverTemplate;
import com.sakura.aicode.module.ai.model.HtmlCodeResult;
import com.sakura.aicode.module.ai.model.MutiFileHtmlCodeResult;

import java.io.File;

/**
 * 保存文件代码执行器
 * @author Sakura
 */
public class CodeFileSaverExecutor {

    private static final HtmlCodeFilerSaverTemplate htmlCodeFilerSaverTemplate = new HtmlCodeFilerSaverTemplate();
    private static final MutieFileSaverTemplate mutieFileSaverTemplate = new MutieFileSaverTemplate();

    /**
     * 保存代码文件执行器
     * @param codeResult 代码结果
     * @param codeGenType 文件类型
     * @return 保存文件的目录
     */
    public static File executeSaver(Object codeResult, CodeGenTypeEnum codeGenType) {
        return switch (codeGenType) {
            case HTML -> htmlCodeFilerSaverTemplate.saveCode((HtmlCodeResult) codeResult);
            case MULTI_FILE -> mutieFileSaverTemplate.saveCode((MutiFileHtmlCodeResult) codeResult);
            default -> throw new BusinessException(ErrorCode.OPERATION_ERROR, "不支持生成类型" + codeGenType.getValue());
        };
    }
}
