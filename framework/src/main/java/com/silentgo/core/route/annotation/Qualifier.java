package com.silentgo.core.route.annotation;

import java.lang.annotation.*;

/**
 * Project : silentgo
 * com.silentgo.core.route.annotation
 *
 * @author <a href="mailto:teddyzhu15@gmail.com" target="_blank">teddyzhu</a>
 *         <p>
 *         Created by teddyzhu on 16/7/29.
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface Qualifier {
    String value();
}
