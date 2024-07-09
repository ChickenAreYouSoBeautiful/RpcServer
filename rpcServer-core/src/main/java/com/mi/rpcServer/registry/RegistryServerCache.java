package com.mi.rpcServer.registry;

import com.mi.rpcServer.model.ServerMetaInfo;

import java.util.List;

/**
 * @author mi11
 * @version 1.0
 * @project rpcServer-core
 * @description 消费者获取服务缓存
 * @ClassName RegistryServerCache
 */
public class RegistryServerCache {

    private List<ServerMetaInfo> serverMetaInfoList;


    /**
     * 写入缓存
     * @param serverMetaInfos 服务列表
     */
    public void write(List<ServerMetaInfo> serverMetaInfos){
        serverMetaInfoList = serverMetaInfos;
    }

    /**
     * 读取缓存
     * @return 服务列表
     */
    public List<ServerMetaInfo> read(){
        return serverMetaInfoList;
    }

    /**
     * 清理缓存
     */
    public void clear(){
        serverMetaInfoList = null;
    }

}
