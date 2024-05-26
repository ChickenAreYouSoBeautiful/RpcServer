package com.mi.rpcServer.util;

import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.io.resource.NoResourceException;
import cn.hutool.core.io.resource.Resource;
import cn.hutool.core.util.ObjUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.setting.dialect.Props;

import java.io.File;

/**
 * @author mi11
 * @version 1.0
 * @project rpcServer-core
 * @description 配置工具类
 * @ClassName ConfigUtil
 */
public class ConfigUtil {

    public static <T> T loadConfig(Class<T> tempClass, String prefix) {
        return loadConfig(prefix,tempClass,"");
    }

    public static<T>  T loadConfig(String prefix, Class<T> tempClass,String environment) {
        StringBuilder configFileBuilder = new StringBuilder("application");
        if (StrUtil.isNotBlank(environment)){
            configFileBuilder.append("-").append(environment);
        }

        Props props = null;
        try {
            props = new Props(configFileBuilder + ".properties");
            return props.toBean(tempClass, prefix);
        } catch (NoResourceException e) {
            props = new Props(configFileBuilder + ".yml");
            return props.toBean(tempClass);
        }

    }
}
