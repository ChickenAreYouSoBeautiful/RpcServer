package com.mi.rpcServer.server;

/**
 * @author mi11
 * @version 1.0
 * @project rpcServer-easy
 * @description Http接口服务
 * @ClassName HttpServer
 */
public interface HttpServer {

    /**
     * 启动服务器
     * @param port
     */
    void doStart(int port);
}
