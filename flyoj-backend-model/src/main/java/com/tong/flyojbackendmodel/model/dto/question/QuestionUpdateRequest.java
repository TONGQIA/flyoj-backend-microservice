package com.tong.flyojbackendmodel.model.dto.question;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 更新请求
 *
 * @author tong
 */
@Data
public class QuestionUpdateRequest implements Serializable {

    /**
     * id
     * 这里区别于add，因为编辑需要题目id来追踪
     */
    private Long id;

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 标签列表
     */
    private List<String> tags;

    /**
     * 正确答案
     */
    private String answer;

    /**
     * 判题用例（json数组）
     */
    private JudgeCase judgeCase;

    /**
     * 判题配置（json对象）
     */
    private JudgeConfig judgeConfig;

    private static final long serialVersionUID = 1L;
}