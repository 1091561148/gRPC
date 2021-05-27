package com.demo.rpc.annotation;
import java.lang.annotation.*;

/**
 * @description：MyRPC
 * @author：GJF
 */

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyAutowired {
    String value() default "";
}