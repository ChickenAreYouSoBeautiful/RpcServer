package com.mi.rpcServer.registry;

import com.mi.rpcServer.spi.SpiLoader;

/**
 * @author mi11
 * @version 1.0
 * @project rpcServer-core
 * @description 注册中心工厂
 * @ClassName RegistryFactory
 */
public class RegistryFactory {

    static {
        SpiLoader.load(Registry.class);
    }

    /**
     * 默认注册中心
     */
    private static final Registry DEFAULT_REGISTRY = new EtcdRegistry();

    public static Registry getRegistry(String key){
        return SpiLoader.getInstance(key, Registry.class);
    }
}
