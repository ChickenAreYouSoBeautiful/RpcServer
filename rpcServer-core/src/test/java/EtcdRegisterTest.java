import cn.hutool.core.collection.CollUtil;
import com.mi.rpcServer.config.RegistryConfig;
import com.mi.rpcServer.model.ServerMetaInfo;
import com.mi.rpcServer.registry.EtcdRegistry;
import com.mi.rpcServer.registry.Registry;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

/**
 * @author mi11
 * @version 1.0
 * @project rpcServer-core
 * @description 注册中心测试
 * @ClassName EtcdRegisterTest
 */
public class EtcdRegisterTest {

    final Registry registry = new EtcdRegistry();

    @Before
    public void init()
    {
        RegistryConfig registryConfig = new RegistryConfig();
        registryConfig.setRegistryAddress("http://127.0.0.1:2379");
        registry.init(registryConfig);
    }

    @Test
    public void registry() throws Exception {
        ServerMetaInfo serverMetaInfo1 = new ServerMetaInfo("test", "127.0.0.1", 8081, "1.0.0", "test");
        registry.registry(serverMetaInfo1);
        ServerMetaInfo serverMetaInfo2 = new ServerMetaInfo("test", "127.0.0.1", 8082, "1.0.0", "test");
        registry.registry(serverMetaInfo2);
        ServerMetaInfo serverMetaInfo3 = new ServerMetaInfo("test", "127.0.0.1", 8083, "1.0.0", "test");
        registry.registry(serverMetaInfo3);
    }

    @Test
    public void unRegistry()
    {
        ServerMetaInfo serverMetaInfo1 = new ServerMetaInfo("test", "127.0.0.1", 8081, "1.0.0", "test");
        registry.unRegistry(serverMetaInfo1);
    }

    @Test
     public void serverDiscovery() throws Exception {
        ServerMetaInfo serverMetaInfo = new ServerMetaInfo();
        serverMetaInfo.setServerName("test");
        serverMetaInfo.setServerVersion("1.0.0");
        String serverKey = serverMetaInfo.getServerKey();
        List<ServerMetaInfo> serverMetaInfos = registry.serverDiscovery(serverKey);
        if (CollUtil.isEmpty(serverMetaInfos)){
            throw new RuntimeException("未匹配到服务");
        }
        ServerMetaInfo serverMetaInfo1 = serverMetaInfos.get(0);
        System.out.println(serverMetaInfo1);
    }

    @Test
    public void heardBeat() throws Exception {
        registry();
        Thread.sleep(60 * 1000L);
    }


}
