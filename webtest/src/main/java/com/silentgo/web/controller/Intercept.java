package com.silentgo.web.controller;

import com.silentgo.core.action.ActionParam;
import com.silentgo.core.aop.AOPPoint;
import com.silentgo.core.aop.annotation.Around;
import com.silentgo.core.aop.annotation.Aspect;
import com.silentgo.core.aop.aspect.support.AspectChain;
import com.silentgo.kit.logger.Logger;
import com.silentgo.kit.logger.LoggerFactory;

/**
 * Project : silentgo
 * com.silentgo.web.controller
 *
 * @author <a href="mailto:teddyzhu15@gmail.com" target="_blank">teddyzhu</a>
 *         <p>
 *         Created by teddyzhu on 16/8/22.
 */
@Aspect
public class Intercept {

    public static final Logger LOGGER = LoggerFactory.getLog(Intercept.class);


    @Around("com.silentgo.web.controller.index.testRegex")
    public void intercept(AspectChain aspectChain, AOPPoint point, boolean[] isHandled) throws Throwable {
        LOGGER.debug("testRegex all start");
        aspectChain.invoke();
        LOGGER.debug("testRegex all end");
    }

    @Around(value = "com.silentgo.web.controller.index.*", regex = true)
    public void intercept2(AspectChain aspectChain, AOPPoint point, boolean[] isHandled) throws Throwable {
        LOGGER.debug("testRegex regex start");
        aspectChain.invoke();
        LOGGER.debug("testRegex regex end");
    }
}
