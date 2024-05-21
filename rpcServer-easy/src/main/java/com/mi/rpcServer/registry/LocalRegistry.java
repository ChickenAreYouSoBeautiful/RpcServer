package com.mi.rpcServer.registry;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author mi11
 * @version 1.0
 * @project rpcServer-easy
 * @description 本地·注册中心
 * @ClassName LocalRegistry
 */
public class LocalRegistry {

    /**
     * 注册中心
     */
    private static final Map<String,Class<?>> LOCAL_REGISTRY_MAP = new ConcurrentHashMap<>();

    /**
     * 注册服务
     * @param serverName 服务名称
     * @param importClass  服务实现
     */
    public static void registry(String serverName,Class<?> importClass){
        LOCAL_REGISTRY_MAP.put(serverName,importClass);
    }

    /**
     * 获取服务
     * @param serverName 服务名称
     */
    public static Class<?> getServer(String serverName){
       return LOCAL_REGISTRY_MAP.get(serverName);
    }

    /**
     * 删除服务
     * @param serverName 服务名称
     */
    public static void removeServer(String serverName){
         LOCAL_REGISTRY_MAP.remove(serverName);
    }

}
