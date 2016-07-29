package com.silentgo.core.aop.validator.support;

import com.silentgo.core.aop.validator.IValidator;
import com.silentgo.core.aop.validator.annotation.RequestString;
import com.silentgo.kit.ClassKit;
import com.silentgo.kit.CollectionKit;
import com.silentgo.kit.logger.Logger;
import com.silentgo.kit.logger.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.*;

/**
 * Project : silentgo
 * com.silentgo.core.aop.validator
 *
 * @author <a href="mailto:teddyzhu15@gmail.com" target="_blank">teddyzhu</a>
 *         <p>
 *         Created by  on 16/7/18.
 */
public class ValidatorFactory {

    private static Logger LOGGER = LoggerFactory.getLog(ValidatorFactory.class);
    /**
     * Key : Annotation Class Name  Value : Sorted ValidatorInterceptor
     */
    private final static Map<Class<? extends Annotation>, IValidator> validatorHashMap = new HashMap<>();

    static {
        validatorHashMap.put(RequestString.class, new StringValidator());

    }

    @SuppressWarnings("unchecked")
    public static void addValidator(Class<? extends IValidator> clz) {
        Class<? extends Annotation> an = (Class<? extends Annotation>) ClassKit.getGenericClass(clz, 0);

        if (an != null) {
            try {
                addValidator(an, clz.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                LOGGER.error("Add Validator Error,Class : [{}]", clz.getName());
                e.printStackTrace();
            }
        }
    }

    public static boolean addValidator(Class<? extends Annotation> clz, IValidator validator) {
        return CollectionKit.MapAdd(validatorHashMap, clz, validator);
    }

    public static IValidator getValidator(Class<? extends Annotation> an) {
        return validatorHashMap.get(an);
    }
}
