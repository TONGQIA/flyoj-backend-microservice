package com.tong.flyojbackendquestionservice.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.tong.flyojbackendmodel.model.dto.question.QuestionQueryRequest;
import com.tong.flyojbackendmodel.model.entity.Question;
import com.tong.flyojbackendmodel.model.vo.QuestionVO;

import javax.servlet.http.HttpServletRequest;

/**
* @author tong
* @description 针对表【question(题目)】的数据库操作Service
* @createDate 2023-11-01 20:17:55
*/
public interface QuestionService extends IService<Question> {

    /**
     * 校验
     *
     * @param question
     * @param add
     */
    void validQuestion(Question question, boolean add);

    /**
     * 获取查询条件（用户）
     *
     * @param questionQueryRequest
     * @return
     */
    QueryWrapper<Question> getQueryWrapper(QuestionQueryRequest questionQueryRequest);


    /**
     * 获取查询条件（管理员）
     *
     * @param questionQueryRequest
     * @return
     */
    QueryWrapper<Question> getQueryWrapperAdmin(QuestionQueryRequest questionQueryRequest);

    /**
     * 获取帖子封装
     *
     * @param question
     * @param request
     * @return
     */
    QuestionVO getQuestionVO(Question question, HttpServletRequest request);

    /**
     * 分页获取帖子封装
     *
     * @param questionPage
     * @param request
     * @return
     */
    Page<QuestionVO> getQuestionVOPage(Page<Question> questionPage, HttpServletRequest request);
}
