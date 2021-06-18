package com.ljs.springbootsearchnotes.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.stereotype.Service;

/**
 * Created by lijunsai on 2021/06/15
 */
@Service
public class BeanUtils implements BeanFactoryAware {
    // Spring的bean工厂
    private static BeanFactory beanFactory;
    @Override
    public void setBeanFactory(BeanFactory factory) throws BeansException {
        beanFactory=factory;
    }
    public static<T> T getBean(Class<T> aClass){
        return (T) beanFactory.getBean(aClass);
    }
}
