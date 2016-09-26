package com.silentgo.core.aop.support;

import com.silentgo.core.SilentGo;
import com.silentgo.core.aop.Interceptor;
import com.silentgo.core.aop.annotation.Intercept;
import com.silentgo.core.build.Factory;
import com.silentgo.core.exception.AppBuildException;
import com.silentgo.core.exception.AppReleaseException;
import com.silentgo.core.ioc.bean.BeanDefinition;
import com.silentgo.core.ioc.bean.BeanFactory;
import com.silentgo.core.support.BaseFactory;
import com.silentgo.utils.CollectionKit;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Project : silentgo
 * com.silentgo.core.aop.support
 *
 * @author <a href="mailto:teddyzhu15@gmail.com" target="_blank">teddyzhu</a>
 *         <p>
 *         Created by teddyzhu on 16/8/3.
 */
@Factory
public class InterceptFactory extends BaseFactory {

    private Map<Class<? extends Interceptor>, Interceptor> allInterceptors = new HashMap<>();

    private Map<String, List<Interceptor>> classInterceptors = new HashMap<>();


    private Map<Method, List<Interceptor>> methodInterceptors = new HashMap<>();


    public Map<Class<? extends Interceptor>, Interceptor> getAllInterceptors() {
        return allInterceptors;
    }

    public Map<String, List<Interceptor>> getClassInterceptors() {
        return classInterceptors;
    }

    public Map<Method, List<Interceptor>> getMethodInterceptors() {
        return methodInterceptors;
    }

    public boolean addAllInterceltor(Class<? extends Interceptor> name) throws IllegalAccessException, InstantiationException {
        return CollectionKit.MapAdd(allInterceptors, name, name.newInstance());
    }

    public boolean addAllInterceltor(List<Interceptor> interceptors) {
        interceptors.forEach(interceptor -> {
            CollectionKit.MapAdd(allInterceptors, interceptor.getClass(), interceptor);
        });
        return true;
    }

    public boolean addAllInterceltor(Class<? extends Interceptor> name, Interceptor interceptor) {
        return CollectionKit.MapAdd(allInterceptors, name, interceptor);
    }

    public boolean addMethodInterceptor(Method name, Interceptor interceptor) {
        CollectionKit.ListMapAdd(methodInterceptors, name, interceptor);
        return true;
    }

    public boolean addClassInterceptor(String name, Interceptor interceptor) {
        CollectionKit.ListMapAdd(classInterceptors, name, interceptor);
        return true;
    }

    public boolean addClassInterceptor(String name, List<Interceptor> interceptors) {
        CollectionKit.ListMapAdd(classInterceptors, name, interceptors);
        return true;
    }

    @Override
    public boolean initialize(SilentGo me) throws AppBuildException {
        InterceptFactory interceptFactory = new InterceptFactory();
        me.getConfig().addFactory(interceptFactory);

        //build global interceptors
        List<Interceptor> globalInterceptors = me.getConfig().getInterceptors();
        me.getAnnotationManager().getClasses(Intercept.class).forEach(interceptClass -> {
            Intercept intercept = (Intercept) interceptClass.getAnnotation(Intercept.class);
            if (intercept.value().length == 0 &&
                    Interceptor.class.isAssignableFrom(interceptClass)) {
                addIntercept(globalInterceptors, interceptClass);
            } else if (intercept.value().length > 0) {
                for (Class<? extends Interceptor> aClass : intercept.value()) {
                    addIntercept(globalInterceptors, aClass);
                }
            }
        });
        interceptFactory.addAllInterceltor(globalInterceptors);
        BeanFactory beanFactory = me.getFactory(me.getConfig().getBeanClass());


        Map<String, BeanDefinition> beanDefinitionMap = (Map<String, BeanDefinition>) beanFactory.getBeans();
        beanDefinitionMap.forEach((k, v) -> {
            Intercept classIntercept = v.getSourceClass().getAnnotation(Intercept.class);
            if (classIntercept != null && classIntercept.value().length != 0) {
                for (Class<? extends Interceptor> aClass : classIntercept.value()) {
                    interceptFactory.addClassInterceptor(v.getSourceClass().getName(), getInterceptor(interceptFactory, aClass));
                }
            }
            Method[] methods = v.getSourceClass().getDeclaredMethods();
            for (Method method : methods) {
                Intercept intercept = method.getAnnotation(Intercept.class);
                if (intercept != null && intercept.value().length != 0) {
                    for (Class<? extends Interceptor> aClass : intercept.value()) {
                        interceptFactory.addMethodInterceptor(method, getInterceptor(interceptFactory, aClass));
                    }
                }
            }
        });

        return true;
    }

    @Override
    public boolean destroy(SilentGo me) throws AppReleaseException {
        return false;
    }

    public Interceptor getInterceptor(InterceptFactory interceptFactory, Class<? extends Interceptor> interceptor) {
        try {
            interceptFactory.addAllInterceltor(interceptor, interceptor.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }

        return interceptFactory.getAllInterceptors().get(interceptor);
    }

    private boolean addIntercept(List<Interceptor> interceptors, Class<? extends Interceptor> clz) {
        if (interceptors.stream().filter(interceptor -> interceptor.getClass().equals(clz)).count() > 0)
            return false;
        try {
            return interceptors.add(clz.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
            return false;
        }
    }
}
