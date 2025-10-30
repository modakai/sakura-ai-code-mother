package com.sakura.aicode.common.constant;

import java.util.Set;

/**
 * @author Sakura
 */
public interface AiConstant {

    /**
     * 应用生成目录
     */
    String CODE_OUTPUT_ROOT_DIR = System.getProperty("user.dir") + "/tmp/code_output";

    /**
     * 应用部署域名
     */
    String CODE_DEPLOY_HOST = "http://localhost";


    String VUE_PROJECT_PATH = "vue_project_";

    /**
     * 需要过滤的文件和目录名称
     */
     Set<String> IGNORED_NAMES = Set.of(
            "node_modules",
            ".git",
            "dist",
            "build",
            ".DS_Store",
            ".env",
            "target",
            ".mvn",
            ".idea",
            ".vscode"
    );

    /**
     * 需要过滤的文件扩展名
     */
    Set<String> IGNORED_EXTENSIONS = Set.of(
            ".log",
            ".tmp",
            ".cache"
    );
}
