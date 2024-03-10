package com.tong.flyojbackendquestionservice.service.impl;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tong.flyojbackendcommon.common.ErrorCode;
import com.tong.flyojbackendcommon.constant.CommonConstant;
import com.tong.flyojbackendcommon.exception.BusinessException;
import com.tong.flyojbackendcommon.utils.SqlUtils;
import com.tong.flyojbackendmodel.model.codesandbox.JudgeInfo;
import com.tong.flyojbackendmodel.model.dto.questionsubmit.QuestionSubmitAddRequest;
import com.tong.flyojbackendmodel.model.dto.questionsubmit.QuestionSubmitQueryRequest;
import com.tong.flyojbackendmodel.model.entity.Question;
import com.tong.flyojbackendmodel.model.entity.QuestionSubmit;
import com.tong.flyojbackendmodel.model.entity.User;
import com.tong.flyojbackendmodel.model.enums.QuestionSubmitJudgeStatusEnum;
import com.tong.flyojbackendmodel.model.enums.QuestionSubmitLanguageEnum;
import com.tong.flyojbackendmodel.model.vo.QuestionSubmitVO;
import com.tong.flyojbackendmodel.model.vo.QuestionVO;
import com.tong.flyojbackendquestionservice.mapper.QuestionSubmitMapper;
import com.tong.flyojbackendquestionservice.rabbitmq.MyMessageProducer;
import com.tong.flyojbackendquestionservice.service.QuestionService;
import com.tong.flyojbackendquestionservice.service.QuestionSubmitService;
import com.tong.flyojbackendserviceclient.service.JudgeFeignClient;
import com.tong.flyojbackendserviceclient.service.UserFeignClient;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author tong
 * @description 针对表【question_submit(题目提交)】的数据库操作Service实现
 * @createDate 2023-11-01 20:25:50
 */
@Service
public class QuestionSubmitServiceImpl extends ServiceImpl<QuestionSubmitMapper, QuestionSubmit>
        implements QuestionSubmitService {

    @Resource
    private QuestionService questionService;

    @Resource
    private UserFeignClient userFeignClient;

    @Resource
    @Lazy
    private JudgeFeignClient judgeFeignClient;

    @Resource
    private MyMessageProducer myMessageProducer;

    /**
     * 点赞
     *
     * @param questionSubmitAddRequest
     * @param loginUser
     * @return
     */
    @Override
    public long doQuestionSubmit(QuestionSubmitAddRequest questionSubmitAddRequest, User loginUser) {
        // 判断编程语言是否合法
        String language = questionSubmitAddRequest.getLanguage();
        QuestionSubmitLanguageEnum languageEnum = QuestionSubmitLanguageEnum.getEnumByValue(language);
        if (languageEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "编程语言错误");
        }

        // 判断实体是否存在，根据类别获取实体
        long questionId = questionSubmitAddRequest.getQuestionId();
        Question question = questionService.getById(questionId);
        if (question == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR);
        }
        // 准备提交的题目信息
        QuestionSubmit questionSubmit = new QuestionSubmit();
        questionSubmit.setUserId(loginUser.getId());
        questionSubmit.setQuestionId(questionId);
        questionSubmit.setCode(questionSubmitAddRequest.getCode());
        questionSubmit.setLanguage(questionSubmitAddRequest.getLanguage());
        questionSubmit.setJudgeInfo(JSONUtil.toJsonStr(new JudgeInfo()));
        // DONE：设置状态初始值
        questionSubmit.setJudgeStatus(QuestionSubmitJudgeStatusEnum.WAITING.getValue());

        boolean save = this.save(questionSubmit);
        if (!save) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "数据插入失败");
        }
        // 使用rabbitmq发送消息 判题服务会接收并完成后续处理
        Long questionSubmitId = questionSubmit.getId();
        myMessageProducer.sendMessage("code_exchange", "my_routingKey", String.valueOf(questionSubmitId));
        // 异步执行判题服务
