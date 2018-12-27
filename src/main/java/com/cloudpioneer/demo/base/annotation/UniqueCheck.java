package com.cloudpioneer.demo.base.annotation;

import java.lang.annotation.*;

/**
 * 字段唯一性校验注解
 * @author jiangyunjun
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
@Inherited
public @interface UniqueCheck {
    Class type();
    String[] field() default "name";
}
