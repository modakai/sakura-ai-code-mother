package com.sakura.aicode.module.history.domain.entity;

import com.mybatisflex.annotation.Column;
import com.mybatisflex.annotation.Id;
import com.mybatisflex.annotation.KeyType;
import com.mybatisflex.annotation.Table;
import com.mybatisflex.core.keygen.KeyGenerators;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 完整对话历史表(用于加载对话记忆，包含工具调用信息) 实体类。
 *
 * @author Sakura
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("chat_history_original")
public class ChatHistoryOriginal implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @Id(keyType = KeyType.Generator, value = KeyGenerators.flexId)
    private Long id;

    /**
     * 消息
     */
    private String message;

    /**
     * U-用户，A-ai消息，TQ-工具调用请求，TE-工具调用结果
     */
    @Column("messageType")
    private String messageType;

    /**
     * 应用id
     */
    @Column("appId")
    private Long appId;

    /**
     * 创建用户id
     */
    @Column("userId")
    private Long userId;

    /**
     * 创建时间
     */
    @Column("createTime")
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    @Column("updateTime")
    private LocalDateTime updateTime;

    /**
     * 是否删除
     */
    @Column(value = "isDelete", isLogicDelete = true)
    private Integer isDelete;

}
