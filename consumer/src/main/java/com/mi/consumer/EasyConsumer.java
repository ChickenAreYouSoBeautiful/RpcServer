package com.mi.consumer;

import com.mi.common.model.User;
import com.mi.common.service.UserService;
import com.mi.rpcServer.RpcApplication;
import com.mi.rpcServer.config.RpcConfig;
import com.mi.rpcServer.proxy.ServiceProxyFactory;
import com.mi.rpcServer.util.ConfigUtil;

/**
 * @author mi11
 * @version 1.0
 * @project consumer
 * @description
 * @ClassName EasyConsumer
 */
public class EasyConsumer {
    public static void main(String[] args) {
        User user = new User();
        user.setName("wudi");
        //直接调用
//        UserService userService = null;
//        String userName = userService.getUserName(user);
//        System.out.println(userName);
        UserService userServiceProxy = ServiceProxyFactory.getProxy(UserService.class);
        String userName = userServiceProxy.getUserName(user);
        System.out.println(userName);
    }
}
