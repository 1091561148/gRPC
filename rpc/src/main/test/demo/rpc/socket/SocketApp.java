package demo.rpc.socket;

import com.demo.rpc.api.Calculator;
import com.demo.rpc.annotation.MyAutowired;
import com.demo.rpc.annotation.MyTest;
import com.demo.rpc.rpc.socket.CalculateRpcRequest;
import com.demo.rpc.IocApplication;
import org.junit.BeforeClass;
import org.junit.Test;

@MyTest
public class SocketApp {

    @BeforeClass
    public static void reflex(){
        IocApplication iocApplication = new IocApplication(IocApplication.class.getPackage().getName(), new CalculateRpcRequest());
        //添加 测试 代码的 autowired 扫描路径
        iocApplication.doScanner(Thread.currentThread().getContextClassLoader(), SocketApp.class.getPackage().getName());
        iocApplication.init();
    }

    @MyAutowired
    public Calculator calculator;

    @Test
    public void customer() {
        SocketApp a = (SocketApp) IocApplication.getBean("demo.rpc.socket.SocketApp");
        Calculator tet = a.calculator;
        if(tet == null){
            System.out.println("calculator is null");
            return;
        }
        //int response =  tet.add(1,2);
        String response = tet.div("hello-","world");
        System.out.println("result:" + response);
    }

    @Test
    public void provider() {
        new CalculateRpcRequest().rpcResponse();
    }
}
