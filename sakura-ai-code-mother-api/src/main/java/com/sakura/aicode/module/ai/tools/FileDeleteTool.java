package com.sakura.aicode.module.ai.tools;

import com.sakura.aicode.common.constant.AiConstant;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

/**
 * 删除文件工具
 *@author Sakura
 */
@Slf4j
@Component
public class FileDeleteTool extends BaseTool {

    @Override
    public String getToolName() {
        return "delete";
    }

    @Override
    public String getDisplayName() {
        return "删除文件";
    }

    @Override
    public String generateToolExecutedResult(Map<String, String> arguments) {
        String relativePath = arguments.get("relativePath");
        return String.format("[工具调用] %s %s", getDisplayName(), relativePath);
    }


    @Tool("删除指定路径的文件")
    public String delete(@ToolMemoryId long appId, @P("要删除的文件的相对路径")String relativePath) {
        Path path = Paths.get(relativePath);
        if (!path.isAbsolute()) {
            String projectName= AiConstant.VUE_PROJECT_PATH + appId;
            // 获取到整个工程目录
            Path pathRoot = Paths.get(AiConstant.CODE_OUTPUT_ROOT_DIR, projectName);
            path =  pathRoot.resolve(relativePath);
        }
        if (!Files.exists(path)) {
            return "警告：文件不存在，无需删除 - " + relativePath;
        }
        if (!Files.isRegularFile(path)) {
            return "错误：指定路径不是文件，无法删除 - " + relativePath;
        }
        // 安全检查，防止删除重要文件
        String fileName = path.getFileName().toString();
        if (isImportantFile(fileName)) {
            return "错误：不允许删除重要文件 - "  + relativePath;
        }
        try {
            Files.delete(path);
        } catch (IOException e) {
            log.error("文件删除失败：{}", e.getMessage(), e);
            return "错误：文件删除失败 - " + relativePath;
        }
        return "文件删除成功：" + relativePath;
    }

    /**
     * 判断是否是重要文件，不允许删除
     */
    private boolean isImportantFile(String fileName) {
        String[] importantFiles = {
                "package.json", "package-lock.json", "yarn.lock", "pnpm-lock.yaml",
                "vite.config.js", "vite.config.ts", "vue.config.js",
                "tsconfig.json", "tsconfig.app.json", "tsconfig.node.json",
                "index.html", "main.js", "main.ts", "App.vue", ".gitignore", "README.md"
        };
        for (String important : importantFiles) {
            if (important.equalsIgnoreCase(fileName)) {
                return true;
            }
        }
        return false;
    }


}
