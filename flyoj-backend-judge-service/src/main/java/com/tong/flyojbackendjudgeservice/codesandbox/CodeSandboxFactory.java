package com.tong.flyojbackendjudgeservice.codesandbox;

import com.tong.flyojbackendjudgeservice.codesandbox.impl.ExampleCodeSandboxImpl;
import com.tong.flyojbackendjudgeservice.codesandbox.impl.RemoteCodeSandboxImpl;
import com.tong.flyojbackendjudgeservice.codesandbox.impl.ThirdPartyCodeSandboxImpl;

/**
 * 代码沙箱静态工厂
 */
public class CodeSandboxFactory {
    public static CodeSandbox newInstance(String type){
        switch (type){
            case "example":
                return new ExampleCodeSandboxImpl();
            case "remote":
                return new RemoteCodeSandboxImpl();
            case "thirdParty":
                return new ThirdPartyCodeSandboxImpl();
            default:
                return new ExampleCodeSandboxImpl();
        }
    }

}
