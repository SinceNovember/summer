package com.summer.core.ioc;

import com.summer.annotation.config.Value;
import com.summer.annotation.ioc.Autowired;
import com.summer.annotation.ioc.Qualifier;
import com.summer.common.util.ObjectUtil;
import com.summer.common.util.ReflectionUtil;
import com.summer.core.aop.factory.AopProxyBeanPostProcessorFactory;
import com.summer.core.aop.intercept.BeanPostProcessor;
import com.summer.core.config.ConfigurationManager;
import com.summer.exception.CanNotDetermineTargetBeanException;
import com.summer.exception.InterfaceNotHaveImplementedClassException;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 自动转配Bean初始化
 */
public class AutowiredBeanInitialization {

    private final String[] packageNames;

    public AutowiredBeanInitialization(String[] packageNames) {
        this.packageNames = packageNames;
    }

    //二级缓存（解决循环依赖问题）
    private static final Map<String, Object> SINGLETON_OBJECTS = new ConcurrentHashMap<>(64);

    public void initialize(Object beanInstance) {
        Class<?> beanClass = beanInstance.getClass();
        Field[] beanFields = beanClass.getDeclaredFields();
        if (beanFields.length > 0) {
            for (Field beanField : beanFields) {
                if (beanField.isAnnotationPresent(Autowired.class)) {
                    Object beanFieldInstance = processAutowiredAnnotationField(beanField);
                    String beanFieldName = BeanHelper.getBeanName(beanField.getType());
                    // 解决循环依赖问题
                    beanFieldInstance = resolveCircularDependency(beanInstance, beanFieldInstance, beanFieldName);
                    //AOP
                    BeanPostProcessor beanPostProcessor = AopProxyBeanPostProcessorFactory.get(beanField.getType());
                    beanFieldInstance = beanPostProcessor.postProcessAfterInitialization(beanFieldInstance);
                    ReflectionUtil.setField(beanInstance, beanField, beanFieldInstance);
                }


                //如果有@Value注解的话 把配置值赋到字段上
                if (beanField.isAnnotationPresent(Value.class)) {
                    Object convertedValue = processValueAnnotationField(beanField);
                    ReflectionUtil.setField(beanInstance, beanField, convertedValue);
                }
            }
        }

    }

    /**
     * 处理被 @Autowired 注解标记的字段
     *
     * @param beanField 目标类的字段
     * @return 目标类的字段对应的对象
     */
    private Object processAutowiredAnnotationField(Field beanField) {
        Class<?> beanFieldClass = beanField.getClass();
        String beanFieldName = BeanHelper.getBeanName(beanFieldClass);
        Object beanFieldInstance;
        if (beanFieldClass.isInterface()) {
            //获取对应字段为接口的所有子类
            @SuppressWarnings("unchecked")
            Set<Class<?>> subClasses = ReflectionUtil.getSubClass(packageNames, (Class<Object>) beanFieldClass);
            if (subClasses.size() == 0) {
                throw new InterfaceNotHaveImplementedClassException(beanFieldClass.getName() + "is interface and do not have implemented class exception");
            }
            if (subClasses.size() == 1) {
                Class<?> subClass = subClasses.iterator().next();
                beanFieldName = BeanHelper.getBeanName(subClass);
            }
            if (subClasses.size() > 1) {
                Qualifier qualifier = beanField.getDeclaredAnnotation(Qualifier.class);
                beanFieldName = qualifier == null ? beanFieldName : qualifier.value();
            }
        }
        beanFieldInstance = BeanFactory.BEANS.get(beanFieldName);
        if (beanFieldInstance == null) {
            throw new CanNotDetermineTargetBeanException("can not determine target bean of" + beanFieldClass.getName());
        }
        return beanFieldInstance;

    }

    /**
     * 处理被 @Value 注解标记的字段
     *
     * @param beanField 目标类的字段
     * @return 目标类的字段对应的对象
     */
    private Object processValueAnnotationField(Field beanField) {
        String key = beanField.getDeclaredAnnotation(Value.class).value();
        ConfigurationManager configurationManager = (ConfigurationManager) BeanFactory.BEANS.get(ConfigurationManager.class.getName());
        //获取@Value中的值
        String value = configurationManager.getString(key);
        if (value == null) {
            throw new IllegalArgumentException("can not find target value for property:{" + key + "}");
        }
        return ObjectUtil.convert(beanField.getType(), value);
    }

    /**
     * 二级缓存解决循环依赖问题
     */
    private Object resolveCircularDependency(Object beanInstance, Object beanFieldInstance, String beanFieldName) {
        if (SINGLETON_OBJECTS.containsKey(beanFieldName)) {
            beanFieldInstance = SINGLETON_OBJECTS.get(beanFieldName);
        } else {
            SINGLETON_OBJECTS.put(beanFieldName, beanFieldInstance);
            initialize(beanInstance);
        }
        return beanFieldInstance;
    }
}
