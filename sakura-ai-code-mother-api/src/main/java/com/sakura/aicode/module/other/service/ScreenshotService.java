package com.sakura.aicode.module.other.service;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.sakura.aicode.common.ErrorCode;
import com.sakura.aicode.exception.BusinessException;
import com.sakura.aicode.exception.ThrowUtils;
import com.sakura.aicode.utils.WebScreenshotUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 网页截图服务
 *
 * @author Sakura
 */
@Service
@Slf4j
public class ScreenshotService {

    @Resource
    private OssService ossService;

    public String screenshotAndUpload(String url) {
        ThrowUtils.throwIf(StrUtil.isBlank(url), ErrorCode.NOT_FOUND_ERROR, "网站访问地位为空");
        // 生成截图
        String webPageScreenshot = WebScreenshotUtil.saveWebPageScreenshot(url);
        ThrowUtils.throwIf(StrUtil.isBlank(webPageScreenshot), ErrorCode.NOT_FOUND_ERROR, "本地生成截图失败");

        File uploadFile = new File(webPageScreenshot);
        ThrowUtils.throwIf(!uploadFile.exists(), ErrorCode.OPERATION_ERROR, "截图文件不存在");
        String objectName = buildOssKey(FileUtil.getName(uploadFile));
        try {
            return ossService.uploadFile(uploadFile,objectName);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传失败");
        }
    }

    private String buildOssKey(String fileName) {
        String pre = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return String.format("screenshot/%s/%s", pre, fileName);
    }
}
