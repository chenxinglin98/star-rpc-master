package com.star.annotation;


import java.lang.annotation.*;

/**
 * RPC reference annotation, autowire the service implementation class
 * 标记服务消费端成员
 *
 * @author smile2coder
 * @createTime 2020年09月16日 21:42:00
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
@Inherited
public @interface RpcReference {

    /**
     * Service version, default value is empty string
     */
    String version() default "";

    /**
     * Service group, default value is empty string
     */
    String group() default "";

}