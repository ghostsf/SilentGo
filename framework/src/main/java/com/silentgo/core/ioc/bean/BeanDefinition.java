package com.silentgo.core.ioc.bean;

import com.silentgo.config.Const;
import com.silentgo.core.ioc.annotation.Inject;
import com.silentgo.kit.CGLib;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Project : silentgo
 * com.silentgo.core.ioc.bean
 *
 * @author <a href="mailto:teddyzhu15@gmail.com" target="_blank">teddyzhu</a>
 *         <p>
 *         Created by teddyzhu on 16/7/20.
 */
public class BeanDefinition extends BeanWrapper {

    String beanName;

    Class<?> clz;

    Object target;

    Object proxyTarget;

    Map<String, Field> fieldBeans;


    boolean injectComplete = false;

    public boolean isInjectComplete() {
        return injectComplete;
    }

    public void setInjectComplete(boolean injectComplete) {
        this.injectComplete = injectComplete;
    }

    public BeanDefinition(Class<?> clz) {
        Create(clz.getName(), clz);
    }

    public BeanDefinition(String beanName, Class<?> clz) {
        Create(beanName, clz);
    }

    private void Create(String beanName, Class<?> clz) {
        this.beanName = beanName;
        this.clz = clz;
        try {
            target = clz.newInstance();
            proxyTarget = CGLib.Proxy(target);
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        fieldBeans = new HashMap<>();
        Field[] fields = clz.getDeclaredFields();
        for (Field field : fields) {
            Inject inject = field.getAnnotation(Inject.class);
            if (inject != null) {
                if (Const.DEFAULT_NONE.equals(inject.value()))
                    fieldBeans.put(field.getDeclaringClass().getName(), field);
                else
                    fieldBeans.put(inject.value(), field);
            }
        }

    }

    public Map<String, Field> getFieldBeans() {
        return fieldBeans;
    }

    @Override
    public Object getBean() {
        return proxyTarget;
    }

    @Override
    public Class<?> getBeanClass() {
        return clz;
    }

    @Override
    public String getBeanName() {
        return beanName;
    }

    public Object getTarget() {
        return target;
    }
}
