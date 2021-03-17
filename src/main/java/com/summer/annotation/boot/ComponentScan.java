package com.summer.annotation.boot;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface  ComponentScan {
    public abstract String[] value() default {};
}
