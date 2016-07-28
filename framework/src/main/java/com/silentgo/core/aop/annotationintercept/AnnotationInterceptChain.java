package com.silentgo.core.aop.annotationintercept;

import com.silentgo.core.aop.AOPPoint;
import com.silentgo.core.aop.Interceptor;

import java.lang.annotation.Annotation;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Project : silentgo
 * com.silentgo.core.aop.annotationintercept
 *
 * @author <a href="mailto:teddyzhu15@gmail.com" target="_blank">teddyzhu</a>
 *         <p>
 *         Created by teddyzhu on 16/7/28.
 */
public class AnnotationInterceptChain {
    private int index;

    private AOPPoint point;

    private boolean[] isResolved;

    private Map<Annotation, IAnnotation> interceptors;

    private Annotation[] annotations;

    private int size = 0;

    public <T extends Annotation> AnnotationInterceptChain(AOPPoint point, boolean[] isResolved, Map<Annotation, IAnnotation> interceptors) {
        this.point = point;
        this.isResolved = isResolved;
        this.interceptors = interceptors;
        this.size = interceptors.keySet().size();
        this.index = 0;
        this.annotations = interceptors.keySet().toArray(new Annotation[size]);
    }

    public Object excute() throws Throwable {
        Object returnValue = null;
        if (index < size) {
            Annotation annotation = annotations[index++];
            IAnnotation interceptor = interceptors.get(annotation);

            //noinspection unchecked
            returnValue = interceptor.intercept(point, isResolved, annotation);

            if (isResolved[0]) {
                return returnValue;
            }
        } else if (index++ == size) {
            return point.resolve();
        }

        return returnValue;

    }

}