package com.demo.rpc.reflex;

import com.demo.rpc.rpc.CalculateRequest;
import com.demo.rpc.rpc.InvokerProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.lang.reflect.*;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: gjf
 * @Date: 2021/05/23/19:50
 * @Description:
 */
public class JdkProxy {

    private final static Logger logger = LoggerFactory.getLogger(JdkProxy.class);

    /**
     * Description:JDK 动态反射
     * return: void
     */
    @SuppressWarnings("unchecked")
    public <T> T getProxy(CalculateRequest request, String beanName, Class<T> clazz, Object o) {
        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class<?>[]{clazz},  (proxy, method, args) -> {
            logger.info("method:" + method.getName());
            //排除toString hashCode
            if (Object.class.equals(method.getDeclaringClass())) {
                return method.invoke(o, args);
            }

            InvokerProtocol rpc = new InvokerProtocol();
            //方法名：findAll
            rpc.setMethodName(method.getName());
            //接口对象的实现类
            rpc.setClassName(beanName);
            //准备参数2 ： params:args
            rpc.setParamValues(args);
            rpc.setParameterTypes(method.getParameterTypes());

            return request.rpcInvoke(rpc);
        });
    }
}
