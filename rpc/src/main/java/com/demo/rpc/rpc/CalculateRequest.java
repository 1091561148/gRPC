package com.demo.rpc.rpc;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: gjf
 * @Date: 2021/05/27/18:34
 * @Description:
 */
public interface CalculateRequest {
    /**
     * 发送RPC request
     * @param rpc
     * @return
     */
    Object rpcInvoke(InvokerProtocol rpc);
    /**
     * 处理请求
     */
    void rpcResponse();
}
