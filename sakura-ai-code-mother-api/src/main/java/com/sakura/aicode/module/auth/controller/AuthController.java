package com.sakura.aicode.module.auth.controller;

import com.sakura.aicode.common.BaseResponse;
import com.sakura.aicode.common.ErrorCode;
import com.sakura.aicode.common.ResultUtils;
import com.sakura.aicode.common.constant.UserConstant;
import com.sakura.aicode.exception.ThrowUtils;
import com.sakura.aicode.module.auth.domain.dto.UserLoginRequest;
import com.sakura.aicode.module.auth.domain.dto.UserRegisterRequest;
import com.sakura.aicode.module.auth.domain.vo.LoginUserVO;
import com.sakura.aicode.module.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证接口
 * @author Sakura
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 获取登录信息
     * @param request http
     * @return {@link LoginUserVO}
     */
    @GetMapping("/get/login")
    public BaseResponse<LoginUserVO> getLoginUser(HttpServletRequest request) {
        LoginUserVO loginUser = authService.getLoginInfo(request);
        return ResultUtils.success(loginUser);
    }


    /**
     * 用户注册
     *
     * @param userRegisterRequest 用户注册请求
     * @return 注册用户的id
     */
    @PostMapping("/register")
    public BaseResponse<Long> userRegister(@RequestBody UserRegisterRequest userRegisterRequest) {
        ThrowUtils.throwIf(userRegisterRequest == null, ErrorCode.PARAMS_ERROR);
        long result = authService.userRegister(userRegisterRequest);
        return ResultUtils.success(result);
    }

    /**
     * 用户登录
     *
     * @param loginRequest 用户登录请求
     * @return 登录用户 {@link LoginUserVO}
     */
    @PostMapping("/login")
    public BaseResponse<LoginUserVO> userRegister(@RequestBody UserLoginRequest loginRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(loginRequest == null, ErrorCode.PARAMS_ERROR);
        LoginUserVO result = authService.login(loginRequest);
        request.getSession().setAttribute(UserConstant.USER_LOGIN_STATE, result);
        return ResultUtils.success(result);
    }

    /**
     * 退出登录
     * @param request http
     * @return 是否成功
     */
    @PostMapping("/logout")
    public BaseResponse<Boolean> userLogout(HttpServletRequest request) {
        ThrowUtils.throwIf(request == null, ErrorCode.PARAMS_ERROR);
        boolean result = authService.logout(request);
        return ResultUtils.success(result);
    }


}
