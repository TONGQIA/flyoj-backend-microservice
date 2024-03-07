package com.tong.flyojbackendquestionservice.controller;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.google.gson.Gson;
import com.tong.flyojbackendserviceclient.service.UserFeignClient;
import com.tong.flyojbackendcommon.annotation.AuthCheck;
import com.tong.flyojbackendcommon.common.BaseResponse;
import com.tong.flyojbackendcommon.common.DeleteRequest;
import com.tong.flyojbackendcommon.common.ErrorCode;
import com.tong.flyojbackendcommon.common.ResultUtils;
import com.tong.flyojbackendcommon.constant.UserConstant;
import com.tong.flyojbackendcommon.exception.BusinessException;
import com.tong.flyojbackendcommon.exception.ThrowUtils;
import com.tong.flyojbackendmodel.model.dto.question.*;
import com.tong.flyojbackendmodel.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.tong.flyojbackendmodel.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.tong.flyojbackendmodel.model.entity.Question;
import com.tong.flyojbackendmodel.model.entity.QuestionSubmit;
import com.tong.flyojbackendmodel.model.entity.User;
import com.tong.flyojbackendmodel.model.vo.QuestionSubmitVO;
import com.tong.flyojbackendmodel.model.vo.QuestionVO;
import com.tong.flyojbackendquestionservice.service.QuestionService;
import com.tong.flyojbackendquestionservice.service.QuestionSubmitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 题目接口
 *
 * @author tong
 */
@RestController
@RequestMapping
@Slf4j
public class QuestionController {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    private QuestionSubmitService questionSubmitService;


    private final static Gson GSON = new Gson();

    // region 增删改查

