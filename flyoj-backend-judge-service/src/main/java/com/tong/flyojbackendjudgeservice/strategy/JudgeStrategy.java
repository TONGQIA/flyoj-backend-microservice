package com.tong.flyojbackendjudgeservice.strategy;

import com.tong.flyojbackendmodel.model.codesandbox.JudgeInfo;

/**
 * 判题服务
 */
public interface JudgeStrategy {
    /**
     * 判断结果
     * @param judgeContext
     * @return
     */
    JudgeInfo doJudge(JudgeContext judgeContext);

}
