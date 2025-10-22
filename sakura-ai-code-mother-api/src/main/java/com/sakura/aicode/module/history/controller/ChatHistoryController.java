package com.sakura.aicode.module.history.controller;

import com.mybatisflex.core.paginate.Page;
import com.sakura.aicode.common.BaseResponse;
import com.sakura.aicode.common.ErrorCode;
import com.sakura.aicode.common.ResultUtils;
import com.sakura.aicode.common.annotation.AuthCheck;
import com.sakura.aicode.common.constant.UserConstant;
import com.sakura.aicode.exception.ThrowUtils;
import com.sakura.aicode.module.app.domain.entity.App;
import com.sakura.aicode.module.app.service.AppService;
import com.sakura.aicode.module.auth.domain.vo.LoginUserVO;
import com.sakura.aicode.module.auth.service.AuthService;
import com.sakura.aicode.module.history.domain.dto.ChatHistoryQueryRequest;
import com.sakura.aicode.module.history.domain.entity.ChatHistory;
import com.sakura.aicode.module.history.domain.vo.ChatHistoryVO;
import com.sakura.aicode.module.history.service.ChatHistoryService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 对话历史 控制层。
 *
 * @author Sakura
 */
@RestController
@RequestMapping("/history")
@RequiredArgsConstructor
public class ChatHistoryController {

    private final ChatHistoryService chatHistoryService;
    private final AuthService authService;
    private final AppService appService;

    /**
     * 游标获取分页应用聊天记录
     * @param appId 应用id
     * @param pageSize 页码
     * @param lastCreateTime 游标时间
     * @param request 请求
     * @return {@link ChatHistory}
     */
    @GetMapping("/app/cursor/{appId}")
    public BaseResponse<Page<ChatHistory>> cursorAppChatHistory(@PathVariable Long appId,
                                                                @RequestParam(name = "pageSize", defaultValue = "20") long pageSize,
                                                                @RequestParam(name = "lastCreateTime", required = false) LocalDateTime lastCreateTime,
                                                                HttpServletRequest request) {
        ThrowUtils.throwIfId(appId, ErrorCode.PARAMS_ERROR, "应用id为空");
        App app = appService.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");

        LoginUserVO user = authService.getLoginInfo(request);
        ThrowUtils.throwIf(!app.getUserId().equals(user.getId()) && !user.isAdmin(), ErrorCode.NO_AUTH_ERROR, "无权限查看此应用的消息");
        return ResultUtils.success(chatHistoryService.listAppChatHistoryPage(app, pageSize, lastCreateTime, user.getId()));
    }


    /**
     * 分页查询某个应用的对话历史
     *
     * @param queryRequest 查询参数
     * @return 分页后的对话历史 VO 列表 {@link Page<ChatHistoryVO>}
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<ChatHistoryVO>> listChatHistoryByPage(@RequestBody ChatHistoryQueryRequest queryRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(queryRequest == null, ErrorCode.PARAMS_ERROR);
        Long appId = queryRequest.getAppId();
        ThrowUtils.throwIfId(appId, ErrorCode.PARAMS_ERROR, "应用id不能为空");

        // 仅应用创建者和管理员可见
        LoginUserVO loginUser = authService.getLoginInfo(request);
        App app = appService.getById(appId);
        ThrowUtils.throwIf(!app.getUserId().equals(loginUser.getId()) && !loginUser.isAdmin(), ErrorCode.NO_AUTH_ERROR);

        long pageNum = queryRequest.getCurrent();
        long pageSize = queryRequest.getPageSize();
        Page<ChatHistory> page = chatHistoryService.page(Page.of(pageNum, pageSize), chatHistoryService.getQueryWrapper(queryRequest));
        Page<ChatHistoryVO> voPage = new Page<>(pageNum, pageSize, page.getTotalRow());
        List<ChatHistoryVO> voList = chatHistoryService.getVoList(page.getRecords());
        voPage.setRecords(voList);
        return ResultUtils.success(voPage);
    }

    /**
     * 管理员分页查询所有对话历史
     *
     * @param queryRequest 查询参数
     * @return 分页后的对话历史 VO 列表 {@link Page<ChatHistoryVO>}
     */
    @PostMapping("/list/page/vo/admin")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<ChatHistoryVO>> listAllChatHistoryByPage(@RequestBody ChatHistoryQueryRequest queryRequest) {
        ThrowUtils.throwIf(queryRequest == null, ErrorCode.PARAMS_ERROR);
        long pageNum = queryRequest.getCurrent();
        long pageSize = queryRequest.getPageSize();
        Page<ChatHistory> page = chatHistoryService.page(Page.of(pageNum, pageSize), chatHistoryService.getQueryWrapper(queryRequest));
        Page<ChatHistoryVO> voPage = new Page<>(pageNum, pageSize, page.getTotalRow());
        List<ChatHistoryVO> voList = chatHistoryService.getVoList(page.getRecords());
        voPage.setRecords(voList);
        return ResultUtils.success(voPage);
    }
}
