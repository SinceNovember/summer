package com.summer.core.aop.intercept;

import com.summer.core.aop.factory.InterceptorFactory;

/**
 * Bean 后置处理器抽象基类
 */
public abstract class AbstractAopProxyBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean) {
        Object wrapperProxyBean = bean;

        //链式包装目标类
        for (Interceptor interceptor : InterceptorFactory.getInterceptors()) {
            if (!interceptor.supports(bean)) {
                wrapperProxyBean = wrapBean(wrapperProxyBean, interceptor);
            }
        }
        return wrapperProxyBean;
    }

    public abstract Object wrapBean(Object target, Interceptor interceptor);
}
