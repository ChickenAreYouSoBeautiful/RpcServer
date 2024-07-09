package com.mi.rpcServer.registry;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.cron.CronUtil;
import cn.hutool.cron.task.Task;
import cn.hutool.json.JSONUtil;
import com.mi.rpcServer.config.RegistryConfig;
import com.mi.rpcServer.model.ServerMetaInfo;
import io.etcd.jetcd.*;
import io.etcd.jetcd.options.GetOption;
import io.etcd.jetcd.options.PutOption;
import io.etcd.jetcd.watch.WatchEvent;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/**
 * @author mi11
 * @version 1.0
 * @project rpcServer-core
 * @description Registry实现
 * @ClassName EtcdRegistry
 */
@Slf4j
public class EtcdRegistry implements Registry {

    private Client client;

    private KV kvClient;

    private final String ETCD_ROOT_PATH = "/rpc/";

    /**
     * 已注册列表
     */
    private final Set<String> REGISTERED = new HashSet<>();

    /**
     * 已监听列表
     */
    private final Set<String> WATCHED_SERVERS = new HashSet<>();

    private final RegistryServerCache registryServerCache = new RegistryServerCache();

    @Override
    public void init(RegistryConfig registryConfig) {
        client = Client.builder()
                .keepaliveWithoutCalls(false)
                .endpoints(registryConfig.getRegistryAddress())
                .build();
        kvClient = client.getKVClient();
        heartBeat();
    }

    @Override
    public void registry(ServerMetaInfo registryMetaInfo) throws Exception {
        //设置租约30秒
        Lease leaseClient = client.getLeaseClient();
        long id = leaseClient.grant(30).get().getID();
        String registryKey = ETCD_ROOT_PATH + registryMetaInfo.getServerNodeKey();
        ByteSequence key = ByteSequence.from(registryKey, StandardCharsets.UTF_8);
        ByteSequence value = ByteSequence.from(JSONUtil.toJsonStr(registryMetaInfo), StandardCharsets.UTF_8);
        PutOption putOption = PutOption.builder().withLeaseId(id).withPrevKV().build();
        kvClient.put(key, value, putOption).get();
        //放入已注册集合
        REGISTERED.add(registryKey);
    }

    @Override
    public void unRegistry(ServerMetaInfo registryMetaInfo) {
        String registryKey = ETCD_ROOT_PATH + registryMetaInfo.getServerNodeKey();
        ByteSequence key = ByteSequence.from(registryKey, StandardCharsets.UTF_8);
        kvClient.delete(key);
        REGISTERED.remove(registryKey);
    }

    @Override
    public List<ServerMetaInfo> serverDiscovery(String serverKey) {
        String registryKey = ETCD_ROOT_PATH + serverKey + "/";

        List<ServerMetaInfo> serverMetaInfoList = registryServerCache.read();

        if (CollUtil.isNotEmpty(serverMetaInfoList)) {
            return serverMetaInfoList;
        }


        try {
            //根据前缀匹配
            ByteSequence key = ByteSequence.from(registryKey, StandardCharsets.UTF_8);
            GetOption getOption = GetOption.builder().isPrefix(true).build();
            List<KeyValue> serverList = kvClient.get(key, getOption).get().getKvs();
            //解析
            serverMetaInfoList = serverList.stream().map(keyValue -> {
                String value = keyValue.getValue().toString(StandardCharsets.UTF_8);
                String serverNodeKey = keyValue.getKey().toString(StandardCharsets.UTF_8);
                watch(serverNodeKey);
                return JSONUtil.toBean(value, ServerMetaInfo.class);
            }).collect(Collectors.toList());
            registryServerCache.write(serverMetaInfoList);
            return serverMetaInfoList;
        } catch (Exception e) {
            log.error("获取服务列表失败" + e.getMessage());
            throw new RuntimeException("获取服务列表失败", e);
        }
    }

    @Override
    public void destroy() {

        for (String registerKey : REGISTERED) {

            try {
                kvClient.delete(ByteSequence.from(registerKey, StandardCharsets.UTF_8)).get();
                log.info("销毁服务：" + registerKey);
            } catch (Exception e) {
                throw new RuntimeException(registerKey + "销毁失败");
            }
        }

        log.warn("etcd当前节点下线");
        if (kvClient != null) {
            kvClient.close();
        }
        if (client != null) {
            client.close();
        }
    }

    @Override
    public void heartBeat() {
        //每10秒执行一次
        CronUtil.schedule("0/10 * * * * ?", new Task() {
            @Override
            public void execute() {
                for (String registryKey : REGISTERED) {
                    ByteSequence key = ByteSequence.from(registryKey, StandardCharsets.UTF_8);
                    try {
                        List<KeyValue> kvs = kvClient.get(key).get().getKvs();
                        if (CollUtil.isEmpty(kvs)) {
                            throw new RuntimeException("服务已下线，请重新注册");
                        }
                        KeyValue keyValue = kvs.get(0);
                        String value = keyValue.getValue().toString();
                        ServerMetaInfo serverMetaInfo = JSONUtil.toBean(value, ServerMetaInfo.class);
                        registry(serverMetaInfo);
                    } catch (Exception e) {
                        throw new RuntimeException(key + "续期失败", e);
                    }
                }
            }
        });

        CronUtil.setMatchSecond(true);
        CronUtil.start();
    }

    @Override
    public void watch(String serverNodeKey) {

        if (StrUtil.isBlank(serverNodeKey)) {
            throw new RuntimeException("serverNodeKey不能为空");
        }

        Watch watchClient = client.getWatchClient();

        if (WATCHED_SERVERS.add(serverNodeKey)) {
            watchClient.watch(ByteSequence.from(ETCD_ROOT_PATH + serverNodeKey, StandardCharsets.UTF_8),watchResponse -> {
                List<WatchEvent> events = watchResponse.getEvents();
                for (WatchEvent event : events) {
                    switch (event.getEventType()){
                        case DELETE: destroy();
                        registryServerCache.clear();
                         break;
                        case PUT:
                        default: break;
                    }
                }
            });
        }
    }

}
