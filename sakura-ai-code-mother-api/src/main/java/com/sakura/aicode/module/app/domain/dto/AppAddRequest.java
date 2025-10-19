package com.sakura.aicode.module.app.domain.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
public class AppAddRequest implements Serializable {

    /**
     * 应用名称
     */
//    @NotBlank(message = "应用名称不能为空")
    private String appName;

    /**
     * 应用封面
     */
    private String cover;

    /**
     * 应用初始化提示词（必填）
     */
    @NotBlank(message = "初始化提示词不能为空")
    private String initPrompt;

    /**
     * 生成应用的类型
     */
    private String codeGenType;

    @Serial
    private static final long serialVersionUID = 1L;
}
