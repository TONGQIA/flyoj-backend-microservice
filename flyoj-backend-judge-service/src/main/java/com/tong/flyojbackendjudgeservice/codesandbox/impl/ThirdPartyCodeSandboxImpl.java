package com.tong.flyojbackendjudgeservice.codesandbox.impl;


import com.tong.flyojbackendjudgeservice.codesandbox.CodeSandbox;
import com.tong.flyojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.tong.flyojbackendmodel.model.codesandbox.ExecuteCodeResponse;

/**
 * 第三方代码沙箱
 *
 * @author tong
 */
public class ThirdPartyCodeSandboxImpl implements CodeSandbox {
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("第三方代码沙箱");
        return null;
    }
}
