package com.tong.flyojbackendserviceclient.service;

import com.tong.flyojbackendmodel.model.entity.Question;
import com.tong.flyojbackendmodel.model.entity.QuestionSubmit;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
* @author tong
* @description 题目服务（包含题目提交）
*/
@FeignClient(name = "flyoj-backend-question-service", path = "/api/question/inner")
public interface QuestionFeignClient {

    /**
     * 根据id获取问题信息
     * @param questionId
     * @return
     */
    @GetMapping("/get/id")
    Question getQuestionById(@RequestParam("questionId") long questionId);

    /**
     * 根据id获取问题信息
     * @param questionSubmitId
     * @return
     */
    @GetMapping("/question_submit/get/id")
    QuestionSubmit getQuestionSubmitById(@RequestParam("questionId") long questionSubmitId);

    /**
     * 更新题目提交信息
     * @param questionSubmitUpdate
     * @return
     */
    @PostMapping("/question_submit/update")
    boolean updateQuestionSubmitById(@RequestBody QuestionSubmit questionSubmitUpdate);
}
