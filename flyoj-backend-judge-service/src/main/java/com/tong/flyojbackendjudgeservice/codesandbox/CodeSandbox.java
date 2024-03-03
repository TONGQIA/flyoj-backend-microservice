package com.tong.flyojbackendjudgeservice.codesandbox;

import com.tong.flyojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.tong.flyojbackendmodel.model.codesandbox.ExecuteCodeResponse;

/**
 * 代码沙箱接口
 *
 * @author tong
 *
 */
public interface CodeSandbox {
    /**
     * 执行代码
     * @param executeCodeRequest
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest);
}
