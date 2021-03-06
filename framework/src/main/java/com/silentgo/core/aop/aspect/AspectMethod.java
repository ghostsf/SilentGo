package com.silentgo.core.aop.aspect;

import net.sf.cglib.reflect.FastMethod;

import java.lang.reflect.InvocationTargetException;

/**
 * Project : silentgo
 * com.silentgo.core.aop
 *
 * @author <a href="mailto:teddyzhu15@gmail.com" target="_blank">teddyzhu</a>
 *         <p>
 *         Created by teddyzhu on 16/7/29.
 */
public class AspectMethod {
    private String rule;

    private boolean regex;

    private FastMethod method;

    private Object targetAspect;

    public Object invoke(Object... args) throws InvocationTargetException {
        return method.invoke(targetAspect, args);
    }

    public AspectMethod(String rule, boolean regex, FastMethod method, Object targetAspect) {
        this.rule = rule;
        this.regex = regex;
        this.method = method;
        this.targetAspect = targetAspect;
    }

    public String getRule() {
        return rule;
    }

    public boolean isRegex() {
        return regex;
    }

    public FastMethod getMethod() {
        return method;
    }

    public Object getTargetAspect() {
        return targetAspect;
    }
}
