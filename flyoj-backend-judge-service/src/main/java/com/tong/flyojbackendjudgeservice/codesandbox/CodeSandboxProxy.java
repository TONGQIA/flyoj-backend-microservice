package com.tong.flyojbackendjudgeservice.codesandbox;

import com.tong.flyojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.tong.flyojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import lombok.extern.slf4j.Slf4j;

/**
 *  代码沙箱的代理
 *
 *  @author tong
 */
@Slf4j
public class CodeSandboxProxy implements CodeSandbox {

    /**
     * 被代理的类
     */
    private final CodeSandbox codeSandbox;

    public CodeSandboxProxy(CodeSandbox codeSandbox){
        this.codeSandbox = codeSandbox;
    }


    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        log.info("代码沙箱请求信息："+executeCodeRequest);
        ExecuteCodeResponse executeCodeResponse = codeSandbox.executeCode(executeCodeRequest);
        log.info("代码沙箱响应信息："+executeCodeResponse);
        return executeCodeResponse;
    }
}
