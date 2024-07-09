package com.mi.rpcServer.model;

import com.mi.rpcServer.constant.RpcConstant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import static com.mi.rpcServer.constant.RpcConstant.DEFAULT_SERVER_VERSION;

/**
 * @author mi11
 * @version 1.0
 * @project rpcServer-core
 * @description 注册服务元信息
 * @ClassName ServerMetaInfo
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ServerMetaInfo {

    /**
     * 服务名称
     */
    private String serverName;

    /**
     * 服务地址
     */
    private String serverHost;

    /**
     * 服务端口
     */
    private int serverPort;

    /**
     * 服务版本
     */
    private String serverVersion = DEFAULT_SERVER_VERSION;

    /**
     * 服务分组
     */
    private String serverGroup;

    /**
     * 获取服务注册节点键名
     * @return 注册节点简明
     */
    public  String getServerNodeKey(){
        return String.format("%s/%s:%s", getServerKey(),serverHost,serverPort);
    }

    /**
     * 获取服务键名
     * @return 服务键名
     */
    public   String getServerKey(){
        return String.format("%s:%s", serverName,serverVersion);
    }

    /**
     * 获取服务地址
     * @return 服务地址
     */
    public String getServiceAddress(){
        if (serverHost.contains("http")){
            return String.format("http://%s/%s",serverHost,serverPort);
        }
        return String.format("%s:%s", serverHost,serverPort);
    }
}
