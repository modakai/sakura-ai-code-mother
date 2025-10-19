package com.sakura.aicode.module.app.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.sakura.aicode.module.app.domain.dto.AppQueryRequest;
import com.sakura.aicode.module.app.domain.entity.App;
import com.sakura.aicode.module.app.domain.vo.AppVO;

import java.util.List;

/**
 * 应用 服务层。
 *
 * @author Sakura
 */
public interface AppService extends IService<App> {

    QueryWrapper getQueryWrapper(AppQueryRequest queryRequest);

    AppVO getVo(App app);

    List<AppVO> getVoList(List<App> appList);
}
