package com.mi.rpcServer.serializer;

import com.mi.rpcServer.spi.SpiLoader;

/**
 * @author mi11
 * @version 1.0
 * @project rpcServer-core
 * @description 序列化工厂
 * @ClassName SerializerFactory
 */
public class SerializerFactory {

    /**
     * 单例序列化器
     */
    static {
        SpiLoader.load(Serializer.class);
    }

    private final static Serializer DEFAULT_SERIALIZER = new JdkSerializer();

    /**
     * 根据key 获取序列化器
     *
     * @param key key
     * @return 序列化器
     */
    public static Serializer getSerializer(String key) {
        return SpiLoader.getInstance(key, Serializer.class);
    }
}
