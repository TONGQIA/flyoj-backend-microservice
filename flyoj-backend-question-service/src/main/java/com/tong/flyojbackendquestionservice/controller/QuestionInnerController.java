package com.tong.flyojbackendquestionservice.controller;

import com.tong.flyojbackendserviceclient.service.QuestionFeignClient;
import com.tong.flyojbackendmodel.model.entity.Question;
import com.tong.flyojbackendmodel.model.entity.QuestionSubmit;
import com.tong.flyojbackendquestionservice.service.QuestionService;
import com.tong.flyojbackendquestionservice.service.QuestionSubmitService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/inner")
public class QuestionInnerController implements QuestionFeignClient {

    @Resource
    private QuestionSubmitService questionSubmitService;

    @Resource
    private QuestionService questionService;


    /**
     * 根据id获取问题信息
     * @param questionId
     * @return
     */
    @Override
    @GetMapping("/get/id")
    public Question getQuestionById(@RequestParam("questionId") long questionId){
        return questionService.getById(questionId);
    }

    /**
     * 根据id获取问题信息
     * @param questionSubmitId
     * @return
     */
    @Override
    @GetMapping("/question_submit/get/id")
    public QuestionSubmit getQuestionSubmitById(@RequestParam("questionId") long questionSubmitId){
        return questionSubmitService.getById(questionSubmitId);
    }

    /**
     * 更新题目提交信息
     * @param questionSubmitUpdate
     * @return
     */
    @Override
    @PostMapping("/question_submit/update")
    public boolean updateQuestionSubmitById(@RequestBody QuestionSubmit questionSubmitUpdate){
        return questionSubmitService.updateById(questionSubmitUpdate);
    }

}
