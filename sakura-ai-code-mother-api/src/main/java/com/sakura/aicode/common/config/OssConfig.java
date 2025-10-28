package com.sakura.aicode.common.config;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.auth.CredentialsProviderFactory;
import com.aliyun.oss.common.auth.DefaultCredentialProvider;
import lombok.Data;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

/**
 * Oss 配置类
 */
@Configuration
public class OssConfig {

    /**
     * 阿里云Oss服务
     * @param aliOssProperties 配置类
     * @return Oss
     */
    @Bean(destroyMethod = "shutdown")
    public OSS ossClient(OssProperties aliOssProperties) {
        String endpoint = aliOssProperties.getEndpoint();
        String region = aliOssProperties.getRegion();
        String accessKey = aliOssProperties.getAccessKey();
        String secretKey = aliOssProperties.getSecretKey();
        DefaultCredentialProvider credentialsProvider = CredentialsProviderFactory.newDefaultCredentialProvider(accessKey, secretKey);
        return OSSClientBuilder.create()
                .endpoint(endpoint)
                .credentialsProvider(credentialsProvider)
                .region(region)
                .build();
    }

    /**
     * OSS对象存储 配置属性
     *
     * @author sakura
     */
    @Data
    @Component
    @ConfigurationProperties(prefix = "oss")
    @ToString
    public static class OssProperties {
        /**
         * 访问站点
         */
        private String endpoint;

        /**
         * 自定义域名
         */
        private String domain;

        /**
         * ACCESS_KEY
         */
        private String accessKey;

        /**
         * SECRET_KEY
         */
        private String secretKey;

        /**
         * 前缀
         */
        private String prefix;

        /**
         * 存储空间名
         */
        private String bucketName;

        /**
         * 存储区域
         */
        private String region;

        /**
         * 是否https（Y=是,N=否）
         */
        private String isHttps;

        /**
         * 桶权限类型(0private 1public 2custom)
         */
        private String accessPolicy;
    }
}
