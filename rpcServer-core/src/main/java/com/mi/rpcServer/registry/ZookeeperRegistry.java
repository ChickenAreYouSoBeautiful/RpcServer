package com.mi.rpcServer.registry;

import com.mi.rpcServer.config.RegistryConfig;
import com.mi.rpcServer.constant.RpcConstant;
import com.mi.rpcServer.model.ServerMetaInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.CuratorCache;
import org.apache.curator.framework.recipes.cache.CuratorCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author mi11
 * @version 1.0
 * @project rpcServer-core
 * @description 注册中心实现
 * @ClassName ZookeeperRegistry
 */
@Slf4j
public class ZookeeperRegistry implements Registry {

    private CuratorFramework client;

    private ServiceDiscovery<ServerMetaInfo> serviceDiscovery;

    /**
     * 本地注册的节点key用于续期
     */
    private final Set<String> lodeRegisterCacheSet = new HashSet<>();

    /**
     * 注册服务缓存
     */
    private final RegistryServerCache registryServerCache = new RegistryServerCache();

    private final Set<String> watchRegisterKey = new HashSet<>();


    @Override
    public void init(RegistryConfig registryConfig) {
        client = CuratorFrameworkFactory.builder()
                .connectString(registryConfig.getRegistryAddress())
                .retryPolicy(new ExponentialBackoffRetry(Math.toIntExact(registryConfig.getTimeout()), 3))
                .build();

        serviceDiscovery = ServiceDiscoveryBuilder.builder(ServerMetaInfo.class)
                .client(client)
                .basePath(registryConfig.getRegistryAddress())
                .serializer(new JsonInstanceSerializer<>(ServerMetaInfo.class))
                .build();


        try {
            client.start();
            serviceDiscovery.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void registry(ServerMetaInfo serverMetaInfo) throws Exception {
        serviceDiscovery.registerService(buildServiceInstance(serverMetaInfo));

        String registerKey = RpcConstant.ZK_ROOT_PATH + serverMetaInfo.getServerNodeKey();

        lodeRegisterCacheSet.add(registerKey);
    }

    @Override
    public void unRegistry(ServerMetaInfo serverMetaInfo) {
        try {
            serviceDiscovery.unregisterService(buildServiceInstance(serverMetaInfo));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //注销清除本地缓存
        String registerKey = RpcConstant.ZK_ROOT_PATH + serverMetaInfo.getServerNodeKey();
        lodeRegisterCacheSet.remove(registerKey);
    }

    @Override
    public List<ServerMetaInfo> serverDiscovery(String serverKey) {
        //读取缓存
        List<ServerMetaInfo> serverMetaInfos = registryServerCache.read();
        //不为空直接返回
        if (serverMetaInfos != null && !serverMetaInfos.isEmpty()) {
            return serverMetaInfos;
        }


        //查询
        Collection<ServiceInstance<ServerMetaInfo>> serviceInstances = null;
        try {
            serviceInstances = serviceDiscovery.queryForInstances(serverKey);

            //解析数据
            serverMetaInfos = serviceInstances.stream()
                    .map(ServiceInstance::getPayload)
                    .collect(Collectors.toList());
            //写入缓存
            registryServerCache.write(serverMetaInfos);
            return serverMetaInfos;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void destroy() {
        log.warn("当前节点已下线");
        try {
            for (String key : lodeRegisterCacheSet) {
                client.delete().guaranteed().forPath(key);
            }
        } catch (Exception e) {
            throw new RuntimeException();
        }

        if (client != null){
            client.close();
        }
    }

    @Override
    public void heartBeat() {
        //不需要心跳机制，建立了临时节点，当服务器故障时，临时节点直接丢失。
    }

    @Override
    public void watch(String serverNodeKey) {
        String registerKey = RpcConstant.ZK_ROOT_PATH + serverNodeKey;
        boolean newValue = watchRegisterKey.add(registerKey);
        if (newValue) {
            CuratorCache curatorCache = CuratorCache.build(client, registerKey);
            curatorCache.start();
            curatorCache.listenable().addListener(CuratorCacheListener.builder()
                    .forDeletes(childData -> registryServerCache.clear())
                    .forChanges(((childData, childData1) -> registryServerCache.clear()))
                    .build());
        }
    }

    private ServiceInstance<ServerMetaInfo> buildServiceInstance(ServerMetaInfo serverMetaInfo) {
        String serverAddress = serverMetaInfo.getServiceAddress();

        try {
            return ServiceInstance.<ServerMetaInfo>builder()
                    .id(serverAddress)
                    .name(serverMetaInfo.getServerKey())
                    .address(serverAddress)
                    .payload(serverMetaInfo)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
