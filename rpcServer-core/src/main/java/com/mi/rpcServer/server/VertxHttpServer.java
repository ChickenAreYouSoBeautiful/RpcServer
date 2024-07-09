package com.mi.rpcServer.server;


import io.vertx.core.Vertx;

/**
 * @author mi11
 * @version 1.0
 * @project rpcServer-easy
 * @description
 * @ClassName VertxHttpServer
 */
public class VertxHttpServer implements HttpServer {

    @Override
    public void doStart(int port) {
        //创建vert.x实例
        Vertx vertx = Vertx.vertx();
        //创建Http服务器
        io.vertx.core.http.HttpServer httpServer = vertx.createHttpServer();

        // 监听端口并处理
        httpServer.requestHandler(new HttpServerHandler());

        httpServer.listen(port,request ->{
            if (request.succeeded()){
                System.out.println("Server is running listening on port" + port);
            }else {
                System.out.println("Failed to start server:" + request.cause());
            }
        });
    }
}
