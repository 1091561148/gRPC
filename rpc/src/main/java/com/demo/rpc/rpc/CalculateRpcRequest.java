package com.demo.rpc.rpc;

import com.demo.rpc.reflex.Reflex;
import com.demo.rpc.serialization.KryoSerialization;
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
public class CalculateRpcRequest {
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

    private byte[] generateRequest(RPCObject rpc){
        KryoSerialization kryoSerialization = new KryoSerialization();
        return kryoSerialization.serialize(rpc);
    }

    /**
     * 发送 请求
     * @param rpc
     * @return
     */
    public int getResponse(RPCObject rpc){
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

            if (response instanceof Integer) {
                return (Integer) response;
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
    public void postRequest(){
        try(ServerSocket listener = new ServerSocket(PORT)) {
            while (true) {
                try(Socket socket = listener.accept()) {
                    ObjectInputStream objectInputStream = new ObjectInputStream(socket.getInputStream());
                    Object object = objectInputStream.readObject();

                    RPCObject rpcObject;
                    // 将请求反序列化
                    if (object instanceof byte[]) {
                        byte[] oo = (byte[]) object;
                        KryoSerialization kryoSerialization = new KryoSerialization();
                        rpcObject = kryoSerialization.deserialize(oo, RPCObject.class);
                        logger.info("request is {}", rpcObject);
                    } else {
                        return;
                    }

                    // 调用服务
                    int result = 0;
                    Object myClass = Reflex.getBean(rpcObject.getMyClass());
                    Class<?> clazz = myClass.getClass();
                    Object[] paramValues = rpcObject.getParamValues();
                    Method method = clazz.getMethod(rpcObject.getMethod(), rpcObject.getParameterTypes());
                    Object resultObj = method.invoke(myClass, paramValues);
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
                    logger.error("postRequest fail{}", e.getMessage());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
