package com.sakura.aicode.common.aop;

import com.sakura.aicode.common.ErrorCode;
import com.sakura.aicode.common.annotation.AuthCheck;
import com.sakura.aicode.common.enums.UserRoleEnum;
import com.sakura.aicode.exception.BusinessException;
import com.sakura.aicode.module.auth.domain.vo.LoginUserVO;
import com.sakura.aicode.module.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Aspect
@Component
@RequiredArgsConstructor
public class AuthInterceptor {

    private final AuthService authService;

    @Before("@annotation(authCheck)")
    public void doBefore(JoinPoint joinPoint, AuthCheck authCheck) throws Throwable {
        String mustRole = authCheck.mustRole();
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();

        LoginUserVO loginUser = authService.getLoginInfo(request);

        UserRoleEnum mustRoleEnum = UserRoleEnum.getEnumByValue(mustRole);
        // 不需要权限，直接放行（不做任何处理即可）
        if (mustRoleEnum == null) {
            return;
        }
        // 校验用户权限
        UserRoleEnum userRoleEnum = UserRoleEnum.getEnumByValue(loginUser.getUserRole());
        if (userRoleEnum == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }

        // 管理员权限校验
        if (UserRoleEnum.ADMIN.equals(mustRoleEnum) && !UserRoleEnum.ADMIN.equals(userRoleEnum)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
    }

}
