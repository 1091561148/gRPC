package com.demo.rpc.api;


import com.demo.rpc.annotation.MyRPC;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: gjf
 * @Date: 2021/05/23/19:23
 * @Description:
 */
@MyRPC
public class CalculatorRemoteImpl implements Calculator {

    @Override
    public int add(int a, int b) {
        return a + b;
    }

    @Override
    public String div(String a, String b) {
        return a + b;
    }
}
