package com.tong.flyojbackendjudgeservice.codesandbox.impl;


import com.tong.flyojbackendjudgeservice.codesandbox.CodeSandbox;
import com.tong.flyojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.tong.flyojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import com.tong.flyojbackendmodel.model.codesandbox.JudgeInfo;
import com.tong.flyojbackendmodel.model.enums.JudgeInfoMessageEnum;
import com.tong.flyojbackendmodel.model.enums.QuestionSubmitStatusEnum;

import java.util.List;

/**
 * 代码沙箱例子
 *
 * @author tong
 */
public class ExampleCodeSandboxImpl implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("默认代码沙箱");
        List<String> inputList = executeCodeRequest.getInputList();
        ExecuteCodeResponse executeCodeResponse = new ExecuteCodeResponse();
        executeCodeResponse.setOutputList(inputList);
        executeCodeResponse.setMessage("测试成功");
        executeCodeResponse.setStatus(QuestionSubmitStatusEnum.WAITING.getValue());
        JudgeInfo judgeInfo = new JudgeInfo();
        judgeInfo.setMessage(JudgeInfoMessageEnum.ACCEPTED.getValue());
        judgeInfo.setMemory(100L);
        judgeInfo.setTime(100L);
        executeCodeResponse.setJudgeInfo(judgeInfo);
        return executeCodeResponse;

    }
}
