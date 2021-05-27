package demo.socket;

import com.demo.rpc.api.Calculator;
import com.demo.rpc.annotation.MyAutowired;
import com.demo.rpc.annotation.MyTest;
import com.demo.rpc.rpc.CalculateRpcRequest;
import com.demo.rpc.reflex.Reflex;
import org.junit.BeforeClass;
import org.junit.Test;

@MyTest
public class SocketApp {

    @BeforeClass
    public static void reflex(){
        Reflex reflex = new Reflex("com.demo.rpc");
        reflex.doScanner(Thread.currentThread().getContextClassLoader(),"demo.socket");
        reflex.init();
    }

    @MyAutowired
    public Calculator calculator;

    @Test
    public void customer() {
        SocketApp a = (SocketApp)Reflex.getBean("demo.socket.SocketApp");
        Calculator tet = a.calculator;
        if(tet == null){
            System.out.println("calculator is null");
            return;
        }
        int response =  tet.add(1,2);
        System.out.println("result:" + response);
    }

    @Test
    public void provider() {
        new CalculateRpcRequest().postRequest();
    }

}
