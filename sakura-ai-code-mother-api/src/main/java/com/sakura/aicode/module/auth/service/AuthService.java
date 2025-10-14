package com.sakura.aicode.module.auth.service;

import cn.hutool.core.util.RandomUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.sakura.aicode.common.ErrorCode;
import com.sakura.aicode.common.constant.UserConstant;
import com.sakura.aicode.exception.BusinessException;
import com.sakura.aicode.exception.ThrowUtils;
import com.sakura.aicode.module.auth.domain.dto.UserLoginRequest;
import com.sakura.aicode.module.auth.domain.dto.UserRegisterRequest;
import com.sakura.aicode.module.auth.domain.vo.LoginUserVO;
import com.sakura.aicode.module.user.domain.convert.UserConvertMapper;
import com.sakura.aicode.module.user.domain.entity.User;
import com.sakura.aicode.module.user.service.UserService;
import com.sakura.aicode.utils.PasswordUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 系统认证服务
 *
 * @author Sakura
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;

    public LoginUserVO getLoginInfo(HttpServletRequest request) {
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(UserConstant.USER_LOGIN_STATE);
        LoginUserVO currentUser = (LoginUserVO) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 从数据库查询（追求性能的话可以注释，直接返回上述结果）
//        long userId = currentUser.getId();
//        currentUser = this.getById(userId);
//        if (currentUser == null) {
//            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
//        }
        return currentUser;
    }

    /**
     * 用户注册
     *
     * @param registerRequest 注册请求
     * @return 新用户 id
     */
    public long userRegister(UserRegisterRequest registerRequest) {
        String userAccount = registerRequest.getUserAccount();
        String userPassword = registerRequest.getUserPassword();
        String checkPassword = registerRequest.getCheckPassword();

        if (checkPassword.equals(userPassword)) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "两次密码不一致");
        }

        QueryWrapper query = userService.query();
        query.from(User.class);
        query.eq(User::getUserAccount, userAccount);
        boolean exists = userService.exists(query);
        if (exists) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR, "账号已存在");
        }

        // 加密
        String password = PasswordUtils.encryptPassword(userPassword);

        // 注册
        User registerUser = new User();
        registerUser.setUserAccount(userAccount);
        registerUser.setUserPassword(password);
        registerUser.setUserName("用户" + RandomUtil.randomInt(1000, 10000));
        registerUser.setUserProfile("这个用户很懒，没有留下任何语言");
        registerUser.setUserRole(UserConstant.DEFAULT_ROLE);
        boolean save = userService.save(registerUser);
        return save ? registerUser.getId() : 0;
    }

    /**
     * 用户注册
     *
     * @param loginRequest 登录请求
     * @return 登录用户
     */
    public LoginUserVO login(UserLoginRequest loginRequest) {
        String userAccount = loginRequest.getUserAccount();
        String userPassword = loginRequest.getUserPassword();

        User user = userService.queryChain()
                .eq(User::getUserAccount, userAccount)
                .oneOpt()
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_ERROR, "账号不存在"));

        // 校验密码
        String storePassword = user.getUserPassword();
        boolean verifyPassword = PasswordUtils.verifyPassword(userPassword, storePassword);
        ThrowUtils.throwIf(!verifyPassword, ErrorCode.PARAMS_ERROR, "密码错误");

        return UserConvertMapper.INSTANCE.toVo(user);
    }

    public boolean logout(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object userObj = session.getAttribute(UserConstant.USER_LOGIN_STATE);
        if (userObj == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        session.removeAttribute(UserConstant.USER_LOGIN_STATE);
        return true;
    }
}
