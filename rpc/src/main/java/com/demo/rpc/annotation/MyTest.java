package com.demo.rpc.annotation;
import java.lang.annotation.*;

/**
 * @description：MyTest
 * @author：GJF
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MyTest {
    String value() default "";
}