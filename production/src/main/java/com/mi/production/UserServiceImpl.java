package com.mi.production;

import com.mi.common.model.User;
import com.mi.common.service.UserService;

/**
 * @author mi11
 * @version 1.0
 * @project production
 * @description
 * @ClassName UserServiceImpl
 */
public class UserServiceImpl implements UserService{
    @Override
    public String getUserName(User user) {
        return user.getName();
    }
}
