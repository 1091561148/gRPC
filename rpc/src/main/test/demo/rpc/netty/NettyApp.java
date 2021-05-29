package demo.rpc.netty;

import com.demo.rpc.annotation.MyAutowired;
import com.demo.rpc.annotation.MyTest;
import com.demo.rpc.api.Calculator;
import com.demo.rpc.registry.zk.ZkServiceDiscoveryImpl;
import com.demo.rpc.registry.zk.ZkServiceRegistryImpl;
import com.demo.rpc.rpc.netty.CalculateNettyRequest;
import com.demo.rpc.IocApplication;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.InetSocketAddress;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: gjf
 * @Date: 2021/05/27/22:49
 * @Description:
 */
@MyTest
public class NettyApp {

    @BeforeClass
    public static void reflex(){
        IocApplication iocApplication = new IocApplication(IocApplication.class.getPackage().getName(), new CalculateNettyRequest());
        //添加 测试 代码的 autowired 扫描路径
        iocApplication.doScanner(Thread.currentThread().getContextClassLoader(), NettyApp.class.getPackage().getName());
        iocApplication.init();
    }


    @Test
    public void test(){
        ZkServiceRegistryImpl zkServiceRegistry = new ZkServiceRegistryImpl();
        zkServiceRegistry.registerService("test", new InetSocketAddress("127.0.0.1", 8080));
        zkServiceRegistry.registerService("test", new InetSocketAddress("127.0.0.1", 8081));

        ZkServiceDiscoveryImpl zkServiceDiscovery = new ZkServiceDiscoveryImpl();
        InetSocketAddress k = zkServiceDiscovery.lookupService("test");
        System.out.println("ip:"+k.getAddress()+"-port:"+k.getPort());
    }
    @MyAutowired
    public Calculator calculator;

    @Test
    public void customer(){
        NettyApp a = (NettyApp) IocApplication.getBean("demo.rpc.netty.NettyApp");
        Calculator tet = a.calculator;
        if(tet == null){
            System.out.println("calculator is null");
            return;
        }
        int response =  tet.add(1,2);
        System.out.println("result:" + response);
    }

    @Test
    public void provider(){
        new CalculateNettyRequest().rpcResponse();
    }
}
