package com.sakura.aicode.module.app.domain.dto;

import com.sakura.aicode.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
public class AppQueryRequest extends PageRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 优先级(99-精选 9999-置顶)
     */
    private Integer priority;

    /**
     * 代码生成类型
     */
    private String codeGenType;

    private static final long serialVersionUID = 1L;
}
