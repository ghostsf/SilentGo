package com.silentgo.core.route.support;

import com.silentgo.core.build.SilentGoBuilder;
import com.silentgo.core.build.annotation.Builder;
import com.silentgo.core.config.Const;
import com.silentgo.core.config.Regex;
import com.silentgo.core.SilentGo;
import com.silentgo.core.aop.MethodAdviser;
import com.silentgo.core.aop.support.MethodAOPFactory;
import com.silentgo.core.ioc.bean.BeanFactory;
import com.silentgo.core.ioc.bean.BeanWrapper;
import com.silentgo.core.route.BasicRoute;
import com.silentgo.core.route.ParameterDispatcher;
import com.silentgo.core.route.RegexRoute;
import com.silentgo.core.route.annotation.Controller;
import com.silentgo.core.route.annotation.ParamDispatcher;
import com.silentgo.core.route.annotation.Route;
import com.silentgo.core.route.support.paramdispatcher.ParamDispatchFactory;
import com.silentgo.kit.StringKit;
import com.silentgo.kit.logger.Logger;
import com.silentgo.kit.logger.LoggerFactory;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Project : silentgo
 * com.silentgo.core.action.support
 *
 * @author <a href="mailto:teddyzhu15@gmail.com" target="_blank">teddyzhu</a>
 *         <p>
 *         Created by teddyzhu on 16/7/25.
 */
@Builder
public class RouteBuilder extends SilentGoBuilder {


    @Override
    public Integer priority() {
        return 30;
    }

    private static final Logger LOGGER = LoggerFactory.getLog(RouteBuilder.class);

    private void buildClass(Class<?> aClass, SilentGo me, RouteFactory routeFactory) {
        Controller controller = aClass.getAnnotation(Controller.class);
        Route route = aClass.getAnnotation(Route.class);
        boolean parentRegex = route != null && route.regex();
        String path = filterPath((route != null && !Const.DEFAULT_NONE.equals(route.value())) ? route.value() : aClass.getSimpleName(), true);
        BeanFactory beanFactory = me.getFactory(BeanFactory.class);
        MethodAOPFactory methodAOPFactory = me.getFactory(MethodAOPFactory.class);
        BeanWrapper bean = beanFactory.getBean(aClass.getName());

        Pattern routePattern = Pattern.compile(Regex.RoutePath);

        for (Method method : bean.getBeanClass().getJavaClass().getDeclaredMethods()) {
            MethodAdviser adviser = methodAOPFactory.getMethodAdviser(method);
            Route an = adviser.getAnnotation(Route.class);
            if (an == null) continue;

            String fullPath = mergePath(path, filterPath(Const.DEFAULT_NONE.equals(an.value()) ?
                    method.getName() : an.value(), true));
            Matcher matcher = routePattern.matcher(fullPath);

            if (an.regex() || parentRegex || matcher.find()) {
                routeFactory.addRoute(buildRegexRoute(fullPath, matcher, adviser));
            } else {
                routeFactory.addRoute(buildBasicRoute(fullPath, adviser));
            }
        }


    }

    private BasicRoute buildBasicRoute(String path, MethodAdviser adviser) {
        BasicRoute basicRoute = new BasicRoute();

        basicRoute.setAdviser(adviser);
        basicRoute.setPath(path);
        return basicRoute;
    }

    private RegexRoute buildRegexRoute(String path, Matcher matcher, MethodAdviser adviser) {
        BasicRoute basicRoute = buildBasicRoute(path, adviser);
        RegexRoute route = new RegexRoute(basicRoute);
        String resolvedMatch = path;
        resolvedMatch = resolveRoute(matcher, route, resolvedMatch, path);
        while (matcher.find()) {
            resolvedMatch = resolveRoute(matcher, route, resolvedMatch, path);
        }
        route.setPattern(Pattern.compile(resolvedMatch));
        return route;
    }

    private String resolveRoute(Matcher matcher, RegexRoute route, String resolvedMatch, String path) {
        String rule = matcher.group();
        String ruleSolved = rule.substring(1, rule.length() - 1);
        if (ruleSolved.contains(Regex.RouteSplit)) {
            if (ruleSolved.length() > Regex.RouteSplit.length()) {
                String name = StringKit.getLeft(ruleSolved, Regex.RouteSplit).trim();
                String regex = StringKit.getRight(ruleSolved, Regex.RouteSplit).trim();
                regex = StringKit.isNullOrEmpty(regex) ? Regex.RegexAll : regex;
                boolean needName = !StringKit.isNullOrEmpty(name);
                String replacement = needName ? Regex.RoutePathNameRegexMatch : Regex.RoutePathCustomMatch;
                if (needName) {
                    route.addName(name);
                    replacement = String.format(replacement, name, regex);
                    resolvedMatch = replaceRegex(resolvedMatch, rule, replacement);
                } else {
                    replacement = String.format(replacement, regex);
                    resolvedMatch = replaceRegex(resolvedMatch, rule, replacement);
                }


            } else {
                LOGGER.warn("can not match rule {} in path {}, ignored !", rule, path);
            }
        } else {
            route.addName(ruleSolved);
            resolvedMatch = replaceRegex(resolvedMatch, rule, String.format(Regex.RoutePathNameRegexMatch, ruleSolved, Regex.RegexAll));

        }
        return resolvedMatch;
    }

    private String replaceRegex(String source, String target, String replacement) {
        return source.replace(target, replacement);
    }

    private String mergePath(String suffix, String prefix) {
        suffix = filterPath(suffix, true);
        suffix = suffix.equals(Const.Slash) ? suffix : (suffix + Const.Slash);
        return suffix + (prefix.startsWith(Const.Slash) ? prefix.substring(1) : prefix);
    }

    private String filterPath(String path, boolean end) {
        if (path.equals(Const.Slash)) return path;
        if (path.endsWith(Const.Slash) && end) path = path.substring(0, path.length() - 1);
        if (path.startsWith(Const.Slash)) return path;
        return Const.Slash + path;
    }

    @Override
    public boolean build(SilentGo me) {


        //build route
        RouteFactory routeFactory = new RouteFactory();
        me.getConfig().addFactory(routeFactory);
        me.getAnnotationManager().getClasses(Controller.class).forEach(aClass -> buildClass(aClass, me, routeFactory));


        //build parameter dispatcher
        ParamDispatchFactory dispatchFactory = new ParamDispatchFactory();
        me.getConfig().addFactory(dispatchFactory);
        me.getAnnotationManager().getClasses(ParamDispatcher.class).forEach(aClass -> {
            if (!ParameterDispatcher.class.isAssignableFrom(aClass)) return;
            try {
                dispatchFactory.addDispatcher((ParameterDispatcher) aClass.newInstance());
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        });
        dispatchFactory.resort();
        return true;
    }
}
