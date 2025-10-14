package com.sakura.aicode.module.user.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.sakura.aicode.module.user.domain.dto.UserQueryRequest;
import com.sakura.aicode.module.user.domain.entity.User;
import com.sakura.aicode.module.user.domain.vo.UserVO;

import java.util.List;

/**
 * 用户 服务层。
 *
 * @author sakura
 */
public interface UserService extends IService<User> {

    QueryWrapper getQueryWrapper(UserQueryRequest queryRequest);

    UserVO getUserVo(User user);

    List<UserVO> getUserVoList(List<User> userList);
}
