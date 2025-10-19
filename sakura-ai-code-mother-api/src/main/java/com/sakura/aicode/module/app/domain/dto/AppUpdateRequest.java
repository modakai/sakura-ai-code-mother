package com.sakura.aicode.module.app.domain.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serializable;

@Data
public class AppUpdateRequest implements Serializable {

    /**
     * id
     */
    @NotNull(message = "应用id不能为空")
    private Long id;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 应用封面
     */
    private String cover;

    private static final long serialVersionUID = 1L;
}
