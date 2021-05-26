package com.demo.rpc.RPC;

import com.demo.rpc.Serialization.KryoSerialization;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: gjf
 * @Date: 2021/05/23/19:35
 * @Description:
 */
public class CalculateRpcRequest {
    private final static int PROT = 8080;
    private String chooseTarget(){
        String address = "127.0.0.1";
        return address;
    }

    public byte[] generateRequest(RPCObject rpc){
        KryoSerialization kryoSerialization = new KryoSerialization();
        return kryoSerialization.serialize(rpc);
    }

    public int getResponse(RPCObject rpc){
        //List<String> addressList = lookupProviders("Calculator.add");
        String address = chooseTarget();
        try {
            Socket socket = new Socket(address, PROT);

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
            System.out.println("fail"+e.getMessage());
            throw new InternalError();
        }
    }
}
