package com.sakura.aicode.module.ai.core.builder;

import com.sakura.aicode.utils.RunCommandUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.concurrent.CompletableFuture;

/**
 * 构建Vue项目
 * @author Sakura
 */
@Slf4j
public class VueProjectBuilder {

    /**
     * 异步构建Vue项目
     * @param projectPath 路径
     */
    public static void buildAsync(String projectPath) {
        CompletableFuture.runAsync(() -> {
            try {
                build(projectPath);
            } catch (Exception e) {
                log.error("构建项目出现异常：{}", e.getMessage(), e);
            }
        });
    }

    /**
     * 同步构建Vue项目
     * @param projectPath 路径
     * @return 是否构建成功
     */
    public static boolean build(String projectPath) {
        File projectFile = new File(projectPath);
        if (!projectFile.exists() || !projectFile.isDirectory()) {
            log.error("项目目录不存在：{}", projectPath);
            return false;
        }
        // 检查package.json
        File packageJsonFile = new File(projectPath, "package.json");
        if (!packageJsonFile.exists() || !packageJsonFile.isFile()) {
            log.error("项目package.json不存在：{}", projectPath);
            return false;
        }
        // 执行 npm install
        if (!executeNpmInstall(projectFile)) {
            log.error("项目依赖下载失败：{}", projectPath);
            return false;
        }

        // 执行 npm run build
        if (!executeNpmBuild(projectFile)) {
            log.error("项目构建失败：{}", projectPath);
            return false;
        }
        // 检查 dist目录
        File distFile = new File(projectPath, "dist");
        if (!distFile.exists() || !distFile.isDirectory()) {
            log.error("项目构建失败：{}", projectPath);
            return false;
        }
        return true;
    }


    /**
     * 执行 npm install 命令
     */
    private static boolean executeNpmInstall(File projectDir) {
        log.info("执行 npm install...");
        String command = String.format("%s install", RunCommandUtil.buildCommand("npm"));
        return RunCommandUtil.executeCommand(projectDir, command, 300); // 5分钟超时
    }

    /**
     * 执行 npm run build 命令
     */
    private static boolean executeNpmBuild(File projectDir) {
        log.info("执行 npm run build...");
        String command = String.format("%s run build", RunCommandUtil.buildCommand("npm"));
        return RunCommandUtil.executeCommand(projectDir, command, 180); // 3分钟超时
    }
}
