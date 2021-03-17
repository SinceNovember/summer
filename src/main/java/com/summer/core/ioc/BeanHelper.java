package com.summer.core.ioc;

import com.summer.annotation.ioc.Component;

import java.util.Arrays;
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class BeanHelper {

    /**
     * 获取Bean名称
     * @param clazz
     * @return
     */
    public static String getBeanName(Class<?> clazz){
        String beanName = clazz.getName();
        if (clazz.isAnnotationPresent(Component.class)) {
            Component component = clazz.getAnnotation(Component.class);
            beanName = "".equals(component.name()) ? clazz.getName() : component.name();
        }
        return beanName;
    }
}
