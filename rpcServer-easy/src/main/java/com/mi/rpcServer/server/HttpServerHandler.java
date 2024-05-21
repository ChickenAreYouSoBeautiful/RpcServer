package com.mi.rpcServer.server;

import com.mi.rpcServer.model.RpcRequest;
import com.mi.rpcServer.model.RpcResponse;
import com.mi.rpcServer.registry.LocalRegistry;
import com.mi.rpcServer.seriaizer.JdkSerializer;
import com.mi.rpcServer.seriaizer.Serializer;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.core.http.HttpServerResponse;

import java.io.IOException;
import java.lang.reflect.Method;

/**
 * @author mi11
 * @version 1.0
 * @project rpcServer-easy
 * @description
 * @ClassName HttpServerHandler
 */
public class HttpServerHandler implements Handler<HttpServerRequest> {
    @Override
    public void handle(HttpServerRequest httpServerRequest) {
        final Serializer serializable =new JdkSerializer();

        System.out.println("Received request:"+httpServerRequest.method()+"uri:"+ httpServerRequest.uri());
        //处理请求
        httpServerRequest.bodyHandler(buffer -> {
            //反序列化传递的请求参数
            byte[] bytes = buffer.getBytes();
            RpcRequest deserialize = null;
            try {
                 deserialize = serializable.deserialize(bytes, RpcRequest.class);
            } catch (IOException e) {
                e.printStackTrace();
            }

            RpcResponse rpcResponse = new RpcResponse();

            if (deserialize == null){
                rpcResponse.setMessage("RpcRequest is NULL");
                doResponse(httpServerRequest,rpcResponse,serializable);
                return;
            }
           //利用反射调用方法返回响应结果
            try {
                Class<?> implClass = LocalRegistry.getServer(deserialize.getServerName());
                if (implClass == null){
                    rpcResponse.setMessage("server not fount!");
                    doResponse(httpServerRequest,rpcResponse,serializable);
                    return;
                }
                Method method = implClass.getMethod(deserialize.getMethodName(), deserialize.getParameterTypes());
                Object request = method.invoke(implClass.newInstance(), deserialize.getParameters());
                rpcResponse.setData(request);
                rpcResponse.setDataType(method.getReturnType());
                rpcResponse.setMessage("ok");
            } catch (Exception e) {
                e.printStackTrace();
                rpcResponse.setMessage(e.getMessage());
                rpcResponse.setException(e);
            }

            doResponse(httpServerRequest,rpcResponse,serializable);
        });

    }

    void doResponse(HttpServerRequest httpServerRequest,RpcResponse rpcResponse,Serializer serializer){
        HttpServerResponse httpServerResponse = httpServerRequest.response().putHeader("content-type", "application/json");

        try {
            byte[] serialize = serializer.serialize(rpcResponse);
            httpServerResponse.end(Buffer.buffer(serialize));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
