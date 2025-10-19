package com.sakura.aicode.module.auth.domain.vo;

import com.sakura.aicode.common.constant.UserConstant;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class LoginUserVO implements Serializable {

    /**
     * 用户 id
     */
    private Long id;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 用户昵称
     */
    private String userName;

    /**
     * 用户头像
     */
    private String userAvatar;

    /**
     * 用户简介
     */
    private String userProfile;

    /**
     * 用户角色：user/admin
     */
    private String userRole;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 判断用户是否为管理员
     * @return 是：true 反之 false
     */
    public boolean isAdmin() {
        return UserConstant.ADMIN_ROLE.equals(userRole);
    }
}
