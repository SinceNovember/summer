package com.summer.core.aop.factory;

import com.summer.core.aop.intercept.BeanPostProcessor;
import com.summer.core.aop.intercept.CglibAopProxyBeanProcessor;
import com.summer.core.aop.intercept.JdkAopProxyBeanPostProcessor;
import com.summer.core.aop.proxy.CglibAspectProxy;
import com.summer.core.aop.proxy.JdkAspectProxy;

public class AopProxyBeanPostProcessorFactory {

    /**
     * @param clazz 目标类
     * @return beanClass 实现了接口就返回jdk动态代理bean后置处理器,否则返回 cglib动态代理bean后置处理器
     */
    public static BeanPostProcessor get(Class<?> clazz){
        if (clazz.isInterface() || clazz.getInterfaces().length > 0) {
            return new JdkAopProxyBeanPostProcessor();
        } else {
            return new CglibAopProxyBeanProcessor();
        }

    }
}
