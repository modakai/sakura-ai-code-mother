package com.sakura.aicode.module.app.controller;

import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.paginate.Page;
import com.sakura.aicode.common.BaseResponse;
import com.sakura.aicode.common.DeleteRequest;
import com.sakura.aicode.common.ErrorCode;
import com.sakura.aicode.common.ResultUtils;
import com.sakura.aicode.common.annotation.AuthCheck;
import com.sakura.aicode.common.constant.UserConstant;
import com.sakura.aicode.common.enums.CodeGenTypeEnum;
import com.sakura.aicode.exception.BusinessException;
import com.sakura.aicode.exception.ThrowUtils;
import com.sakura.aicode.module.app.domain.convert.AppConvertMapper;
import com.sakura.aicode.module.app.domain.dto.AppAddRequest;
import com.sakura.aicode.module.app.domain.dto.AppDeployRequest;
import com.sakura.aicode.module.app.domain.dto.AppQueryRequest;
import com.sakura.aicode.module.app.domain.dto.AppUpdateRequest;
import com.sakura.aicode.module.app.domain.entity.App;
import com.sakura.aicode.module.app.domain.vo.AppVO;
import com.sakura.aicode.module.app.service.AppService;
import com.sakura.aicode.module.auth.domain.vo.LoginUserVO;
import com.sakura.aicode.module.auth.service.AuthService;
import com.sakura.aicode.utils.JsonUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

/**
 * 应用 控制层。
 *
 */
@RestController
@RequestMapping("/app")
@RequiredArgsConstructor
public class AppController {

    private final AppService appService;
    private final AuthService authService;

    /**
     * 部署应用
     * @return 返回部署成功的应用访问路径
     */
    @PostMapping("/deploy")
    public BaseResponse<String> deployApp(@RequestBody AppDeployRequest appDeployRequest,
                                          HttpServletRequest request) {
        Long appId = appDeployRequest.getAppId();
        if (appId == null || appId <= 0) {
            return ResultUtils.error(ErrorCode.PARAMS_ERROR, "请设置应用id");
        }
        LoginUserVO loginInfo = authService.getLoginInfo(request);
        String url = appService.deployApp(appId, loginInfo);
        appService.genCover(url, appId);
        return ResultUtils.success(url);
    }

    /**
     * 流式响应：ai生成的对话内容
     * @param appId 应用id
     * @param message 用户消息
     * @param request 请求
     * @return 流式响应
     */
    @GetMapping(value = "/chat/gen/code", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> chatGenCode(@RequestParam Long appId,
                                                     @RequestParam String message,
                                                     HttpServletRequest request) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "appId错误");
        ThrowUtils.throwIf(StrUtil.isBlank(message), ErrorCode.PARAMS_ERROR, "提示词为空");

