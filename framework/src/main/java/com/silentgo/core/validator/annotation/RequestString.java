package com.silentgo.core.validator.annotation;

import com.silentgo.config.Const;

import java.lang.annotation.*;

/**
 * Project : silentgo
 * com.silentgo.core.validator.annotation
 *
 * @author <a href="mailto:teddyzhu15@gmail.com" target="_blank">teddyzhu</a>
 *         <p>
 *         Created by  on 16/7/18.
 */
@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface RequestString {
    boolean required() default false;

    String defaultValue() default Const.DEFAULT_NONE;

    int maxLength() default Integer.MAX_VALUE;

    int minLength() default -1;

}
