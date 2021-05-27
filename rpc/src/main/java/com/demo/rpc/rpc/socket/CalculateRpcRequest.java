package com.demo.rpc.rpc.socket;

import com.demo.rpc.rpc.CalculateRequest;
import com.demo.rpc.rpc.InvokerProtocol;
import com.demo.rpc.serialization.KryoSerialization;
import com.demo.rpc.IocApplication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: gjf
 * @Date: 2021/05/23/19:35
 * @Description:
 */
public class CalculateRpcRequest implements CalculateRequest {
    private final static Logger logger = LoggerFactory.getLogger(CalculateRpcRequest.class);
    /**
     * 端口号
     */
    private final static int PORT = 8080;
    private final static String ADDRESS = "127.0.0.1";

    private String chooseTarget(String address){
        if(address == null || "".equals(address))
        {
            return ADDRESS;
        }
        return address;
    }

    private byte[] generateRequest(InvokerProtocol rpc){
        KryoSerialization kryoSerialization = new KryoSerialization();
        return kryoSerialization.serialize(rpc);
    }

    /**
     * 发送 请求
     * @param rpc
     * @return
     */
    @Override
    public Object rpcInvoke(InvokerProtocol rpc){
        String address = chooseTarget(null);
        try {
            Socket socket = new Socket(address, PORT);

            // 将请求序列化
            byte[] calculateRpcRequest = generateRequest(rpc);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());

            // 将请求发给服务提供方
            objectOutputStream.writeObject(calculateRpcRequest);

            // 将响应体反序列化
            ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
            Object response = objectInputStream.readObject();

            if(response != null){
                return response;
            } else {
                throw new InternalError();
            }

        } catch (Exception e) {
            logger.error("getResponse fail{}", e.getMessage());
            throw new InternalError();
        }
    }

    /**
     * 处理请求
     */
    @Override
    public void rpcResponse(){
        try(ServerSocket listener = new ServerSocket(PORT)) {
            while (true) {
                try(Socket socket = listener.accept()) {
                    ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                    Object object = objectInputStream.readObject();

                    InvokerProtocol invokerProtocol;
                    // 将请求反序列化
                    if (object instanceof byte[]) {
                        byte[] oo = (byte[]) object;
                        KryoSerialization kryoSerialization = new KryoSerialization();
                        invokerProtocol = kryoSerialization.deserialize(oo, InvokerProtocol.class);
                        logger.info("request is {}", invokerProtocol);
                    } else {
                        break;
                    }

                    // 调用服务
                    Object myClass = IocApplication.getBean(invokerProtocol.getClassName());
                    Class<?> clazz = myClass.getClass();
                    Object[] paramValues = invokerProtocol.getParamValues();
                    Method method = clazz.getMethod(invokerProtocol.getMethodName(), invokerProtocol.getParameterTypes());
                    Object resultObj = method.invoke(myClass, paramValues);

                    // 返回结果
                    ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                    objectOutputStream.writeObject(resultObj);
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.error("postRequest fail{}", e.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
