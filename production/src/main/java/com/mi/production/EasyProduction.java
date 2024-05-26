package com.mi.production;


import com.mi.common.service.UserService;
import com.mi.rpcServer.RpcApplication;
import com.mi.rpcServer.config.RpcConfig;
import com.mi.rpcServer.registry.LocalRegistry;
import com.mi.rpcServer.server.VertxHttpServer;

import java.lang.reflect.InvocationTargetException;

/**
 * @author mi11
 * @version 1.0
 * @project production
 * @description 消费服务启动类
 * @ClassName EasyProduction
 */
public class EasyProduction {
    public static void main(String[] args) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        RpcApplication.init();
        LocalRegistry.registry(UserService.class.getName(),UserServiceImpl.class);
        VertxHttpServer vertxHttpServer = new VertxHttpServer();
        RpcConfig rpcConfig = RpcApplication.getRpcConfig();
        vertxHttpServer.doStart(RpcApplication.getRpcConfig().getServerPort());

        //测试调用
//        Class<?> userService = LocalRegistry.getServer("UserService");
//        Method getUserName = userService.getMethod("getUserName", User.class);
//        User user = new User();
//        user.setName("wudi");
//        Object wudia = getUserName.invoke(new UserServiceImpl(),user);
//        System.out.println(wudia);
    }
}
