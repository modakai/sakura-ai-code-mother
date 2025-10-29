package com.sakura.aicode.module.ai;

import com.sakura.aicode.module.ai.service.AiCodeGenTypeRoutingService;
import com.sakura.aicode.module.ai.service.AiCodeGeneratorService;
import com.sakura.aicode.module.ai.tools.FileWriteTool;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * lang chain 4j 创建 AiService工厂类
 * @author Sakura
 */
@Configuration
public class AiCodeGeneratorFactory {

    @Resource
    private ChatModel chatModel;
    @Resource
    private StreamingChatModel openAiStreamingChatModel;
    @Resource
    private RedisChatMemoryStore redisChatMemoryStore;

    @Bean
    public AiCodeGeneratorService aiCodeGeneratorService() {
        return AiServices.builder(AiCodeGeneratorService.class)
                .chatModel(chatModel)
                .streamingChatModel(openAiStreamingChatModel)
                .tools(new FileWriteTool())
                .hallucinatedToolNameStrategy(toolExecutionRequest -> ToolExecutionResultMessage.from(
                        toolExecutionRequest, "Error: there is not tool called" + toolExecutionRequest.name()
                ))
                .chatMemoryProvider(memoryId -> MessageWindowChatMemory.builder()
                        .id(memoryId)
                        .chatMemoryStore(redisChatMemoryStore)
                        .maxMessages(9999)
                        .build())
                .build();
    }

    @Bean
    public AiCodeGenTypeRoutingService aiCodeGenTypeRoutingService() {
        return AiServices.builder(AiCodeGenTypeRoutingService.class)
                .chatModel(chatModel)
                .build();
    }
}
