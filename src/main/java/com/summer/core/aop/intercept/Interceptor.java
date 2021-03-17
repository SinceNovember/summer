package com.summer.core.aop.intercept;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Interceptor {
    public int order = -1;

    public boolean supports(Object bean) {
        return false;
    }

    public abstract Object intercept(MethodInvocation invocation);
}
