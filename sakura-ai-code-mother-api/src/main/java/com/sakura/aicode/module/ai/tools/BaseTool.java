package com.sakura.aicode.module.ai.tools;

import java.util.Map;

/**
 * 工具基础类
 * @author Sakura
 */
public abstract class BaseTool {

    /**
     * 工具名 也及方法名
     * @return 返回对应工具的方法名称
     */
    public abstract String getToolName();

    /**
     * 获取工具的中文名
     * @return 工具的文名
     */
    public abstract String getDisplayName();

    /**
     * 生成工具执行结果格式
     * @param arguments 工具执行参数
     * @return 格式化的工具执行结果
     */
    public abstract String generateToolExecutedResult(Map<String, String> arguments);

    /**
     * 生成工具请求时的返回值
     * @return 工具请求显示内容
     */
    public String generateToolResult() {
        return String.format("\n\n[选择工具] %s\n\n", getDisplayName());
    }

}
