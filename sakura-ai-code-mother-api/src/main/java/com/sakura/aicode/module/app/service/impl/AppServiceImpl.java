package com.sakura.aicode.module.app.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.sakura.aicode.common.ErrorCode;
import com.sakura.aicode.common.constant.AiConstant;
import com.sakura.aicode.common.constant.CommonConstant;
import com.sakura.aicode.common.enums.CodeGenTypeEnum;
import com.sakura.aicode.exception.BusinessException;
import com.sakura.aicode.exception.ThrowUtils;
import com.sakura.aicode.module.ai.facade.AiCodeGeneratorFacade;
import com.sakura.aicode.module.app.domain.convert.AppConvertMapper;
import com.sakura.aicode.module.app.domain.dto.AppQueryRequest;
import com.sakura.aicode.module.app.domain.entity.App;
import com.sakura.aicode.module.app.domain.vo.AppVO;
import com.sakura.aicode.module.app.mapper.AppMapper;
import com.sakura.aicode.module.app.service.AppService;
import com.sakura.aicode.module.auth.domain.vo.LoginUserVO;
import com.sakura.aicode.module.user.domain.entity.User;
import com.sakura.aicode.module.user.domain.vo.UserVO;
import com.sakura.aicode.module.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 应用 服务层实现。
 *
 * @author Sakura
 */
@Service
@RequiredArgsConstructor
public class AppServiceImpl extends ServiceImpl<AppMapper, App> implements AppService {

    private final UserService userService;

    private final AiCodeGeneratorFacade aiCodeGeneratorFacade;

    @Setter
    @Value("${app.deployPath}")
    private String deployPath;

    @Override
    public String deployApp(Long appId, LoginUserVO loginUserVO) {
//      1. 参数校验：app是否存在，用户是否有权限部署（仅本人）
        App app = getById(appId);
        if (app == null || !app.getUserId().equals(loginUserVO.getId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权限部署此应用");
        }

//      2. 生成deployKey：生成逻辑：6位大小写字母 + 数字，只能生成一次，并且不能修改
        String deployKey = app.getDeployKey();
        if (StrUtil.isBlank(deployKey)) {
            deployKey = RandomUtil.randomString(6);
        }

//      3. 部署操作：将 code_output 目录下的文件拷贝到 code_deploy 目录，已 deployKey为命名

        // 根据app的代码类型构建目录路径
        String codeGenType = app.getCodeGenType();
        String sourceDirName = codeGenType + "_" + appId;
        String sourceDirPath = AiConstant.CODE_OUTPUT_ROOT_DIR + File.separator + sourceDirName;
        // 检查源目录是否存在
        if (!FileUtil.exist(sourceDirPath) || !FileUtil.isDirectory(sourceDirPath)) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "目录没有代码，请先生成代码");
        }

        // 复制文件到部署目录
        String deployDirPath = deployPath + File.separator + deployKey;
        try {
            FileUtil.copyContent(new File(sourceDirPath), new File(deployDirPath), true);
        } catch (IORuntimeException e) {
            throw new BusinessException(ErrorCode.OPERATION_ERROR, "部署失败");
        }
        // 更新app的部署信息
        boolean updated = updateChain()
                .set(App::getDeployKey, deployKey)
                .set(App::getDeployTime, LocalDateTime.now())
                .eq(App::getId, app.getId())
                .update();
        ThrowUtils.throwIf(!updated, ErrorCode.OPERATION_ERROR, "部署失败");
        // 返回URL
        return String.format("%s/%s", AiConstant.CODE_DEPLOY_HOST, deployKey);
    }

    @Override
    public Flux<String> chatToCode(Long appId, String message, LoginUserVO loginUserVO) {
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "appId错误");

        // 1 查询应用
        App app = queryChain()
                .eq(App::getId, appId)
                .oneOpt()
                .orElseThrow(() -> new BusinessException(ErrorCode.NOT_FOUND_ERROR, "应用不存在"));
        // 2 校验是否为当前用户
        ThrowUtils.throwIf(!app.getUserId().equals(loginUserVO.getId()), ErrorCode.NO_AUTH_ERROR, "无权限操作该应用");

        // 3 Ai生成代码并返回
        String codeGenType = app.getCodeGenType();
        CodeGenTypeEnum codeGenTypeEnum = CodeGenTypeEnum.getEnumByValue(codeGenType);
        ThrowUtils.throwIf(codeGenTypeEnum == null, ErrorCode.PARAMS_ERROR, "不支持该代码类型: " + codeGenType);
        return aiCodeGeneratorFacade.generatorCodeAndSaveWithStream(message, codeGenTypeEnum, appId);
    }

    @Override
    public QueryWrapper getQueryWrapper(AppQueryRequest queryRequest) {
        if (queryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = queryRequest.getId();
        String appName = queryRequest.getAppName();
        Long userId = queryRequest.getUserId();
        Integer priority = queryRequest.getPriority();
        String sortField = queryRequest.getSortField();
        String sortOrder = queryRequest.getSortOrder();
        return QueryWrapper.create()
                .eq(App::getId, id)
                .eq(App::getUserId, userId)
                .eq(App::getPriority, priority)
                .like(App::getAppName, appName)
                .orderBy(sortField, CommonConstant.SORT_ORDER_ASC.equals(sortOrder));
    }

    @Override
    public AppVO getVo(App app) {
        if (app == null) return null;
        AppVO vo = AppConvertMapper.INSTANCE.toVo(app);
        // 查询用户信息
        Long userId = vo.getUserId();
        if (userId != null) {
            User user = userService.getById(userId);
            vo.setUser(userService.getUserVo(user));
        }
        return vo;
    }

    @Override
    public List<AppVO> getVoList(List<App> appList) {
        if (CollUtil.isEmpty(appList)) return CollUtil.empty(List.class);

        // 用户id列表
        Set<Long> appUserIds = appList.stream().map(App::getUserId).collect(Collectors.toSet());
        List<User> users = userService.listByIds(appUserIds);
        if (CollUtil.isEmpty(users)) return CollUtil.empty(List.class);
        Map<Long, UserVO> userVOMap = users.stream()
                .collect(Collectors.toMap(User::getId, userService::getUserVo));

        List<AppVO> voList = AppConvertMapper.INSTANCE.toVoList(appList);
        // 填充信息
        for (AppVO appVO : voList) {
            appVO.setUser(userVOMap.get(appVO.getUserId()));
        }
        return voList;
    }
}
