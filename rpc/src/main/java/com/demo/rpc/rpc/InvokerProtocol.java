package com.demo.rpc.rpc;

import java.io.Serializable;

/**
 * 自定义传输协议
 *
 * @Author: gjf
 * @Date: 2021/05/23/22:25
 * @Description:
 */
public class InvokerProtocol implements Serializable {
    private static final long serialVersionUID = 1905122041950251207L;
    /**
     * 方法名
     */
    private String methodName;
    /**
     * 参数列表
     */
    private Object[] paramValues;
    /**
     * 类名
     */
    private String className;
    /**
     * 参数类型
     */
    private Class[] parameterTypes;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Object[] getParamValues() {
        return paramValues;
    }

    public void setParamValues(Object[] paramValues) {
        this.paramValues = paramValues;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public Class[] getParameterTypes() {
        return parameterTypes;
    }

    public void setParameterTypes(Class[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }
}
