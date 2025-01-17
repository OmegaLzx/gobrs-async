package com.gobrs.async.spring;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


public class GobrsSpring implements ApplicationContextAware, BeanFactoryPostProcessor {

    public static ApplicationContext applicationContext;
    private static ConfigurableListableBeanFactory cf;

    //获取applicationContext
    public static ApplicationContext getApplicationContext() {
        return applicationContext;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (GobrsSpring.applicationContext == null) {
            GobrsSpring.applicationContext = applicationContext;
        }
    }

    //通过name获取 Bean.
    public static Object getBean(String name) {
        return getApplicationContext() == null ? cf.getBean(name) : getApplicationContext().getBean(name);
    }

    //通过class获取Bean.
    public static <T> T getBean(Class<T> clazz) {
        ApplicationContext applicationContext = getApplicationContext();
        if (applicationContext == null) {
            return cf.getBean(clazz);
        }
        return applicationContext.getBean(clazz);
    }

    //通过name,以及Clazz返回指定的Bean
    public static <T> T getBean(String name, Class<T> clazz) {
        return getApplicationContext().getBean(name, clazz);
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
        cf = beanFactory;
    }
}
