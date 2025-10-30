package com.sakura.aicode.module.ai.tools;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * 工具管理器
 * @author Sakura
 */
@Getter
@Slf4j
@Component
public class ToolFactory {

    private static final HashMap<String, BaseTool> toolMap = new HashMap<>();

    @Resource
    private BaseTool[] tools;


    @PostConstruct
    public void init() {
        for (BaseTool tool : tools) {
            toolMap.put(tool.getToolName(), tool);
            log.info("注册工具：{} -> {}", tool.getToolName(), tool.getDisplayName());
        }
        log.info("注册完成，共注册：{} 个工具", toolMap.size());
    }

    public static BaseTool getTool(String toolName) {
        return toolMap.get(toolName);
    }

}
