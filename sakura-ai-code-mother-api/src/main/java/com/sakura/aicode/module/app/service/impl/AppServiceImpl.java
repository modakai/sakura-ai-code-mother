package com.sakura.aicode.module.app.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sakura.aicode.module.app.domain.entity.App;
import com.sakura.aicode.module.app.mapper.AppMapper;
import com.sakura.aicode.module.app.service.AppService;
import org.springframework.stereotype.Service;

/**
 * 应用 服务层实现。
 *
 * @author Sakura
 */
@Service
public class AppServiceImpl extends ServiceImpl<AppMapper, App>  implements AppService{

}
