package com.mi.rpcServer.proxy;

import com.mi.rpcServer.RpcApplication;

import java.lang.reflect.Proxy;

/**
 * @author mi11
 * @version 1.0
 * @project consumer
 * @description 动态代理工程（根据指定类创建动态代理对象）
 * @ClassName ServiceProxyFactory
 */
public class ServiceProxyFactory {

    /**
     * 根据服务类获取服务对象
     * @param serviceClass
     * @param <T>
     * @return
     */
    public static <T> T getProxy(Class<T> serviceClass) {
        if (RpcApplication.getRpcConfig().isMock()){
            return getMockProxy(serviceClass);
        }
        return (T) Proxy.newProxyInstance(serviceClass.getClassLoader()
                , new Class[]{serviceClass}
                , new ServiceProxy());
    }

    private static <T> T getMockProxy(Class<T> serviceClass) {
        return (T) Proxy.newProxyInstance(serviceClass.getClassLoader()
                , new Class[]{serviceClass}
                , new ServiceMockProxy());
    }
}
