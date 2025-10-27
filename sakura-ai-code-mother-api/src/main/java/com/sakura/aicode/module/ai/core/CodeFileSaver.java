package com.sakura.aicode.module.ai.core;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.sakura.aicode.common.enums.CodeGenTypeEnum;
import com.sakura.aicode.module.ai.core.model.HtmlCodeResult;
import com.sakura.aicode.module.ai.core.model.MutiFileHtmlCodeResult;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * 保存代码文件
 * @author Sakura
 */
@Deprecated
public class CodeFileSaver {


    /**
     * 存放目录
     * 第一个占位符表示：业务类型
     * 第一个占位符表示：雪花id
     */
    private static final String TARGET_PATH = System.getProperty("user.dir") + "/tmp/code_output";


    /**
     * 保存HtmlCode 生成的结果
     * @param result AI 生成的代码结果
     * @return 返回对应保存文件的目录
     */
    public static File saveHtmlCodeResult(HtmlCodeResult result) {
        // 构建要保存的目录
        String targetPath = buildUniqueDir(CodeGenTypeEnum.HTML.getValue());
        // 保存对应的文件
        writeToFile(targetPath, "index.html", result.getHtmlCode());
        return new File(targetPath);
    }

    /**
     * 保存 MutiFileHtmlCodeResult 生成的结果
     * @param result AI 生成的代码结果
     * @return 返回对应保存文件的目录
     */
    public static File saveMutieFileCodeResult(MutiFileHtmlCodeResult result) {
        // 构建要保存的目录
        String targetPath = buildUniqueDir(CodeGenTypeEnum.HTML.getValue());
        // 保存对应的文件
        writeToFile(targetPath, "index.html", result.getHtmlCode());
        writeToFile(targetPath, "style.css", result.getCssCode());
        writeToFile(targetPath, "script.js", result.getJsCode());
        return new File(targetPath);
    }

    /**
     * 构建唯一目录路径：tmp/code_output/bizType_雪花ID
     */
    private static String buildUniqueDir(String bizType) {
        String uniqueDirName = StrUtil.format("{}_{}", bizType, IdUtil.getSnowflakeNextIdStr());
        String dirPath = TARGET_PATH + File.separator + uniqueDirName;
        FileUtil.mkdir(dirPath);
        return dirPath;
    }

    /**
     * 写入单个文件
     */
    private static void writeToFile(String dirPath, String filename, String content) {
        String filePath = dirPath + File.separator + filename;
        FileUtil.writeString(content, filePath, StandardCharsets.UTF_8);
    }
}
