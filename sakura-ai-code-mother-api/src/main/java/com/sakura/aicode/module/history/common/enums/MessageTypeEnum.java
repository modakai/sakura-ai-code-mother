package com.sakura.aicode.module.history.common.enums;

import cn.hutool.core.util.ObjUtil;
import lombok.Getter;

/**
 * 消息类型枚举
 */
@Getter
public enum MessageTypeEnum {
    USER("U", "用户消息"),
    AI("A", "AI消息");

    private final String value;
    private final String description;

    MessageTypeEnum(String value, String description) {
        this.value = value;
        this.description = description;
    }

    /**
     * 根据 value 获取枚举
     *
     * @param value 枚举值的value
     * @return 枚举值
     */
    public static MessageTypeEnum getEnumByValue(String value) {
        if (ObjUtil.isEmpty(value)) {
            return null;
        }
        for (MessageTypeEnum anEnum : MessageTypeEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }
}
