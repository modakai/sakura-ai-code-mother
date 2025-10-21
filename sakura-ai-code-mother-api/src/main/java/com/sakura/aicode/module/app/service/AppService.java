package com.sakura.aicode.module.app.service;

import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import com.sakura.aicode.module.app.domain.dto.AppQueryRequest;
import com.sakura.aicode.module.app.domain.entity.App;
import com.sakura.aicode.module.app.domain.vo.AppVO;
import com.sakura.aicode.module.auth.domain.vo.LoginUserVO;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 应用 服务层。
 *
 * @author Sakura
 */
public interface AppService extends IService<App> {

    /**
     * 部署应用
     * @param appId 应用id
     * @param loginUserVO 登录用户
     * @return 访问的URL路径
     */
    String deployApp(Long appId, LoginUserVO loginUserVO);

    /**
     * 生成应用代码
     * @param appId 应用id
     * @param message 用户提示词
     * @param loginUserVO 登录用户
     * @return Ai生产代码的流式响应
     */
    Flux<String> chatToCode(Long appId, String message, LoginUserVO loginUserVO);

    QueryWrapper getQueryWrapper(AppQueryRequest queryRequest);

    AppVO getVo(App app);

    List<AppVO> getVoList(List<App> appList);
}
