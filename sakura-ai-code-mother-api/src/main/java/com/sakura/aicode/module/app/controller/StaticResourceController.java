package com.sakura.aicode.module.app.controller;

import com.sakura.aicode.common.constant.AiConstant;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import java.io.File;

/**
 * 静态资源映射 (预览操作)
 *
 * @author Sakura
 */
@RestController
@RequestMapping("/static")
public class StaticResourceController {

    @GetMapping("/{deployKey}/**")
    public ResponseEntity<Resource> previewApp(@PathVariable String deployKey,
                                               HttpServletRequest request) {
        // 获取资源路径
        String resourcePath = (String) request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE);
        resourcePath = resourcePath.substring(("/static/" + deployKey).length());
        // 如果是目录访问（不带斜杠）重定向到带斜杠的URL
        if (resourcePath.isEmpty()) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Location", request.getRequestURI() + "/");
            return new ResponseEntity<>(httpHeaders,  HttpStatus.MOVED_PERMANENTLY);
        }
        // 如果 resourcePath为 / 默认返回 index
        if ("/".equals(resourcePath)) {
            resourcePath = "/index.html";
        }
        // 构建文件目录
        String filePath = AiConstant.CODE_OUTPUT_ROOT_DIR + "/" + deployKey + resourcePath;
        File file = new File(filePath);
        // 检查文件是否存在
        if (!file.exists()) {
            return ResponseEntity.notFound().build();
        }
        // 返回资源文件
        FileSystemResource resource = new FileSystemResource(file);
        return ResponseEntity.ok()
                .header("Content-Type", getContentTypeWithCharset(filePath))
                .body(resource);
    }

    private String getContentTypeWithCharset(String filePath) {
        // 截取 .后面的数
        filePath = filePath.substring(filePath.lastIndexOf("."));
        return switch (filePath) {
            case ".html" -> "text/html;charset=utf-8";
            case ".css" -> "text/css;charset=utf-8";
            case ".js" -> "application/javascript;charset=utf-8";
            case ".png" -> "image/png";
            case ".jpg" -> "image/jpeg";
            default -> "application/octet-stream";
        };
    }
}
