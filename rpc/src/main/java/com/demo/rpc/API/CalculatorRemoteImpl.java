package com.demo.rpc.API;

import com.demo.rpc.Annotation.MyRPC;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: gjf
 * @Date: 2021/05/23/19:23
 * @Description:
 */
@MyRPC("CalculatorRemoteImpl")
public class CalculatorRemoteImpl implements Calculator {

    @Override
    public int add(int a, int b) {
        return a + b;
    }
}
