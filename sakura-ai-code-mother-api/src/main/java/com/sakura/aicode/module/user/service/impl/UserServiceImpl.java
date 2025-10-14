package com.sakura.aicode.module.user.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sakura.aicode.common.ErrorCode;
import com.sakura.aicode.common.constant.CommonConstant;
import com.sakura.aicode.exception.BusinessException;
import com.sakura.aicode.module.user.domain.convert.UserConvertMapper;
import com.sakura.aicode.module.user.domain.dto.UserQueryRequest;
import com.sakura.aicode.module.user.domain.entity.User;
import com.sakura.aicode.module.user.domain.vo.UserVO;
import com.sakura.aicode.module.user.mapper.UserMapper;
import com.sakura.aicode.module.user.service.UserService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用户 服务层实现。
 *
 * @author sakura
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>  implements UserService{

    @Override
    public QueryWrapper getQueryWrapper(UserQueryRequest userQueryRequest) {
        if (userQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = userQueryRequest.getId();
        String userAccount = userQueryRequest.getUserAccount();
        String userName = userQueryRequest.getUserName();
        String userProfile = userQueryRequest.getUserProfile();
        String userRole = userQueryRequest.getUserRole();
        String sortField = userQueryRequest.getSortField();
        String sortOrder = userQueryRequest.getSortOrder();
        return QueryWrapper.create()
                .eq(User::getId, id)
                .eq(User::getUserRole, userRole)
                .like(User::getUserAccount, userAccount)
                .like(User::getUserName, userName)
                .like(User::getUserProfile, userProfile)
                .orderBy(sortField, CommonConstant.SORT_ORDER_ASC.equals(sortOrder));
    }


    @Override
    public UserVO getUserVo(User user) {
        return user == null ? null : UserConvertMapper.INSTANCE.toVo(user);
    }

    @Override
    public List<UserVO> getUserVoList(List<User> userList) {
        return CollUtil.isEmpty(userList) ? null : UserConvertMapper.INSTANCE.toVoList(userList);
    }
}
