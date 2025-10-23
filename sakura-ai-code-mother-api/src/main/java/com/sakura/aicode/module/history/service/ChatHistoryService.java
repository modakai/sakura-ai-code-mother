package com.sakura.aicode.module.history.service;

import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.sakura.aicode.module.app.domain.entity.App;
import com.sakura.aicode.module.history.domain.dto.ChatHistoryQueryRequest;
import com.sakura.aicode.module.history.domain.entity.ChatHistory;
import com.sakura.aicode.module.history.domain.vo.ChatHistoryVO;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 对话历史 服务层。
 *
 * @author Sakura
 */
public interface ChatHistoryService extends IService<ChatHistory> {

    /**
     * 加载对应应用的 消息
     * @param appId 应用id
     * @param userId 用户id
     */
    void loadChatMemoryMessage(long appId, long userId, int maxCount);

    /**
     * 游标分页
     * @param app 应用
     * @param pageSize 分页数量
     * @param lastCreateTime 游标
     * @param useId 用户
     * @return 分页结果
     */
    Page<ChatHistory> listAppChatHistoryPage(App app, long pageSize,
                                             LocalDateTime lastCreateTime,
                                             Long useId);

    QueryWrapper getQueryWrapper(ChatHistoryQueryRequest queryRequest);

    List<ChatHistoryVO> getVoList(List<ChatHistory> chatHistories);

    void saveMessage(ChatHistory chatHistory);

    void saveUserMessage(Long appId, String message, Long userId);

    void saveAiMessage(Long appId, String message, Long userId);

    void removeByAppId(Long appId);
}
