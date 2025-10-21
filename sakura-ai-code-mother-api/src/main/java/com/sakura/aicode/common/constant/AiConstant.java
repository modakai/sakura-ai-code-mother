package com.sakura.aicode.common.constant;

/**
 * @author Sakura
 */
public interface AiConstant {

    /**
     * 存放目录
     * 第一个占位符表示：业务类型
     * 第一个占位符表示：雪花id
     */
     static final String TARGET_PATH = System.getProperty("user.dir") + "/tmp/code_output";
}
