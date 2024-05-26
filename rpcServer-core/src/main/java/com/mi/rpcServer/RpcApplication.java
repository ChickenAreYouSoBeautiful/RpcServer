package com.mi.rpcServer;

import com.mi.rpcServer.config.RpcConfig;
import com.mi.rpcServer.constant.RpcConstant;
import com.mi.rpcServer.util.ConfigUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author mi11
 * @version 1.0
 * @project rpcServer-core
 * @description  配置类双检索单例模式实现
 * @ClassName RpcApplication
 */
@Slf4j
public class RpcApplication {

    private static volatile RpcConfig rpcConfig;

    /**
     * 支持传入配置类对象
     * @param newRpcConfig
     */
    public static void init(RpcConfig newRpcConfig){
        rpcConfig = newRpcConfig;
        log.info("rpc init config{}",newRpcConfig.toString());
    }

    /**
     * 实现获取配置
     */
    public static void  init(){

        try{
            rpcConfig = ConfigUtil.loadConfig(RpcConfig.class, RpcConstant.RPC_CONFIG_PREFIX);
        }catch (Exception e){
            rpcConfig = new RpcConfig();
        }
    }

    /**
     * 获取配置
     * @return rpcConfig
     */
    public static RpcConfig getRpcConfig(){
        if (rpcConfig == null){
            synchronized (RpcApplication.class){
                if (rpcConfig == null){
                    init();
                }
            }
        }
        return rpcConfig;
    }

}
