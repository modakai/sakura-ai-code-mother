package com.sakura.aicode.module.history.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sakura.aicode.module.user.domain.vo.UserVO;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class ChatHistoryVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 应用id
     */
    private Long appId;

    /**
     * 对话消息
     */
    private String chatMessage;

    /**
     * 消息类型(U-用户，A-AI消息)
     */
    private String messageType;

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
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

}
