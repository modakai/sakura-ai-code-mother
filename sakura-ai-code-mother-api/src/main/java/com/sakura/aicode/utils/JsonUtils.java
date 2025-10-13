package com.sakura.aicode.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import lombok.Setter;
import lombok.SneakyThrows;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * JSON工具类，基于Jackson ObjectMapper实现
 * 提供常用的JSON序列化和反序列化方法
 */
public class JsonUtils {

    /**
     * -- GETTER --
     *  获取ObjectMapper实例（用于更复杂的操作）
     */
    @Setter
    private static ObjectMapper objectMapper;

    private JsonUtils(){}

    /**
     * 对象转JSON字符串
     */
    @SneakyThrows(JsonProcessingException.class)
    public static String toJson(Object obj) {
        return objectMapper.writeValueAsString(obj);
    }

    /**
     * 对象转JSON下划线
     */
    @SneakyThrows(JsonProcessingException.class)
    public static String toJsonSankName(Object obj) {
        ObjectMapper mapper = objectMapper.copy();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        return mapper.writeValueAsString(obj);
    }
    
    /**
     * JSON字符串转对象
     */
    @SneakyThrows
    public static <T> T fromJson(String json, Class<T> clazz) {
        return objectMapper.readValue(json, clazz);
    }

    /**
     * JSON字符串转对象
     */
    @SneakyThrows
    public static <T> T fromJsonSankName(String json, Class<T> clazz) {
        ObjectMapper mapper = objectMapper.copy();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        mapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
        return mapper.readValue(json, clazz);
    }

    /**
     * JSON字符串转对象
     */
    @SneakyThrows
    public static <T> T fromJson(InputStream json, Class<T> clazz) {
        return objectMapper.readValue(json, clazz);
    }

        /**
         * JSON字符串转对象（使用TypeReference）
         * 用法示例: fromJson(json, new TypeReference<List<String>>() {})
         */
    @SneakyThrows(JsonProcessingException.class)
    public static <T> T fromJson(String json, TypeReference<T> typeReference) {
        return objectMapper.readValue(json, typeReference);
    }

    /**
     * Map转对象
     * 用法示例: User user = JsonUtils.fromJsonMap(map, User.class)
     */
    @SneakyThrows
    public static <T> T fromJsonMap(Map<?, ?> map, Class<T> clazz) {
        return objectMapper.convertValue(map, clazz);
    }
    
    /**
     * JSON字符串转List
     */
    @SneakyThrows(JsonProcessingException.class)
    public static <T> List<T> fromJsonToList(String json, Class<T> elementClass) {
        if (!StringUtils.hasText(json)) {
            return new ArrayList<>();
        }
        CollectionType listType = objectMapper.getTypeFactory()
                .constructCollectionType(List.class, elementClass);
        return objectMapper.readValue(json, listType);
    }
    
    /**
     * JSON字符串转Map
     */
    @SneakyThrows(JsonProcessingException.class)
    public static <K, V> Map<K, V> fromJsonToMap(String json, Class<K> keyClass, Class<V> valueClass) {
        MapType mapType = objectMapper.getTypeFactory()
                .constructMapType(Map.class, keyClass, valueClass);
        return objectMapper.readValue(json, mapType);
    }
    
    /**
     * JSON字符串转Map<String, Object>
     */
    public static Map<String, Object> fromJsonToMap(String json) {
        return fromJsonToMap(json, String.class, Object.class);
    }
    
    /**
     * 格式化JSON字符串（美化输出）
     */
    @SneakyThrows(JsonProcessingException.class)
    public static String prettyJson(Object obj) {
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
    }
    
    /**
     * 检查字符串是否为有效的JSON
     */
    public static boolean isValidJson(String json) {
        try {
            objectMapper.readTree(json);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
