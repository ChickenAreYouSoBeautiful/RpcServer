package com.mi.rpcServer.spi;

import cn.hutool.core.io.resource.ResourceUtil;
import com.mi.rpcServer.serializer.Serializer;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author mi11
 * @version 1.0
 * @project rpcServer-core
 * @description Spi加载配置
 * @ClassName SpiLoader
 */
@Slf4j
public class SpiLoader {

    /**
     * 存储已加载过的实例
     */
    private static Map<String, Map<String, Class<?>>> loaderMap = new ConcurrentHashMap<>();

    /**
     * 实例的缓存-单例
     */
    private static Map<String, Object> instanceMap = new ConcurrentHashMap<>();

    /**
     * 系统的SPI加载路径
     */
    private final static String SPI_SYSTEM_PATH = "META-INF/rpc/system/";

    /**
     * 用户自定义序列化器加载路径
     */
    private final static String SPI_CUSTOM_PATH = "META-INF/rpc/serializer/";

    /**
     * 用户自定义注册中心加载路径
     */
    private final static String SPI_REGISTRY_PATH = "META-INF/rpc/registry/";

    /**
     * 加载路径列表
     */
    private static final String[] SPI_PATH_LIST = new String[]{SPI_SYSTEM_PATH, SPI_CUSTOM_PATH,SPI_REGISTRY_PATH};

    private static final List<Class<?>> LOAD_CLASS_LIST = Collections.singletonList(Serializer.class);


    public static void loadAll() {
        for (Class<?> aClass : LOAD_CLASS_LIST) {
            load(aClass);
        }
    }


    /**
     * 获取指定实例
     *
     * @param key
     * @param loadClass
     * @param <T>
     * @return
     */
    public static <T> T getInstance(String key, Class<?> loadClass) {
        String className = loadClass.getName();
        Map<String, Class<?>> keyClassMap = loaderMap.get(className);
        if (keyClassMap == null) {
            throw new RuntimeException(String.format("SpiLoader 未加载 %s 类型", className));
        }
        if (!keyClassMap.containsKey(key)) {
            throw new RuntimeException(String.format("SpiLoader 未加载 %s 类型", className));
        }

        Class<?> instanceClass = keyClassMap.get(key);
        String instanceClassName = instanceClass.getName();
        if (!instanceMap.containsKey(instanceClassName)) {
            try {
                instanceMap.put(instanceClassName, instanceClass.newInstance());
            } catch (Exception e) {
                log.error(String.format("实例化实例失败 %s", instanceClassName));
                e.printStackTrace();
            }
        }

        return (T) instanceMap.get(instanceClassName);

    }



    /**
     * 加载某个类型的实例
     *
     * @param loadClass loadClass
     * @return key和加载类的实现
     */
    public static Map<String, Class<?>> load(Class<?> loadClass) {
        log.info("load class:{}", loadClass.getName());
        Map<String, Class<?>> keyClassMap = new HashMap<>();
        for (String spiPath : SPI_PATH_LIST) {
            List<URL> resources = ResourceUtil.getResources(spiPath + loadClass.getName());
            for (URL resource : resources) {
                try {
                    InputStreamReader inputStreamReader = new InputStreamReader(resource.openStream());
                    BufferedReader reader = new BufferedReader(inputStreamReader);
                    String line;
                    while ((line = reader.readLine()) != null) {
                        String[] split = line.split("=");
                        String key = split[0];
                        String clazz = split[1];
                        keyClassMap.put(key, Class.forName(clazz));
                    }
                } catch (Exception e) {
                    log.error("load spi error", e);
                }
            }

        }
        loaderMap.put(loadClass.getName(), keyClassMap);
        return keyClassMap;

    }
}
