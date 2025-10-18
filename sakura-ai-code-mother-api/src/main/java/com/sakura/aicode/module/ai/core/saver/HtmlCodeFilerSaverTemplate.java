package com.sakura.aicode.module.ai.core.saver;

import com.sakura.aicode.common.enums.CodeGenTypeEnum;
import com.sakura.aicode.module.ai.model.HtmlCodeResult;

/**
 * HTML保存
 * @author Sakura
 */
public final class HtmlCodeFilerSaverTemplate extends CodeFilerSaverTemplate<HtmlCodeResult> {

    @Override
    protected CodeGenTypeEnum getCodeType() {
        return CodeGenTypeEnum.HTML;
    }

    @Override
    protected void saveFiles(String baseDirPath, HtmlCodeResult result) {
        writeToFile(baseDirPath, "index.html", result.getHtmlCode());
    }
}
