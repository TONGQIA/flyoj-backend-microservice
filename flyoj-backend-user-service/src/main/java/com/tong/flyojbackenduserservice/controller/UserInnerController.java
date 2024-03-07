package com.tong.flyojbackenduserservice.controller;

import com.tong.flyojbackendserviceclient.service.UserFeignClient;
import com.tong.flyojbackendmodel.model.entity.User;
import com.tong.flyojbackenduserservice.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/inner")
public class UserInnerController implements UserFeignClient {

    @Resource
    private UserService userService;

    /**
     * 根据用户id获取用户信息
     * @param userId
     * @return
     */
    @Override
    @GetMapping("get/id")
    public User getById(@RequestParam("userId") Long userId) {
        return userService.getById(userId);
    }

    /**
     * 获取id列表
     * @param idList
     * @return
     */
    @Override
    @GetMapping("get/ids")
    public List<User> listByIds(@RequestParam("idList") Set<Long> idList) {
        return userService.listByIds(idList);
    }
}
