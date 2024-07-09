package com.mi.rpcServer.registry;

import com.mi.rpcServer.config.RegistryConfig;
import com.mi.rpcServer.model.ServerMetaInfo;

import java.util.List;

/**
 * @author mi11
 * @version 1.0
 * @project rpcServer-core
 * @description 注册中心接口
 * @ClassName Registry
 */
public interface Registry {

    /**
     * 初始化
     * @param registryConfig 注册中心配置
     */
    void init(RegistryConfig registryConfig);

    /**
     * 服务注册
     * @param serverMetaInfo 注册中心元数据
     */
    void registry(ServerMetaInfo serverMetaInfo) throws Exception;

    /**
     * 服务注销
     * @param serverMetaInfo 注册中心元数据
     */
    void unRegistry(ServerMetaInfo serverMetaInfo);

    /**
     * 服务发现
     * @param serverKey 服务标识
     * @throws Exception 异常信息
     * @return 服务列表
     */
    List<ServerMetaInfo> serverDiscovery(String serverKey) throws Exception;

    /**
     * 注册中心销毁
     */
    void destroy();

    /**
     * 心跳检测（服务端）
     */
    void heartBeat();

    /**
     * 监听
     * @param serverNodeKey 服务节点key
     */
    void watch(String serverNodeKey);
}
