package com.sakura.aicode.module.history.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.sakura.aicode.module.history.domain.dto.ChatHistoryQueryRequest;
import com.sakura.aicode.module.history.domain.entity.ChatHistory;
import com.sakura.aicode.module.history.domain.vo.ChatHistoryVO;

import java.util.List;

/**
 * 对话历史 服务层。
 *
 * @author Sakura
 */
public interface ChatHistoryService extends IService<ChatHistory> {

    QueryWrapper getQueryWrapper(ChatHistoryQueryRequest queryRequest);

    List<ChatHistoryVO> getVoList(List<ChatHistory> chatHistories);

    void saveMessage(ChatHistory chatHistory);

    void saveUserMessage(Long appId, String message, Long userId);

    void saveAiMessage(Long appId, String message, Long userId);

    void removeByAppId(Long appId);
}
