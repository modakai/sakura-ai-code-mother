package com.sakura.aicode.module.user.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sakura.aicode.module.user.domain.entity.User;
import com.sakura.aicode.module.user.mapper.UserMapper;
import com.sakura.aicode.module.user.service.UserService;
import org.springframework.stereotype.Service;

/**
 * 用户 服务层实现。
 *
 * @author sakura
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User>  implements UserService{

}
