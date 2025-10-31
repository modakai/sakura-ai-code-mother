package com.sakura.aicode.module.history.domain.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.sakura.aicode.common.handler.ToolRequestMessageListTypeHandler;
import com.sakura.aicode.module.ai.core.model.message.ToolRequestMessage;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 对话历史 实体类。
 *
 * @author Sakura
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("chat_history")
public class ChatHistory implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Id(keyType = KeyType.Auto)
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
     * 工具调用信息
     */
    @Column(value = "tool_request_message", typeHandler = ToolRequestMessageListTypeHandler.class)
    private List<ToolRequestMessage> toolRequestMessage;

    /**
     * 用户id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    private Integer isDelete;

}
