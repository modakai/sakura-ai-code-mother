package com.sakura.aicode.module.history.domain.convert;

import com.sakura.aicode.module.history.domain.entity.ChatHistory;
import com.sakura.aicode.module.history.domain.vo.ChatHistoryVO;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ChatHistoryConvertMapper {

    ChatHistoryConvertMapper INSTANCE = Mappers.getMapper(ChatHistoryConvertMapper.class);

    ChatHistoryVO toVo(ChatHistory chatHistory);

    List<ChatHistoryVO> toVoList(List<ChatHistory> chatHistories);
}
