package com.summer.core;

import com.summer.annotation.boot.ComponentScan;
import com.summer.common.Banner;
import com.summer.core.aop.factory.InterceptorFactory;
import com.summer.core.boot.ApplicationRunner;
import com.summer.core.config.Configuration;
import com.summer.core.config.ConfigurationManager;
import com.summer.core.ioc.BeanFactory;
import com.summer.core.ioc.DependencyInjection;
import com.summer.core.springmvc.factory.RouteMethodMapper;
import com.summer.factory.ClassFactory;
import com.summer.server.HttpServer;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;

public class ApplicationContext {
    private static final ApplicationContext APPLICATION_CONTEXT = new ApplicationContext();

    public void run(Class<?> applicationClass) {
        //打印Banner
        Banner.print();
        //解析包，获取加载bean包
        String[] packageNames = getPackageNames(applicationClass);
        //加载自定义注解的所有类
        ClassFactory.loadClass(packageNames);
        //加载mvc的路由
        RouteMethodMapper.loadRoutes();
        //加载所有Bean
        BeanFactory.loadBeans();
        //加载所有属性配置
        loadResources(applicationClass);
        //加载所有拦截器
        InterceptorFactory.loadInterceptors(packageNames);
        //bean注入
        DependencyInjection.inject(packageNames);
        // 为所有bean应用后置处理器
        BeanFactory.applyBeanPostProcessors();
        // 执行一些回调事件
        callRunners();
    }

    public static ApplicationContext getApplicationContext() {
        return APPLICATION_CONTEXT;
    }

    private static String[] getPackageNames(Class<?> applicationClass) {
        ComponentScan componentScan = applicationClass.getAnnotation(ComponentScan.class);
        return !Objects.isNull(componentScan) ? componentScan.value()
                : new String[]{applicationClass.getPackage().getName()};
    }

    private void loadResources(Class<?> applicationClass){
        ClassLoader classLoader = applicationClass.getClassLoader();
        List<Path> filePaths = new ArrayList<>();
        for (String configName : Configuration.DEFAULT_CONFIG_NAMES) {
            URL url = classLoader.getResource(configName);
            if (!Objects.isNull(url)) {
                try {
                    filePaths.add(Paths.get(url.toURI()));
                }catch (URISyntaxException ignored){

                }
            }
        }
        ConfigurationManager configurationManager = BeanFactory.getBean(ConfigurationManager.class);
        configurationManager.loadResources(filePaths);
    }

    private void callRunners(){
        //获取实现了ApplicationRunner的Bean类
        List<ApplicationRunner> runners = new ArrayList<>(BeanFactory.getBeansOfType(ApplicationRunner.class).values());
        //最后开启Web服务器
        runners.add(() -> {
            HttpServer httpServer = new HttpServer();
            httpServer.start();
        });
        //执行所有run方法，最后启动服务器
        for (Object runner : new LinkedHashSet<>(runners)) {
            ((ApplicationRunner) runner).run();
        }
    }

}
