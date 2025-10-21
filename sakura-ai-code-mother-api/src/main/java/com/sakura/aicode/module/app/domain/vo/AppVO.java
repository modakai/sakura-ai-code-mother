package com.sakura.aicode.module.app.domain.vo;

import com.sakura.aicode.module.user.domain.vo.UserVO;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class AppVO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * 应用封面
     */
    private String cover;

    /**
     * 应用初始化提示词
     */
    private String initPrompt;

    /**
     * 部署标识
     */
    private String deployKey;

    /**
     * 生成应用的类型
     */
    private String codeGenType;

    /**
     * 优先级(99-精选 9999-置顶)
     */
    private Integer priority;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 用户信息
     */
    private UserVO user;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
