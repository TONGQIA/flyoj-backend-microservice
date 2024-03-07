package com.tong.flyojbackendserviceclient.service;
import com.tong.flyojbackendmodel.model.entity.QuestionSubmit;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author tong
 * @description 判题服务
 */
@FeignClient(name = "flyoj-backend-judge-service", path = "/api/judge/inner")
public interface JudgeFeignClient {
    /**
     * 调用执行代码沙箱
     * @param questionSubmitId
     * @return
     */
    @GetMapping("/do")
    QuestionSubmit doJudge(@RequestParam("questionSubmitId") long questionSubmitId);
}
