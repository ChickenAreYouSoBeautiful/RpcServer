package com.mi.rpcServer;

import com.mi.rpcServer.config.RegistryConfig;
import com.mi.rpcServer.config.RpcConfig;
import com.mi.rpcServer.constant.RpcConstant;
import com.mi.rpcServer.registry.Registry;
import com.mi.rpcServer.registry.RegistryFactory;
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
     */
    public static void init(RpcConfig newRpcConfig){
        rpcConfig = newRpcConfig;
        log.info("rpc init config{}",newRpcConfig.toString());
        //注册中心初始化
        RegistryConfig registryConfig = rpcConfig.getRegistryConfig();
        Registry registry = RegistryFactory.getRegistry(registryConfig.getRegistryName());
        registry.init(registryConfig);
        log.info("rpc init registry{}", registryConfig);

        //创建并注册Shutdown Hook 退出时执行
        Runtime.getRuntime().addShutdownHook(new Thread(registry::destroy));
    }

    /**
     * 实现获取配置
     */
    public static void  init(){
        RpcConfig newRpcConfig;
        try{
            newRpcConfig = ConfigUtil.loadConfig(RpcConfig.class, RpcConstant.RPC_CONFIG_PREFIX);
        }catch (Exception e){
            newRpcConfig = new RpcConfig();
        }
        init(newRpcConfig);
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
