package com.sakura.aicode.module.ai.facade;

import cn.hutool.core.util.StrUtil;
import com.sakura.aicode.common.ErrorCode;
import com.sakura.aicode.common.enums.CodeGenTypeEnum;
import com.sakura.aicode.exception.BusinessException;
import com.sakura.aicode.module.ai.core.CodeFileSaver;
import com.sakura.aicode.module.ai.core.CodeParser;
import com.sakura.aicode.module.ai.model.HtmlCodeResult;
import com.sakura.aicode.module.ai.model.MutiFileHtmlCodeResult;
import com.sakura.aicode.module.ai.service.AiCodeGeneratorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;

/**
 * Ai生成代码门面 门面模块
 *    隐藏内部调用 AI 生成代码 -》并根据不同的模式保存代码的流程
 * @author Sakura
 */
@Service
@RequiredArgsConstructor
@Slf4j(topic = "ai.code.generator")
public class AiCodeGeneratorFacade {

    private final AiCodeGeneratorService  aiCodeGeneratorService;

    /**
     * 统一入口：根据类型生成并保存对应的代码
     * @param userMessage 用户提示词
     * @param codeGenTypeEnum 代码生成的类型
     * @return 返回生成文件的目录
     */
    public File generatorCodeAndSave(String userMessage, CodeGenTypeEnum codeGenTypeEnum) {
        if (StrUtil.isBlank(userMessage)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "prompt为空");
        }
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "代码生成类型为空");
        }
        return switch (codeGenTypeEnum) {
            // 单HTML
            case HTML -> generatorHtmlCodeAndSave(userMessage);
            // 原生 HTML css js
            case MULTI_FILE -> generatorMultiFileCodeAndSave(userMessage);
            default -> {
                String error = "不支持生成类型：" + codeGenTypeEnum.getValue();
                throw new BusinessException(ErrorCode.PARAMS_ERROR, error);
            }
        };
    }

    /**
     * 统一入口(流式)：根据类型生成并保存对应的代码
     * @param userMessage 用户提示词
     * @param codeGenTypeEnum 代码生成的类型
     * @return 返回生成文件的目录
     */
    public Flux<String> generatorCodeAndSaveWithStream(String userMessage, CodeGenTypeEnum codeGenTypeEnum) {
        if (StrUtil.isBlank(userMessage)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "prompt为空");
        }
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "代码生成类型为空");
        }
        return switch (codeGenTypeEnum) {
            // 单HTML
            case HTML -> generatorHtmlCodeAndSaveWithStream(userMessage);
            // 原生 HTML css js
            case MULTI_FILE -> generatorMultiFileCodeAndSaveWithStream(userMessage);
            default -> {
                String error = "不支持生成类型：" + codeGenTypeEnum.getValue();
                throw new BusinessException(ErrorCode.PARAMS_ERROR, error);
            }
        };
    }

    /**
     * 保存多个原生文件
     * @param userMessage prompt
     * @return 保存的目录
     */
    private File generatorMultiFileCodeAndSave(String userMessage) {
        MutiFileHtmlCodeResult htmlCodeResult = aiCodeGeneratorService.generateMultiFileHtmlCode(userMessage);
        return CodeFileSaver.saveMutieFileCodeResult(htmlCodeResult);
    }

    /**
     * 保存单HTML文件
     * @param userMessage prompt
     * @return 保存的目录
     */
    private File generatorHtmlCodeAndSave(String userMessage) {
        HtmlCodeResult result = aiCodeGeneratorService.generateHtmlCode(userMessage);
        return CodeFileSaver.saveHtmlCodeResult(result);
    }

    /**
     * 保存单HTML文件 (stream流)
     * @param userMessage prompt
     * @return 保存的目录
     */
    private Flux<String> generatorHtmlCodeAndSaveWithStream(String userMessage) {
        Flux<String> result = aiCodeGeneratorService.generateHtmlCodeStream(userMessage);
        StringBuilder codeBuilder = new StringBuilder();
        return result.
                doOnNext(chunk -> {
                    // 实时收集代码片段
                    codeBuilder.append(chunk);
                })
                .doOnComplete(() -> {
                    // 流式返回完成保存代码
                    String codeContent = codeBuilder.toString();
                    HtmlCodeResult htmlCodeResult = CodeParser.parserHtmlCode(codeContent);
                    File file = CodeFileSaver.saveHtmlCodeResult(htmlCodeResult);
                    log.info("保存成功：路径为：{}", file.getAbsolutePath());
                });
    }

    /**
     * 保存多文件文件 (stream流)
     * @param userMessage prompt
     * @return 保存的目录
     */
    private Flux<String> generatorMultiFileCodeAndSaveWithStream(String userMessage) {
        Flux<String> result = aiCodeGeneratorService.generateMultiFileCodeStream(userMessage);
        StringBuilder codeBuilder = new StringBuilder();
        // 实时收集代码片段
        return result.
                doOnNext(codeBuilder::append)
                .doOnComplete(() -> {
                    // 流式返回完成保存代码
                    String codeContent = codeBuilder.toString();
                    MutiFileHtmlCodeResult mutiFileHtmlCodeResult = CodeParser.parserMultiFileCode(codeContent);
                    File file = CodeFileSaver.saveMutieFileCodeResult(mutiFileHtmlCodeResult);
                    log.info("保存成功：路径为：{}", file.getAbsolutePath());
                });
    }

}
