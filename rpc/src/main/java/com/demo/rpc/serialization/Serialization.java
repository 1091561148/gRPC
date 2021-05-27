package com.demo.rpc.serialization;

import java.io.IOException;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: gjf
 * @Date: 2021/05/23/21:37
 * @Description:
 */
public interface Serialization {

    /**
     * @param obj
     * @return
     * @throws IOException
     */
    byte[] serialize(Object obj) throws IOException;

    /**
     * @param bytes
     * @param clz
     * @param <T>
     * @return
     * @throws IOException
     */
    <T> T deserialize(byte[] bytes, Class<T> clz) throws IOException;
}