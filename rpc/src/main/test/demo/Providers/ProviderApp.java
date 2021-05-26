package demo.Providers;

import com.demo.rpc.API.CalculatorRemoteImpl;
import com.demo.rpc.API.Calculator;
import com.demo.rpc.RPC.RPCObject;
import com.demo.rpc.Reflex.Reflex;
import com.demo.rpc.Serialization.KryoSerialization;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: gjf
 * @Date: 2021/05/23/19:25
 * @Description:
 */
public class ProviderApp {

    private Calculator calculator = new CalculatorRemoteImpl();

    public static void main(String[] args) throws IOException {
        new ProviderApp().run();
    }

    private void run() throws IOException {
        Reflex reflex = new Reflex();
        reflex.doScanner("com.demo.rpc");
        reflex.doInstance();
        ServerSocket listener = new ServerSocket(8080);
        try {
            while (true) {
                Socket socket = listener.accept();
                try {
                    ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                    Object object = objectInputStream.readObject();

                    System.out.println("request is {}" + object);
                    RPCObject rpcObject = null;
                    // 将请求反序列化
                    if (object instanceof byte[]) {
                        byte[] oo = (byte[]) object;
                        KryoSerialization kryoSerialization = new KryoSerialization();
                        rpcObject = kryoSerialization.deserialize(oo, RPCObject.class);
                    } else {
                        return;
                    }

                    // 调用服务
                    int result = 0;
                    Object myClass = Reflex.test(rpcObject.getMyClass());
                    Class<?> clazz = myClass.getClass();
                    List<Object> paramValues = rpcObject.getParamValues();
                    Object [] paramValueObjs= new Object[paramValues.size()];
                    for (int i = 0; i < paramValues.size(); i ++){
                        paramValueObjs[i] = paramValues.get(i);
                    }
                    Method method = clazz.getMethod(rpcObject.getMethod(), rpcObject.getReturnTypes());
                    Object resultObj = method.invoke(myClass, paramValueObjs);
                    if(resultObj instanceof Integer){
                        result = (Integer) resultObj;
                    } else {
                        throw new UnsupportedOperationException();
                    }

                    // 返回结果
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    objectOutputStream.writeObject(result);
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("fail:"+e.getMessage());
                } finally {
                    socket.close();
                }
            }
        } finally {
            listener.close();
        }
    }
}
