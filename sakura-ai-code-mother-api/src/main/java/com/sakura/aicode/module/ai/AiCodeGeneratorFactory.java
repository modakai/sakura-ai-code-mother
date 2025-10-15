package com.sakura.aicode.module.ai;

import com.sakura.aicode.module.ai.service.AiCodeGeneratorService;
import dev.langchain4j.model.chat.ChatModel;
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

    @Bean
    public AiCodeGeneratorService aiCodeGeneratorService() {
        return AiServices.create(AiCodeGeneratorService.class, chatModel);
    }
}
