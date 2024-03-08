package com.tong.flyojbackendjudgeservice.codesandbox.impl;

import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.tong.flyojbackendcommon.common.ErrorCode;
import com.tong.flyojbackendcommon.exception.BusinessException;
import com.tong.flyojbackendjudgeservice.codesandbox.CodeSandbox;
import com.tong.flyojbackendmodel.model.codesandbox.ExecuteCodeRequest;
import com.tong.flyojbackendmodel.model.codesandbox.ExecuteCodeResponse;
import org.apache.commons.lang3.StringUtils;

/**
 * 远程代码沙箱
 *
 * @author tong
 */
public class RemoteCodeSandboxImpl implements CodeSandbox {

    //定义鉴权请求头和密钥
    public static final String AUTH_REQUEST_HEADER = "auth";
    public static final String AUTH_REQUEST_SECRET = "secretKey";

    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest executeCodeRequest) {
        System.out.println("远程代码沙箱");

        //String url = "http://10.211.55.3:8090/executeCode";
        String url = "http://localhost:8090/executeCode";
        String json = JSONUtil.toJsonStr(executeCodeRequest);
        HttpResponse httpResponse = HttpUtil.createPost(url)
                .header(AUTH_REQUEST_HEADER, AUTH_REQUEST_SECRET)
                .body(json)
                .execute();
        String responseStr = httpResponse.body();
        // TODO 这里的错误获取不够清晰
        if (StringUtils.isBlank(responseStr)) {
            throw new BusinessException(ErrorCode.API_REQUEST_ERROR);
        }

        return JSONUtil.toBean(responseStr,ExecuteCodeResponse.class);
    }
}
