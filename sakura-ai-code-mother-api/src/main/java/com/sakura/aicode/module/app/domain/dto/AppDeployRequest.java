package com.sakura.aicode.module.app.domain.dto;


import lombok.Data;

import java.io.Serializable;

/**
 * App 部署请求
 * @author Sakura
 */
@Data
public class AppDeployRequest implements Serializable {

    /**
     * 应用id
     */
    private Long appId;
}
