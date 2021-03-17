package com.summer.core.ioc;

import com.summer.annotation.ioc.Component;
import com.summer.annotation.springmvc.RestController;
import com.summer.common.util.ReflectionUtil;
import com.summer.core.aop.factory.AopProxyBeanPostProcessorFactory;
import com.summer.core.aop.intercept.BeanPostProcessor;
import com.summer.core.config.ConfigurationFactory;
import com.summer.core.config.ConfigurationManager;
import com.summer.exception.DoGetBeanException;
import com.summer.factory.ClassFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

;

public class BeanFactory {

    //bean容器
    public static final Map<String, Object> BEANS = new ConcurrentHashMap<>(128);

    private static final Map<String, String[]> SINGLE_BEAN_NAMES_TYPE_MAP = new ConcurrentHashMap<>(128);

    public static void loadBeans(){
        //加载Component的bean
        ClassFactory.CLASSES.get(Component.class).forEach(clazz -> {
            //获取注解的name,没有则获取Class的名称
            String beanName = BeanHelper.getBeanName(clazz);
            Object obj = ReflectionUtil.newInstance(clazz);
            BEANS.put(beanName, obj);
        });
        //RestController
        ClassFactory.CLASSES.get(RestController.class).forEach(clazz -> {
            Object obj = ReflectionUtil.newInstance(clazz);
            BEANS.put(clazz.getName(), obj);
        });
        BEANS.put(ConfigurationManager.class.getName(), new ConfigurationManager(ConfigurationFactory.getConfig()));

    }

    /**
     * 应用Bean后置处理器
     */
    public static void applyBeanPostProcessors(){
        BEANS.replaceAll((beanName, beanInstance)->{
            BeanPostProcessor beanPostProcessor = AopProxyBeanPostProcessorFactory.get(beanInstance.getClass());
            return beanPostProcessor.postProcessAfterInitialization(beanInstance);
        });
    }

    public static <T> T getBean(Class<T> type) {
        String[] beanNames = getBeanNamesForType(type);
        if (beanNames.length == 0) {
            throw new DoGetBeanException("not fount bean implement，the bean :" + type.getName());
        }
        Object beanInstance = BEANS.get(beanNames[0]);
        if (!type.isInstance(beanInstance)) {
            throw new DoGetBeanException("not fount bean implement，the bean :" + type.getName());
        }
        return type.cast(beanInstance);
    }

    public static <T> Map<String, T> getBeansOfType(Class<T> type) {
        Map<String, T> result = new HashMap<>();
        String[] beanNames = getBeanNamesForType(type);
        for (String beanName : beanNames) {
            Object beanInstance = BEANS.get(beanName);
            if (!type.isInstance(beanInstance)) {
                throw new DoGetBeanException("not fount bean implement，the bean :" + type.getName());
            }
            result.put(beanName, type.cast(beanInstance));
        }
        return result;
    }



    /**
     *    根据类型获取Bean实现类的名称和别名
     * @param type
     * @return
     */
    private static String[] getBeanNamesForType(Class<?> type) {
     String beanName = type.getName();
        String[] beanNames = SINGLE_BEAN_NAMES_TYPE_MAP.get(beanName);
        if (beanNames == null) {
            List<String> beanNamesList = new ArrayList<>();
            for (Map.Entry<String, Object> beanEntry : BEANS.entrySet()) {
                Class<?> beanClass = beanEntry.getValue().getClass();
                if (type.isInterface()) {
                    Class<?>[] interfaces = beanClass.getInterfaces();
                    for (Class<?> c : interfaces) {
                        if (type.getName().equals(c.getName())) {
                            beanNamesList.add(beanEntry.getKey());
                            break;
                        }
                    }
                    //baanClass是type的父类或者相同类型
                } else if(beanClass.isAssignableFrom(type)){
                    beanNamesList.add(beanEntry.getKey());
                }
            }
            beanNames = beanNamesList.toArray(new String[0]);
            SINGLE_BEAN_NAMES_TYPE_MAP.put(beanName, beanNames);
        }
        return beanNames;
    }

}
