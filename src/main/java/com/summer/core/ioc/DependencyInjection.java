package com.summer.core.ioc;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Map;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Supplier;
import java.util.stream.Stream;

@Slf4j
public class DependencyInjection {
    public static void inject(String[] packageNames) {
        AutowiredBeanInitialization autowiredBeanInitialization = new AutowiredBeanInitialization(packageNames);
        Map<String, Object> beans = BeanFactory.BEANS;
        if (beans.size() > 0) {
            BeanFactory.BEANS.values().forEach(autowiredBeanInitialization::initialize);
        }
    }
}
