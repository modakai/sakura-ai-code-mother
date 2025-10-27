package com.sakura.aicode.module.ai.core;

import com.sakura.aicode.common.ErrorCode;
import com.sakura.aicode.common.enums.CodeGenTypeEnum;
import com.sakura.aicode.exception.BusinessException;
import com.sakura.aicode.module.ai.core.model.HtmlCodeResult;
import com.sakura.aicode.module.ai.core.model.MutiFileHtmlCodeResult;
import com.sakura.aicode.module.ai.core.saver.HtmlCodeFilerSaverTemplate;
import com.sakura.aicode.module.ai.core.saver.MutieFileSaverTemplate;

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
     * @param appId 应用id
     * @return 保存文件的目录
     */
    public static File executeSaver(Object codeResult, CodeGenTypeEnum codeGenType, Long appId) {
        return switch (codeGenType) {
            case HTML -> htmlCodeFilerSaverTemplate.saveCode((HtmlCodeResult) codeResult, appId);
            case MULTI_FILE -> mutieFileSaverTemplate.saveCode((MutiFileHtmlCodeResult) codeResult, appId);
            case VUE_PROJECT -> new File("src/main/resources/prompt");
            default -> throw new BusinessException(ErrorCode.OPERATION_ERROR, "不支持生成类型" + codeGenType.getValue());
        };
    }
}
