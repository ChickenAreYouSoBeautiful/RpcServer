package com.mi.rpcServer.config;

import lombok.Data;

/**
 * @author mi11
 * @version 1.0
 * @project rpcServer-core
 * @description 配置信息
 * @ClassName RpcConfig
 */
@Data
public class RpcConfig {

    /**
     * 名称
     */
    private  String name = "yu-rpc";

    /**
     * 版本
     */
    private String version = "0.0.1";

    /**
     * 服务器地址
     */
    private String serverHost = "127.0.0.1";

    /**
     *  服务器端口
     */
    private int serverPort = 8080;

    /**
     * 是否模拟调用
     */
    private boolean isMock = false;

    /**
     * 指定序列化器
     */
    private String serializer = "jdk";
}
