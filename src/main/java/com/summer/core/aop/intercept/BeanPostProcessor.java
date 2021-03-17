package com.summer.core.aop.intercept;

public interface BeanPostProcessor {
    default Object postProcessAfterInitialization(Object bean) {
        return bean;
    }
}
