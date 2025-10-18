package com.sakura.aicode.module.ai.core.saver;

import com.sakura.aicode.common.enums.CodeGenTypeEnum;
import com.sakura.aicode.module.ai.model.MutiFileHtmlCodeResult;

/**
 * 原生多文件保存
 * @author Sakura
 */
public final class MutieFileSaverTemplate extends CodeFilerSaverTemplate<MutiFileHtmlCodeResult>{
    @Override
    protected CodeGenTypeEnum getCodeType() {
        return CodeGenTypeEnum.MULTI_FILE;
    }

    @Override
    protected void saveFiles(String baseDirPath, MutiFileHtmlCodeResult result) {
        // 保存对应的文件
        writeToFile(baseDirPath, "index.html", result.getHtmlCode());
        writeToFile(baseDirPath, "style.css", result.getCssCode());
        writeToFile(baseDirPath, "script.js", result.getJsCode());
    }
}
