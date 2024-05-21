package com.mi.common.service;

import com.mi.common.model.User;

/**
 * @author mi11
 * @version 1.0
 * @project common
 * @description
 * @ClassName UserService
 */
public interface UserService {

    /**
     * 获取用户名
     * @param user 用户
     * @return 用户名
     */
    String getUserName(User user);

}
