package com.tong.flyojbackendjudgeservice.strategy;

import com.tong.flyojbackendmodel.model.codesandbox.JudgeInfo;
import com.tong.flyojbackendmodel.model.dto.question.JudgeConfig;
import com.tong.flyojbackendmodel.model.entity.QuestionSubmit;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class JudgeContext {
    private JudgeInfo judgeInfoReal;

    private JudgeConfig judgeConfigExpect;

    private List<String> outputListReal;

    private List<String> outputListExpect;

    private QuestionSubmit questionSubmit;



}
