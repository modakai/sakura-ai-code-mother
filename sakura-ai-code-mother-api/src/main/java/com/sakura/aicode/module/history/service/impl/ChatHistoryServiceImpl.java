package com.sakura.aicode.module.history.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sakura.aicode.common.ErrorCode;
import com.sakura.aicode.common.constant.CommonConstant;
import com.sakura.aicode.exception.BusinessException;
import com.sakura.aicode.exception.ThrowUtils;
import com.sakura.aicode.module.app.domain.entity.App;
import com.sakura.aicode.module.history.common.enums.MessageTypeEnum;
import com.sakura.aicode.module.history.domain.convert.ChatHistoryConvertMapper;
import com.sakura.aicode.module.history.domain.dto.ChatHistoryQueryRequest;
import com.sakura.aicode.module.history.domain.entity.ChatHistory;
import com.sakura.aicode.module.history.domain.vo.ChatHistoryVO;
import com.sakura.aicode.module.history.mapper.ChatHistoryMapper;
import com.sakura.aicode.module.history.service.ChatHistoryService;
import com.sakura.aicode.module.user.domain.entity.User;
import com.sakura.aicode.module.user.domain.vo.UserVO;
import com.sakura.aicode.module.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 对话历史 服务层实现。
 *
 * @author Sakura
 */
@Service
@RequiredArgsConstructor
public class ChatHistoryServiceImpl extends ServiceImpl<ChatHistoryMapper, ChatHistory> implements ChatHistoryService {

    private final UserService userService;

    @Override
    public Page<ChatHistory> listAppChatHistoryPage(App app, long pageSize, LocalDateTime lastCreateTime, Long useId) {

        ThrowUtils.throwIf(pageSize <= 0 || pageSize > 50, ErrorCode.PARAMS_ERROR, "页码大小必须在10~50之间");

        // 构建查询参数
        ChatHistoryQueryRequest queryRequest = new ChatHistoryQueryRequest();
        queryRequest.setAppId(app.getId());
        queryRequest.setUserId(useId);
        queryRequest.setLastCreateTime(lastCreateTime);

        // 查询
        return page(Page.of(1, pageSize), getQueryWrapper(queryRequest));
    }

    @Override
    public QueryWrapper getQueryWrapper(ChatHistoryQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long appId = queryRequest.getAppId();
        Long userId = queryRequest.getUserId();
        String messageType = queryRequest.getMessageType();
        LocalDateTime lastCreateTime = queryRequest.getLastCreateTime();
        String sortField = queryRequest.getSortField();
        String sortOrder = queryRequest.getSortOrder();

        if (StrUtil.isBlank(sortField)) {
            // 默认按时间排序
            sortField = "create_time";
            sortOrder = CommonConstant.SORT_ORDER_DESC;
        }

        return QueryWrapper.create()
                .eq(ChatHistory::getAppId, appId)
                .eq(ChatHistory::getUserId, userId)
                .eq(ChatHistory::getMessageType, messageType)
                .lt(ChatHistory::getCreateTime, lastCreateTime)
                .orderBy(sortField, CommonConstant.SORT_ORDER_ASC.equals(sortOrder));
    }

    @Override
    public List<ChatHistoryVO> getVoList(List<ChatHistory> chatHistories) {
        if (CollUtil.isEmpty(chatHistories)) {
            return CollUtil.empty(List.class);
        }

        Set<Long> userIds = chatHistories.stream().map(ChatHistory::getUserId).collect(Collectors.toSet());
        List<User> users = userService.listByIds(userIds);
        if (CollUtil.isEmpty(users)) {
            return ChatHistoryConvertMapper.INSTANCE.toVoList(chatHistories);
        }
        Map<Long, UserVO> userVOMap = users.stream().collect(Collectors.toMap(User::getId, userService::getUserVo));

        List<ChatHistoryVO> voList = ChatHistoryConvertMapper.INSTANCE.toVoList(chatHistories);
        for (ChatHistoryVO chatHistoryVO : voList) {
            chatHistoryVO.setUser(userVOMap.get(chatHistoryVO.getUserId()));
        }
        return voList;
    }

    @Override
    public void saveUserMessage(Long appId, String message, Long userId) {
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setAppId(appId);
        chatHistory.setChatMessage(message);
        chatHistory.setUserId(userId);
        chatHistory.setMessageType(MessageTypeEnum.USER.getValue());
        saveMessage(chatHistory);
    }

    @Override
    public void saveAiMessage(Long appId, String message, Long userId) {
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setAppId(appId);
        chatHistory.setChatMessage(message);
        chatHistory.setUserId(userId);
        chatHistory.setMessageType(MessageTypeEnum.AI.getValue());
        saveMessage(chatHistory);
    }

    @Override
    public void removeByAppId(Long appId) {
        ThrowUtils.throwIfId(appId, ErrorCode.PARAMS_ERROR, "应用id生成错误");
        remove(QueryWrapper.create().eq(ChatHistory::getAppId, appId));
    }

    public void saveMessage(ChatHistory chatHistory) {
        Long appId = chatHistory.getAppId();
        String chatMessage = chatHistory.getChatMessage();
        String messageType = chatHistory.getMessageType();
        Long userId = chatHistory.getUserId();

        ThrowUtils.throwIfId(appId, ErrorCode.PARAMS_ERROR, "应用id为空");
        ThrowUtils.throwIfId(userId, ErrorCode.PARAMS_ERROR, "应用id为空");
        ThrowUtils.throwIf(StrUtil.isBlank(chatMessage), ErrorCode.PARAMS_ERROR, "消息为空");
        ThrowUtils.throwIf(StrUtil.isBlank(messageType), ErrorCode.PARAMS_ERROR, "消息类型为空");

        MessageTypeEnum enumByValue = MessageTypeEnum.getEnumByValue(messageType);
        if (enumByValue == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "消息类型错误");
        }
        this.save(chatHistory);
    }
}