        LoginUserVO loginUser = authService.getLoginInfo(request);
        Flux<String> chatToCode = appService.chatToCode(appId, message, loginUser);
        return chatToCode
                .map(chunk -> {
                    Map<String, String> wrapper = Map.of("d", chunk);
                    String d = JsonUtils.toJson(wrapper);
                    return ServerSentEvent.<String>builder()
                            .data(d)
                            .build();
                })
                .concatWith(Mono.just(
                        ServerSentEvent.<String>builder()
                                .event("done")
                                .data("")
                                .build())
                );
    }

    // region 业务代码

    /**
     * 创建应用
     *
     * @param addRequest 创建请求
     * @param request HttpServletRequest
     * @return 新应用 id
     */
    @PostMapping("/add")
    public BaseResponse<Long> addApp(@RequestBody @Validated AppAddRequest addRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(addRequest == null, ErrorCode.PARAMS_ERROR);

        String initPrompt = addRequest.getInitPrompt();

        LoginUserVO loginUser = authService.getLoginInfo(request);
        App app = AppConvertMapper.INSTANCE.toEntity(addRequest);
        app.setUserId(loginUser.getId());

        // todo 调用AI，让Ai根据用户给的提示词生成对应的AppName和描述
        app.setAppName(initPrompt.substring(0, Math.min(initPrompt.length(), 12)));
        app.setCodeGenType(CodeGenTypeEnum.VUE_PROJECT.getValue());
        app.setCover("https://www.mianshiya.com/logo.png");

        boolean result = appService.save(app);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(app.getId());
    }

    /**
     * 根据 id 修改自己的应用
     *
     * @param updateRequest 更新请求
     * @param request HttpServletRequest
     * @return 是否成功
     */
    @PostMapping("/update/my")
    public BaseResponse<Boolean> updateMyApp(@RequestBody @Validated AppUpdateRequest updateRequest, HttpServletRequest request) {
        if (updateRequest == null || updateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        LoginUserVO loginUser = authService.getLoginInfo(request);
        App app = appService.getById(updateRequest.getId());

        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);
        ThrowUtils.throwIf(!app.getUserId().equals(loginUser.getId()), ErrorCode.NO_AUTH_ERROR);

        App update = AppConvertMapper.INSTANCE.toEntity(updateRequest);
        boolean result = appService.updateById(update);

        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);

        return ResultUtils.success(true);
    }

    /**
     * 根据 id 删除应用
     *
     * @param deleteRequest 删除请求
     * @param request HttpServletRequest
     * @return 是否成功
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteMyApp(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        LoginUserVO loginUser = authService.getLoginInfo(request);
        App app = appService.getById(deleteRequest.getId());

        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);
        // 判断是否是自己或管理员
        ThrowUtils.throwIf(!app.getUserId().equals(loginUser.getId())
                && !loginUser.checkAdmin(), ErrorCode.NO_AUTH_ERROR);

        boolean b = appService.removeById(deleteRequest.getId());
        return ResultUtils.success(b);
    }

    /**
     * 根据 id 获取应用详情
     *
     * @param id      应用 id
     * @return 应用详情
     */
    @GetMapping("/get/vo")
    public BaseResponse<AppVO> getAppVOById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        App app = appService.getById(id);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类（包含用户信息）
        return ResultUtils.success(appService.getVo(app));
    }

    /**
     * 分页查询自己的应用列表（pageSize = 20）
     *
     * @param queryRequest 查询参数
     * @param request HttpServletRequest
     * @return 分页后的应用 VO 列表 {@link Page<AppVO>}
     */
    @PostMapping("/list/page/my")
    public BaseResponse<Page<AppVO>> listMyAppByPage(@RequestBody AppQueryRequest queryRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(queryRequest == null, ErrorCode.PARAMS_ERROR);
        LoginUserVO loginUser = authService.getLoginInfo(request);
        long pageNum = queryRequest.getCurrent();
        long pageSize = 20;
        Page<App> page = appService.page(Page.of(pageNum, pageSize), appService.getQueryWrapper(queryRequest).eq(App::getUserId, loginUser.getId()));
        Page<AppVO> voPage = new Page<>(pageNum, pageSize, page.getTotalRow());
        List<AppVO> voList = appService.getVoList(page.getRecords());
        voPage.setRecords(voList);
        return ResultUtils.success(voPage);
    }

    /**
     * 分页查询系统精选应用列表（pageSize = 20，priority = 99）
     *
     * @param queryRequest 查询参数
     * @return 分页后的应用 VO 列表 {@link Page<AppVO>}
     */
    @PostMapping("/list/page/featured")
    public BaseResponse<Page<AppVO>> listFeaturedByPage(@RequestBody AppQueryRequest queryRequest) {
        ThrowUtils.throwIf(queryRequest == null, ErrorCode.PARAMS_ERROR);
        long pageNum = queryRequest.getCurrent();
        long pageSize = 20;
        Page<App> page = appService.page(Page.of(pageNum, pageSize), appService.getQueryWrapper(queryRequest).eq(App::getPriority, 99));
        Page<AppVO> voPage = new Page<>(pageNum, pageSize, page.getTotalRow());
        List<AppVO> voList = appService.getVoList(page.getRecords());
        voPage.setRecords(voList);
        return ResultUtils.success(voPage);
    }

    /**
     * 管理员根据 id 修改任意应用
     *
     * @param updateRequest 更新请求
     * @return 是否成功
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> update(@RequestBody AppUpdateRequest updateRequest) {
        if (updateRequest == null || updateRequest.getId() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        App app = AppConvertMapper.INSTANCE.toEntity(updateRequest);
        boolean result = appService.updateById(app);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 管理员分页查询应用列表
     *
     * @param queryRequest 查询参数
     * @return 分页后的应用 VO 列表 {@link Page<AppVO>}
     */
    @PostMapping("/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<AppVO>> listByPage(@RequestBody AppQueryRequest queryRequest) {
        ThrowUtils.throwIf(queryRequest == null, ErrorCode.PARAMS_ERROR);
        long pageNum = queryRequest.getCurrent();
        long pageSize = queryRequest.getPageSize();
        Page<App> page = appService.page(Page.of(pageNum, pageSize), appService.getQueryWrapper(queryRequest));
        Page<AppVO> voPage = new Page<>(pageNum, pageSize, page.getTotalRow());
        List<AppVO> voList = appService.getVoList(page.getRecords());
        voPage.setRecords(voList);
        return ResultUtils.success(voPage);
    }

    /**
     * 管理员根据 id 查询应用详情
     *
     * @param id 应用 id
     * @return 应用详情 {@link App}
     */
    @GetMapping("/get")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<App> getById(long id) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        App app = appService.getById(id);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR);
        return ResultUtils.success(app);
    }

    // endregion
}
