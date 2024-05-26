package com.mi.rpcServer.proxy;

import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * @author mi11
 * @version 1.0
 * @project rpcServer-core
 * @description 模拟接口代理
 * @ClassName ServiveMockProxy
 */
@Slf4j
public class ServiceMockProxy implements InvocationHandler {

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Class<?> returnType = method.getReturnType();
        log.info("mock proxy invoke method:{}", method.getName());
        return getDefaultValue(returnType);
    }

    private Object getDefaultValue(Class<?> returnType) {
        if (returnType.isPrimitive()) {
            if (returnType == int.class) {
                return 0;
            } else if (returnType == long.class) {
                return 0L;
            } else if (returnType == float.class) {
                return 0.0f;
            } else if (returnType == double.class) {
                return 0.0d;
            } else if (returnType == boolean.class) {
                return false;
            }
        }
        return null;
    }
}
