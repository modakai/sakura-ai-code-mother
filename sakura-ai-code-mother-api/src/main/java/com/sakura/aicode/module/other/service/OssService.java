package com.sakura.aicode.module.other.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.sakura.aicode.common.config.OssConfig;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
public class OssService {

    @Resource
    private OSS ossClient;

    @Resource
    private OssConfig.OssProperties ossProperties;

    /**
     * 通用上传方法：上传文件并返回可访问的 URL
     * @param uploadFile 要上传的文件
     * @param objectName 上传到 OSS 的对象名（如：images/2025/10/avatar.png）
     * @return 可公开访问的 URL（HTTPS）
     */
    public String uploadFile(File uploadFile, String objectName) {
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(ossProperties.getBucketName(), objectName, uploadFile);
            // 执行上传
            return upload(objectName, putObjectRequest);
        } catch (Exception e) {
            log.error("OSS 文件上传失败，objectName: {}, 错误: {}", objectName, e.getMessage(), e);
            throw new RuntimeException("文件上传失败", e);
        }
    }

    /**
     * 通用上传方法：上传文件并返回可访问的 URL
     *
     * @param file       要上传的文件（Spring 的 MultipartFile）
     * @param objectName 上传到 OSS 的对象名（如：images/2025/10/avatar.png）
     * @return 可公开访问的 URL（HTTPS）
     * @throws IOException 上传过程中可能抛出的 IO 异常
     */
    public String uploadFile(MultipartFile file, String objectName) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            // 构造上传请求
            PutObjectRequest putObjectRequest = new PutObjectRequest(ossProperties.getBucketName(), objectName, inputStream);
            // 执行上传
            return upload(objectName, putObjectRequest);
        } catch (Exception e) {
            log.error("OSS 文件上传失败，objectName: {}, 错误: {}", objectName, e.getMessage(), e);
            throw new RuntimeException("文件上传失败", e);
        }
    }

    /**
     * 重载方法：支持直接传入 InputStream 和 contentType（可选）
     */
    public String uploadFile(InputStream inputStream, String objectName, String contentType) {
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(ossProperties.getBucketName(), objectName, inputStream);
            if (contentType != null && !contentType.isEmpty()) {
                ObjectMetadata objectMetadata = new ObjectMetadata();
                objectMetadata.setContentType(contentType);
                putObjectRequest.setMetadata(objectMetadata);
            }
            return upload(objectName, putObjectRequest);
        } catch (Exception e) {
            log.error("OSS 文件上传失败，objectName: {}, 错误: {}", objectName, e.getMessage(), e);
            throw new RuntimeException("文件上传失败", e);
        }
    }

    private String upload(String objectName, PutObjectRequest putObjectRequest) {
        ossClient.putObject(putObjectRequest);
        String url = String.format("https://%s.%s/%s", ossProperties.getBucketName(), ossProperties.getEndpoint(), objectName);
        log.info("文件上传成功，访问地址: {}", url);
        return url;
    }
}
