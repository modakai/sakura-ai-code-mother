package com.sakura.aicode.module.history.domain.dto;

import com.sakura.aicode.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 对话历史查询 （ChatHistoryQueryRequest）
 * @author Sakura
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ChatHistoryQueryRequest extends PageRequest implements Serializable {

    /**
     * 消息id
     */
    private Long id;

    /**
     * 应用id
     */
    private Long appId;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 消息类型(U-用户，A-AI消息)
     */
    private String messageType;

    /**
     * 游标查询 - 最后一条记录的创建时间
     * 用于游标分页，获取早于当前时间的记录
     */
    private LocalDateTime lastCreateTime;

    private static final long serialVersionUID = 1L;
}
