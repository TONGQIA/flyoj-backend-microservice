package com.tong.flyojbackendmodel.model.dto.questionsubmit;

import com.tong.flyojbackendcommon.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * 题目提交请求
 *
 * @author tong
 * 
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class QuestionSubmitQueryRequest extends PageRequest implements Serializable {

    /**
     * 编程语言
     */
    private String language;

    /**
     * 题目提交状态
     */
    private Integer judgeStatus;

    /**
     * 题目 id
     */
    private Long questionId;

    /**
     * 用户id
     */
    private Long userId;

    private static final long serialVersionUID = 1L;
}