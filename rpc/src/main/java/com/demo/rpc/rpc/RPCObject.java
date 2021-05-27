package com.demo.rpc.rpc;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: gjf
 * @Date: 2021/05/23/22:25
 * @Description:
 */
public class RPCObject {
    private String method;
    private Object[] paramValues;
    private String myClass;
    private Class<?> returnTypes;
    private Class[] parameterTypes;

    public Class[] getParameterTypes() {
        return this.parameterTypes;
    }

    public void setParameterTypes(Class[] parameterTypes) {
        this.parameterTypes = parameterTypes;
    }

    public Class<?> getReturnTypes() {
        return returnTypes;
    }

    public void setReturnTypes(Class<?> returnTypes) {
        this.returnTypes = returnTypes;
    }

    public String getMyClass() {
        return myClass;
    }

    public void setMyClass(String myClass) {
        this.myClass = myClass;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object[] getParamValues() {
        return paramValues;
    }

    public void setParamValues(Object[] paramValues) {
        this.paramValues = paramValues;
    }
}
