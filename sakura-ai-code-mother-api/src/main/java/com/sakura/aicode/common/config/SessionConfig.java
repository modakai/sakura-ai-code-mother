package com.sakura.aicode.common.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.DeserializationConfig;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
@EnableRedisHttpSession(redisNamespace = "sakura:app:login")
public class SessionConfig {
	// 定义统一的时间格式
	private static final String DATE_TIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(DATE_TIME_PATTERN);

	@Bean
	public GenericJackson2JsonRedisSerializer springSessionDefaultRedisSerializer() {
		ObjectMapper mapper = new ObjectMapper();

		JavaTimeModule javaTimeModule = new JavaTimeModule();

		// 序列化：LocalDateTime -> String
		javaTimeModule.addSerializer(LocalDateTime.class, new LocalDateTimeSerializer(FORMATTER));

		// 反序列化：String -> LocalDateTime（关键！）
		javaTimeModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer(FORMATTER));

		mapper.registerModule(javaTimeModule);

		// 启用类型信息（用于反序列化对象）
		mapper.activateDefaultTyping(
				mapper.getPolymorphicTypeValidator(),
				ObjectMapper.DefaultTyping.NON_FINAL,
				JsonTypeInfo.As.PROPERTY
		);
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		DeserializationConfig deserializationConfig = mapper.getDeserializationConfig();
		deserializationConfig.withoutFeatures(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);

		return new GenericJackson2JsonRedisSerializer(mapper);
	}

}
