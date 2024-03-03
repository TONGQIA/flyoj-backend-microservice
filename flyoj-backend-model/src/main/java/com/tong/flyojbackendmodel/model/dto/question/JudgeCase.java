package com.tong.flyojbackendmodel.model.dto.question;

import lombok.Data;

import java.io.Serializable;

/**
 * 题目用例
 *
 * @author tong
 * 
 */
@Data
public class JudgeCase implements Serializable {
    /**
     * 输入用例
     */
    private String input;

    /**
     * 输出用例
     */
    private String output;
}