package com.demo.rpc.rpc.netty;

import com.demo.rpc.rpc.InvokerProtocol;
import com.demo.rpc.IocApplication;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * netty read msg
 *
 * @Author: gjf
 * @Date: 2021/05/27/20:28
 * @Description:
 */
public class RegistryHandler extends ChannelInboundHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        InvokerProtocol invokerProtocol = (InvokerProtocol) msg;
        // 调用服务，从IOC容器中得到实例对象
        Object classObject = IocApplication.getBean(invokerProtocol.getClassName());
        Class<?> clazz = classObject.getClass();
        //得到所有参数
        Object[] paramValues = invokerProtocol.getParamValues();
        //获取对应的方法
        Method method = clazz.getMethod(invokerProtocol.getMethodName(), invokerProtocol.getParameterTypes());
        //通过反射调用
        Object result = method.invoke(classObject, paramValues);
        ctx.write(result);
        ctx.flush();
        ctx.close();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
