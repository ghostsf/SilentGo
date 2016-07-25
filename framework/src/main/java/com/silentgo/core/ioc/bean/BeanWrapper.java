package com.silentgo.core.ioc.bean;

/**
 * Project : silentgo
 * com.silentgo.core.ioc.bean
 *
 * @author    <a href="mailto:teddyzhu15@gmail.com" target="_blank">teddyzhu</a>
 * <p>
 * Created by teddyzhu on 16/7/20.
 */
public abstract class BeanWrapper {

    public abstract Object getBean();
    public abstract Class<?> getBeanClass();
    public abstract String getBeanName();

}