//        CompletableFuture.runAsync(()->{
//            judgeFeignClient.doJudge(questionSubmitId);
//        });
        return questionSubmitId;

    }


    /**
     * 获取查询包装类
     *
     * @param questionSubmitQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<QuestionSubmit> getQueryWrapper(QuestionSubmitQueryRequest questionSubmitQueryRequest) {
        QueryWrapper<QuestionSubmit> queryWrapper = new QueryWrapper<>();
        if (questionSubmitQueryRequest == null) {
            return queryWrapper;
        }
        String language = questionSubmitQueryRequest.getLanguage();
        Integer judgeJudgeStatus = questionSubmitQueryRequest.getJudgeStatus();
        Long questionId = questionSubmitQueryRequest.getQuestionId();
        String sortField = questionSubmitQueryRequest.getSortField();
        String sortOrder = questionSubmitQueryRequest.getSortOrder();

        // 拼接查询条件
        queryWrapper.eq(ObjectUtils.isNotEmpty(questionId), "questionId", questionId);
        queryWrapper.eq(StringUtils.isNotBlank(language), "language", language);
        queryWrapper.eq(QuestionSubmitJudgeStatusEnum.getEnumByValue(judgeJudgeStatus) != null, "judgeJudgeStatus", judgeJudgeStatus);

        queryWrapper.eq("status", 0);
        queryWrapper.orderBy(SqlUtils.validSortField(sortField), sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 数据脱敏，只能本人或管理员能看到自己提交的代码（即判断提交用户id是否等于该用户）
     * @param questionSubmit
     * @param loginUser
     * @return
     */
    @Override
    public QuestionSubmitVO getQuestionSubmitVO(QuestionSubmit questionSubmit, User loginUser) {
        QuestionSubmitVO questionSubmitVO = QuestionSubmitVO.objToVo(questionSubmit);
        // 脱敏
        if (loginUser.getId() != questionSubmit.getUserId() || !userFeignClient.isAdmin(loginUser)){
            questionSubmitVO.setCode(null);
        }

        return questionSubmitVO;
    }

    @Override
    public Page<QuestionSubmitVO> getQuestionSubmitVOPage(Page<QuestionSubmit> questionSubmitPage, User loginUser) {
        List<QuestionSubmit> questionSubmitList = questionSubmitPage.getRecords();

        Page<QuestionSubmitVO> questionSubmitVOPage = new Page<>(questionSubmitPage.getCurrent(), questionSubmitPage.getSize(), questionSubmitPage.getTotal());
        if (CollectionUtils.isEmpty(questionSubmitList)) {
            return questionSubmitVOPage;
        }
        // 1. 脱敏
        List<QuestionSubmitVO> questionSubmitVOList = questionSubmitList.stream()
                .map(questionSubmit -> getQuestionSubmitVO(questionSubmit, loginUser))
                .collect(Collectors.toList());


        // 2. 批量关联查询用户信息
        // 2.1 将列表中的用户id装到set中
        Set<Long> userIdSet = questionSubmitList.stream().map(QuestionSubmit::getUserId).collect(Collectors.toSet());
        // 2.2 查找ids的用户信息并借助map形成对应关系
        Map<Long, List<User>> userIdUserListMap = userFeignClient.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 3. 批量关联题目信息（逻辑同上）
        Set<Long> questionIDSet = questionSubmitList.stream().map(QuestionSubmit::getQuestionId).collect(Collectors.toSet());
        Map<Long, List<Question>> questionIdQuestionListMap = questionService.listByIds(questionIDSet).stream().collect(Collectors.groupingBy(Question::getId));

        // 4. 填充信息
        List<QuestionSubmitVO> questionSubmitVOList1 = questionSubmitVOList.stream().map(questionSubmitVO -> {
            // 填充用户信息
            Long userId = questionSubmitVO.getUserId();
            User user = userIdUserListMap.containsKey(userId)?  userIdUserListMap.get(userId).get(0):null;
            questionSubmitVO.setUserVO(userFeignClient.getUserVO(user));
            // 填充题目信息
            Long questionId = questionSubmitVO.getQuestionId();
            Question question =  questionIdQuestionListMap.containsKey(questionId)? questionIdQuestionListMap.get(questionId).get(0):null;
            questionSubmitVO.setQuestionVO(QuestionVO.objToVo(question));
            return questionSubmitVO;
        }).collect(Collectors.toList());

        questionSubmitVOPage.setRecords(questionSubmitVOList1);
        return questionSubmitVOPage;
    }
}