    /**
     * 创建
     *
     * @param questionAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addQuestion(@RequestBody QuestionAddRequest questionAddRequest, HttpServletRequest request) {
        // 判空
        if (questionAddRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 1. 创建并复制到题目实体类中
        Question question = new Question();
        BeanUtils.copyProperties(questionAddRequest, question);
        // TODO: 可以提取为方法，因为在这个页面 有三个地方用到
        // 2. 特殊处理tags、judgeCase、judgeConfig
        List<String> tags = questionAddRequest.getTags();
        question.setTags(tags != null ? JSONUtil.toJsonStr(tags) : null);

        List<JudgeCase> judgeCase = questionAddRequest.getJudgeCase();

        question.setJudgeCase(judgeCase != null ? JSONUtil.toJsonStr(judgeCase):null);

        JudgeConfig judgeConfig = questionAddRequest.getJudgeConfig();
        question.setJudgeConfig(judgeConfig != null ? JSONUtil.toJsonStr(judgeConfig): null);

        questionService.validQuestion(question, true);
        User loginUser = userFeignClient.getLoginUser(request);
        question.setUserId(loginUser.getId());
        question.setFavourNum(0);
        question.setThumbNum(0);
        boolean result = questionService.save(question);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        long newQuestionId = question.getId();
        return ResultUtils.success(newQuestionId);
    }

    /**
     * 删除
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteQuestion(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userFeignClient.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldQuestion.getUserId().equals(user.getId()) && !userFeignClient.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean b = questionService.removeById(id);
        return ResultUtils.success(b);
    }

    /**
     * 更新（仅管理员）
     *
     * @param questionUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateQuestion(@RequestBody QuestionUpdateRequest questionUpdateRequest) {
        if (questionUpdateRequest == null || questionUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionUpdateRequest, question);
        // 2. 特殊处理tags、judgeCase、judgeConfig
        List<String> tags = questionUpdateRequest.getTags();
        question.setTags(tags != null ? JSONUtil.toJsonStr(tags) : null);

        JudgeCase judgeCase = questionUpdateRequest.getJudgeCase();
        question.setJudgeCase(judgeCase != null ? JSONUtil.toJsonStr(judgeCase):null);

        JudgeConfig judgeConfig = questionUpdateRequest.getJudgeConfig();
        question.setJudgeConfig(judgeConfig != null ? JSONUtil.toJsonStr(judgeConfig): null);
        // 参数校验
        questionService.validQuestion(question, false);
        long id = questionUpdateRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        boolean result = questionService.updateById(question);
        return ResultUtils.success(result);
    }

    /**
     * 根据 id 获取
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<QuestionVO> getQuestionVOById(long id, HttpServletRequest request) {
        if (id <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = questionService.getById(id);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        return ResultUtils.success(questionService.getQuestionVO(question, request));
    }

    /**
     * 分页获取列表（封装类）
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                               HttpServletRequest request) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    /**
     * 分页获取用户列表（仅管理员）
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Question>> listQuestionByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                   HttpServletRequest request) {
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionPage);
    }


    /**
     * 分页获取当前用户创建的资源列表
     *
     * @param questionQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<QuestionVO>> listMyQuestionVOByPage(@RequestBody QuestionQueryRequest questionQueryRequest,
                                                                 HttpServletRequest request) {
        if (questionQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User loginUser = userFeignClient.getLoginUser(request);
        questionQueryRequest.setUserId(loginUser.getId());
        long current = questionQueryRequest.getCurrent();
        long size = questionQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        Page<Question> questionPage = questionService.page(new Page<>(current, size),
                questionService.getQueryWrapper(questionQueryRequest));
        return ResultUtils.success(questionService.getQuestionVOPage(questionPage, request));
    }

    /**
     * 编辑（用户）
     *
     * @param questionEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editQuestion(@RequestBody QuestionEditRequest questionEditRequest, HttpServletRequest request) {
        if (questionEditRequest == null || questionEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionEditRequest, question);
        // 2. 特殊处理tags、judgeCase、judgeConfig
        List<String> tags = questionEditRequest.getTags();
        question.setTags(tags != null ? JSONUtil.toJsonStr(tags) : null);

        List<JudgeCase> judgeCase = questionEditRequest.getJudgeCase();
        question.setJudgeCase(judgeCase != null ? JSONUtil.toJsonStr(judgeCase):null);

        JudgeConfig judgeConfig = questionEditRequest.getJudgeConfig();
        question.setJudgeConfig(judgeConfig != null ? JSONUtil.toJsonStr(judgeConfig): null);
        // 参数校验
        questionService.validQuestion(question, false);
        User loginUser = userFeignClient.getLoginUser(request);
        long id = questionEditRequest.getId();
        // 判断是否存在
        Question oldQuestion = questionService.getById(id);
        ThrowUtils.throwIf(oldQuestion == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldQuestion.getUserId().equals(loginUser.getId()) && !userFeignClient.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        boolean result = questionService.updateById(question);
        return ResultUtils.success(result);
    }

    /**
     * @param questionSubmitAddRequest
     * @param request
     * @return resultNum 提交题目
     */
    @PostMapping("/question_submit/do")
    public BaseResponse<Long> doQuestionSubmit(@RequestBody QuestionSubmitAddRequest questionSubmitAddRequest,
                                               HttpServletRequest request) {
        if (questionSubmitAddRequest == null || questionSubmitAddRequest.getQuestionId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 登录才能提交
        final User loginUser = userFeignClient.getLoginUser(request);

        long result = questionSubmitService.doQuestionSubmit(questionSubmitAddRequest, loginUser);
        return ResultUtils.success(result);
    }

    /**
     * 分页获取题目提交列表（除管理员外，用户只能看到非答案、提交代码等公开信息）
     * @param questionSubmitQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/question_submit/list/page")
    public BaseResponse<Page<QuestionSubmitVO>> listQuestionSubmitByPage(@RequestBody QuestionSubmitQueryRequest questionSubmitQueryRequest,
                                                                         HttpServletRequest request) {
        long current = questionSubmitQueryRequest.getCurrent();
        long size = questionSubmitQueryRequest.getPageSize();
        // 从数据库中查询原始的题目提交分页信息
        Page<QuestionSubmit> questionSubmitPage = questionSubmitService.page(new Page<>(current, size),
                questionSubmitService.getQueryWrapper(questionSubmitQueryRequest));

        User loginUser = userFeignClient.getLoginUser(request);
        // 脱敏后返回
        return ResultUtils.success(questionSubmitService.getQuestionSubmitVOPage(questionSubmitPage,loginUser));
    }

}
