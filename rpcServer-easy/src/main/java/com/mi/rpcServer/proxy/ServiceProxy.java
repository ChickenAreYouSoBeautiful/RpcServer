package com.mi.rpcServer.proxy;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.mi.rpcServer.model.RpcRequest;
import com.mi.rpcServer.model.RpcResponse;
import com.mi.rpcServer.seriaizer.JdkSerializer;
import com.mi.rpcServer.seriaizer.Serializer;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author mi11
 * @version 1.0
 * @project consumer
 * @description 服务端（JDK动态代理）
 * @ClassName ServiceProxy
 */
public class ServiceProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        final Serializer serializer = new JdkSerializer();

        RpcRequest rpcRequest = RpcRequest.builder()
                .serverName(method.getDeclaringClass().getName())
                .methodName(method.getName())
                .parameterTypes(method.getParameterTypes())
                .parameters(args).build();
        try {
            byte[] serialize = serializer.serialize(rpcRequest);
            //try(){} 响应后自动关闭流
            //todo 地址硬编码，需要使用配置中心和服务发现机制解决
            try(HttpResponse execute = HttpRequest.post("localhost:8080").body(serialize).execute()){
              byte[]  result = execute.bodyBytes();
                RpcResponse deserialize = serializer.deserialize(result, RpcResponse.class);
                if (!"ok".equals(deserialize.getMessage())){
                    return deserialize.getMessage();
                }
                return deserialize.getData();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
