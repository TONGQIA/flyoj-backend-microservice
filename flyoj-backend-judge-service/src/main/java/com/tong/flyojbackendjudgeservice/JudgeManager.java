package com.tong.flyojbackendjudgeservice;

import com.tong.flyojbackendjudgeservice.strategy.DefaultJudgeStrategyImpl;
import com.tong.flyojbackendjudgeservice.strategy.JavaLanguageJudgeStrategyImpl;
import com.tong.flyojbackendjudgeservice.strategy.JudgeContext;
import com.tong.flyojbackendjudgeservice.strategy.JudgeStrategy;
import com.tong.flyojbackendmodel.model.codesandbox.JudgeInfo;
import com.tong.flyojbackendmodel.model.entity.QuestionSubmit;
import org.springframework.stereotype.Service;

/**
 * 判题管理（简化版）-->策略模式
 */
@Service
public class JudgeManager {
    JudgeInfo doJudge(JudgeContext judgeContext){
        QuestionSubmit questionSubmit = judgeContext.getQuestionSubmit();
        String language = questionSubmit.getLanguage();
        JudgeStrategy judgeStrategy;
        switch (language){
            case "java":
                judgeStrategy = new JavaLanguageJudgeStrategyImpl();
                break;
            default:
                judgeStrategy = new DefaultJudgeStrategyImpl();

        }
        return judgeStrategy.doJudge(judgeContext);

    }

}
