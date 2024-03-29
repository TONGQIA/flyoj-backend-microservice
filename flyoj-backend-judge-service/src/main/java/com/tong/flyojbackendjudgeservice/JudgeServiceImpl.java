package com.tong.flyojbackendjudgeservice;

import cn.hutool.json.JSONUtil;

import com.tong.flyojbackendmodel.model.codesandbox.ExecuteStatusEnum;
import com.tong.flyojbackendserviceclient.service.QuestionFeignClient;
import com.tong.flyojbackendcommon.common.ErrorCode;
import com.tong.flyojbackendcommon.exception.BusinessException;
import com.tong.flyojbackendjudgeservice.codesandbox.CodeSandbox;
import com.tong.flyojbackendjudgeservice.codesandbox.CodeSandboxFactory;
import com.tong.flyojbackendjudgeservice.codesandbox.CodeSandboxProxy;
import com.tong.flyojbackendjudgeservice.strategy.JudgeContext;
import com.tong.flyojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.tong.flyojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import com.tong.flyojbackendmodel.model.codesandbox.JudgeInfo;
import com.tong.flyojbackendmodel.model.dto.question.JudgeCase;
import com.tong.flyojbackendmodel.model.dto.question.JudgeConfig;
import com.tong.flyojbackendmodel.model.entity.Question;
import com.tong.flyojbackendmodel.model.entity.QuestionSubmit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.tong.flyojbackendmodel.model.enums.QuestionSubmitJudgeStatusEnum;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 判题服务实现类
 */
@Service
public class JudgeServiceImpl implements JudgeService {

    @Value("${codesandbox.type:example}")
    private String type;

    @Resource
    private QuestionFeignClient questionFeignClient;

    @Resource
    private JudgeManager judgeManager;

    @Override
    public QuestionSubmit doJudge(long questionSubmitId) {
        // 1. 根据题目提交id获得题目提交信息
        QuestionSubmit questionSubmit = questionFeignClient.getQuestionSubmitById(questionSubmitId);
        if (questionSubmit == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"题目提交信息不存在");
        }
        // 2. 查看题目是否存在
        Question question = questionFeignClient.getQuestionById(questionSubmit.getQuestionId());
        if (question == null){
            throw new BusinessException(ErrorCode.NOT_FOUND_ERROR,"题目信息不存在");
        }
        // 3. 更改题目状态
        if (questionSubmit.getJudgeStatus().equals(QuestionSubmitJudgeStatusEnum.RUNNING.getValue()) ){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"题目正在判题");
        }
        QuestionSubmit questionSubmitUpdate = new QuestionSubmit();
        questionSubmitUpdate.setId(questionSubmit.getId());
        questionSubmitUpdate.setJudgeStatus(QuestionSubmitJudgeStatusEnum.RUNNING.getValue());
        boolean update = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
        if (!update){
            throw new BusinessException(ErrorCode.OPERATION_ERROR,"题目状态更新错误");
        }

        // 4. 存在就将执行的请求类需要的值加入：code、language、inputList，调用代码沙箱
        // 4.1 获取测试用例
        List<JudgeCase> judgeCaseList = JSONUtil.toList(question.getJudgeCase(), JudgeCase.class);
        List<String> inputList = judgeCaseList.stream().map(JudgeCase::getInput).collect(Collectors.toList());
        // 4.2 将请求需要的参数加入
        ExecuteCodeRequest executeCodeRequest = ExecuteCodeRequest.builder()
                .code(questionSubmit.getCode())
                .language(questionSubmit.getLanguage())
                .inputList(inputList)
                .build();

        // 4.3 调用代码沙箱
        CodeSandbox codeSandbox = CodeSandboxFactory.newInstance(type);
        CodeSandboxProxy codeSandboxProxy = new CodeSandboxProxy(codeSandbox);
        ExecuteCodeResponse executeCodeResponse = codeSandboxProxy.executeCode(executeCodeRequest);
        // 5. 根据沙箱的结果设置题目的判题状态和信息
        // 根据executeCodeResponse的executeStatus做不同处理
        Integer executeStatus = executeCodeResponse.getExecuteStatus();
        // 5.1 运行成功， 接下来要做判题
        if (executeStatus.equals(ExecuteStatusEnum.SUCCESS.getCode())){
            List<String> outputListReal = executeCodeResponse.getOutputList();
            List<String> outputListExpect = judgeCaseList.stream().map(JudgeCase::getOutput).collect(Collectors.toList());
            JudgeInfo judgeInfoReal = executeCodeResponse.getJudgeInfo();
            JudgeConfig judgeConfigExpect = JSONUtil.toBean(question.getJudgeConfig(), JudgeConfig.class);
            JudgeContext judgeContext = JudgeContext.builder()
                    .outputListReal(outputListReal)
                    .outputListExpect(outputListExpect)
                    .judgeConfigExpect(judgeConfigExpect)
                    .judgeInfoReal(judgeInfoReal)
                    .questionSubmit(questionSubmit)
                    .build();
            JudgeInfo judgeInfo = judgeManager.doJudge(judgeContext);
            // 更新题目提交记录
            questionSubmitUpdate= new QuestionSubmit();
            questionSubmitUpdate.setId(questionSubmitId);
            questionSubmitUpdate.setJudgeStatus(QuestionSubmitJudgeStatusEnum.SUCCEED.getValue());
            questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        } else if (executeStatus >= ExecuteStatusEnum.RUN_ERROR.getCode()) {
            //5.2  执行状态为3 4 5 --》judgestatus为成功 judgeinfo的message为不同的
            // 更新题目提交记录
            questionSubmitUpdate= new QuestionSubmit();
            questionSubmitUpdate.setId(questionSubmitId);
            questionSubmitUpdate.setJudgeStatus(QuestionSubmitJudgeStatusEnum.SUCCEED.getValue());
            JudgeInfo judgeInfo = new JudgeInfo();
            judgeInfo.setMessage(ExecuteStatusEnum.getEnumByValue(executeStatus).getMessage());
            judgeInfo.setErrorMessage(executeCodeResponse.getMessage());
            questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        }else {
            //5.3  执行状态为1 2 --》judgestatus为失败
            // 更新题目提交记录
            questionSubmitUpdate= new QuestionSubmit();
            questionSubmitUpdate.setId(questionSubmitId);
            questionSubmitUpdate.setJudgeStatus(QuestionSubmitJudgeStatusEnum.FAILED.getValue());
            JudgeInfo judgeInfo = new JudgeInfo();
            judgeInfo.setMessage(ExecuteStatusEnum.getEnumByValue(executeStatus).getMessage());
            questionSubmitUpdate.setJudgeInfo(JSONUtil.toJsonStr(judgeInfo));
        }
        // 6. 更新数据库
        update = questionFeignClient.updateQuestionSubmitById(questionSubmitUpdate);
        if (!update){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "题目提交状态更新错误");
        }
        QuestionSubmit questionSubmitResult = questionFeignClient.getQuestionSubmitById(questionSubmitId);
        return questionSubmitResult;
    }
}
