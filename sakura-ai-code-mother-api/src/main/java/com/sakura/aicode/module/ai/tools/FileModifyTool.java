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
import java.nio.file.StandardOpenOption;
import java.util.Map;

/**
 * 文件修改工具
 * @author Sakura
 */
@Slf4j
@Component
public class FileModifyTool extends BaseTool {

    @Tool("修改文件内容， 用新内容替换指定的旧内容")
    public String modify(@ToolMemoryId long appId,
                         @P("文件的相对路径")String relativePath,
                         @P("要替换的旧内容")String oldContent,
                         @P("要修改的内容")String newContent) {
        Path path = Paths.get(relativePath);
        if (!path.isAbsolute()) {
            String projectName= AiConstant.VUE_PROJECT_PATH + appId;
            // 获取到整个工程目录
            path = Paths.get(AiConstant.CODE_OUTPUT_ROOT_DIR, projectName);
        }
        if (!Files.exists(path) || !Files.isRegularFile(path)) {
            return "错误：文件不存在或不是文件 - " + relativePath;
        }
        try {
            // 获取旧文件内容
            String originalContent = Files.readString(path);
            if (!originalContent.contains(oldContent)) {
                return "警告：文件中未找到要替换的内容，文件未修改 - " + relativePath;
            }
            String modifyContent = originalContent.replace(oldContent, newContent);
            if (originalContent.equals(modifyContent)) {
                return "信息：替换后文件内容未发生改变 - " + relativePath;
            }
            Files.writeString(path, modifyContent, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            log.error("文件修改失败：{}",e.getMessage(), e);
            return "错误：文件修改失败：%s 错误：%s".formatted(relativePath, e.getMessage());
        }

        return "文件修改完成 - " + relativePath;
    }

    @Override
    public String getToolName() {
        return "modifyFile";
    }

    @Override
    public String getDisplayName() {
        return "修改文件";
    }

    @Override
    public String generateToolExecutedResult(Map<String, String> arguments) {
        String relativeFilePath = arguments.get("relativeFilePath");
        String oldContent = arguments.get("oldContent");
        String newContent = arguments.get("newContent");
        // 显示对比内容
        return String.format("""
                [工具调用] %s %s
                
                替换前：
                ```
                %s
                ```
                
                替换后：
                ```
                %s
                ```
                """, getDisplayName(), relativeFilePath, oldContent, newContent);
    }
}
