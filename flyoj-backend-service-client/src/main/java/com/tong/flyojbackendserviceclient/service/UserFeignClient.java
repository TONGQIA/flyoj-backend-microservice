package com.tong.flyojbackendserviceclient.service;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.tong.flyojbackendcommon.common.ErrorCode;
import com.tong.flyojbackendcommon.exception.BusinessException;
import com.tong.flyojbackendmodel.model.entity.User;
import com.tong.flyojbackendmodel.model.enums.UserRoleEnum;
import com.tong.flyojbackendmodel.model.vo.UserVO;
import org.springframework.beans.BeanUtils;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.tong.flyojbackendcommon.constant.UserConstant.USER_LOGIN_STATE;

/**
 * 用户服务
 * @author tong
 * 
 */
@FeignClient(name = "flyoj-backend-user-service", path = "/api/user/inner")
public interface UserFeignClient  {
//            userService.getById(userId)
//            userService.listByIds(userIdSet)
//            userService.getLoginUser(request)
//            userService.isAdmin(loginUser)
//            userService.getUserVO(user)

    /**
     * 根据用户id获取用户信息
     * @param userId
     * @return
     */
    @GetMapping("/get/id")
    User getById(@RequestParam("userId") Long userId);

    /**
     * 获取id列表
     * @param idList
     * @return
     */
    @GetMapping("/get/ids")
    List<User> listByIds(@RequestParam("idList")Set<Long> idList);

    /**
     * 获取当前登录用户
     *
     * @param request
     * @return
     */
    default User getLoginUser(HttpServletRequest request){
        // 先判断是否已登录
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User currentUser = (User) userObj;
        if (currentUser == null || currentUser.getId() == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }

        return currentUser;
    }

    /**
     * 是否为管理员
     *
     * @param request
     * @return
     */
    default boolean isAdmin(HttpServletRequest request){
        // 仅管理员可查询
        // 这里的session是存在redis中，是分布式存储了
        Object userObj = request.getSession().getAttribute(USER_LOGIN_STATE);
        User user = (User) userObj;
        return isAdmin(user);
    }

    /**
     * 是否为管理员
     *
     * @param user
     * @return
     */
    default boolean isAdmin(User user){
        return user != null && UserRoleEnum.ADMIN.getValue().equals(user.getUserRole());
    }


    /**
     * 获取脱敏的用户信息
     *
     * @param user
     * @return
     */
    default UserVO getUserVO(User user){
        if (user == null) {
            return null;
        }
        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        return userVO;
    }

    /**
     * 获取脱敏的用户信息
     *
     * @param userList
     * @return
     */
    default List<UserVO> getUserVO(List<User> userList){
        if (CollectionUtils.isEmpty(userList)) {
            return new ArrayList<>();
        }
        return userList.stream().map(this::getUserVO).collect(Collectors.toList());
    }
}
