package com.summer.core.aop.factory;

import com.summer.annotation.aop.Aspect;
import com.summer.common.util.ReflectionUtil;
import com.summer.core.aop.intercept.Interceptor;
import com.summer.exception.CannotInitializeConstructorException;
import com.summer.factory.ClassFactory;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 存放所有拦截器
 */
public class InterceptorFactory {
    private static List<Interceptor> interceptors = new ArrayList<>();

    public static void loadInterceptors(String[] packageName) {
        // 获取指定包中实现了 Interceptor 接口的类
        Set<Class<? extends Interceptor>> interceptorClasses = ReflectionUtil.getSubClass(packageName, Interceptor.class);

        // 获取被 @Aspect 标记的类
        Set<Class<?>> aspects = ClassFactory.CLASSES.get(Aspect.class);

        interceptorClasses.forEach(interceptorClass -> {
            try {
                interceptors.add(interceptorClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                throw new CannotInitializeConstructorException("not init constructor , the interceptor name :" + interceptorClass.getSimpleName());
            }
        });

        //根据order进行排序
        interceptors = interceptors.stream().sorted(Comparator.comparing(Interceptor::getOrder)).collect(Collectors.toList());
    }

    public static List<Interceptor> getInterceptors() {
        return interceptors;
    }
}
