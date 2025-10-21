package com.sakura.aicode.module.ai.core.saver;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.sakura.aicode.common.ErrorCode;
import com.sakura.aicode.common.constant.AiConstant;
import com.sakura.aicode.common.enums.CodeGenTypeEnum;
import com.sakura.aicode.exception.BusinessException;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * 保存代码 模板方法
 * @author Sakura
 */
public abstract sealed class CodeFilerSaverTemplate<T>
        permits HtmlCodeFilerSaverTemplate, MutieFileSaverTemplate {

    /**
     * 存放目录
     * 第一个占位符表示：业务类型
     * 第一个占位符表示：雪花id
     */
    private static final String TARGET_PATH = AiConstant.TARGET_PATH;

    /**
     * 模板方法：保存代码的流程
     *
     * @param result ai 响应结果
     * @param appId 应用id
     * @return 保存代码的文件目录
     */
    public final File saveCode(T result, Long appId) {
        // 1. 验证参数
        validateInput(result);
        // 2. 生成目标目录
        String baseDirPath = buildUniqueDir(appId);
        // 3. 保存文件
        saveFiles(baseDirPath, result);
        // 4. 返回结果
        return new File(baseDirPath);
    }

    /**
     *  可以由子类覆盖对应的校验
     * @param result 代码结果
     */
    protected void validateInput(T result) {
        if (result == null) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "代码结果为空");
        }
    }


    /**
     * 构建唯一目录路径：tmp/code_output/bizType_appId
     */
    private String buildUniqueDir(Long appId) {
        String bizType = getCodeType().getValue();
        String uniqueDirName = StrUtil.format("{}_{}", bizType, appId);
        String dirPath = TARGET_PATH + File.separator + uniqueDirName;
        FileUtil.mkdir(dirPath);
        return dirPath;
    }

    /**
     * 写入文件
     */
    protected final void writeToFile(String dirPath, String filename, String content) {
        if (StrUtil.isBlank(content)) return;
        String filePath = dirPath + File.separator + filename;
        FileUtil.writeString(content, filePath, StandardCharsets.UTF_8);
    }

    /**
     * 获取保存代码的类型 子类实现
     * @return 文件保存类型
     */
    protected abstract CodeGenTypeEnum getCodeType();

    /**
     * 写入文件 具体写入多少个文件 由子类实现
     * @param baseDirPath 路径
     */
    protected abstract void saveFiles(String baseDirPath, T result);
}
