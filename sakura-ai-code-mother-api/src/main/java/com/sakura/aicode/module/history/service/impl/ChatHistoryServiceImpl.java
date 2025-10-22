package com.sakura.aicode.module.history.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sakura.aicode.module.history.domain.entity.ChatHistory;
import com.sakura.aicode.module.history.mapper.ChatHistoryMapper;
import com.sakura.aicode.module.history.service.ChatHistoryService;
import org.springframework.stereotype.Service;

/**
 * 对话历史 服务层实现。
 *
 * @author Sakura
 */
@Service
public class ChatHistoryServiceImpl extends ServiceImpl<ChatHistoryMapper, ChatHistory>  implements ChatHistoryService{

}
