package com.mi.consumer.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.mi.common.model.User;
import com.mi.common.service.UserService;
import com.mi.rpcServer.model.RpcRequest;
import com.mi.rpcServer.model.RpcResponse;
import com.mi.rpcServer.seriaizer.JdkSerializer;
import com.mi.rpcServer.seriaizer.Serializer;

import java.io.IOException;

/**
 * @author mi11
 * @version 1.0
 * @project consumer
 * @description 静态代理
 * @ClassName UserServiceProxy
 */
public class UserServiceProxy implements UserService {
    @Override
    public String getUserName(User user) {
        final Serializer serializer = new JdkSerializer();

        RpcRequest rpcRequest = RpcRequest.builder()
                .serverName("UserService")
                .methodName("getUserName")
                .parameterTypes(new Class[]{User.class})
                .parameters(new Object[]{user}).build();
        byte[] result = null;
        try {
            byte[] serialize = serializer.serialize(rpcRequest);
            //try(){} 响应后自动关闭流
            try(HttpResponse execute = HttpRequest.post("localhost:8080").body(serialize).execute()){
                result = execute.bodyBytes();
            }
            RpcResponse deserialize = serializer.deserialize(result, RpcResponse.class);
            return (String) deserialize.getData();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
