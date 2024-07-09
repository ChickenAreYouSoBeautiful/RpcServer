package com.mi.rpcServer.config;

import com.mi.rpcServer.registry.RegistryKeys;
import lombok.Data;

/**
 * @author mi11
 * @version 1.0
 * @project rpcServer-core
 * @description 注册中心配置
 * @ClassName RegistryConfig
 */
@Data
public class RegistryConfig {

    private String registryName = RegistryKeys.ETCD;

    private String registryAddress = "http://127.0.0.1:2379";

    private String username;

    private String password;

    private  Long timeout = 100000L;
}
