package com.demo.rpc.annotation;
import java.lang.annotation.*;

/**
 * @description：MyRPC
 * @author：GJF
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyRPC {
    String value() default "";
}