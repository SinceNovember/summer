package com.summer.core.aop.intercept;

import com.summer.core.aop.proxy.CglibAspectProxy;

public class CglibAopProxyBeanProcessor extends AbstractAopProxyBeanPostProcessor{
    @Override
    public Object wrapBean(Object target, Interceptor interceptor) {
        return CglibAspectProxy.wrap(target, interceptor);
    }
}
