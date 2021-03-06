package com.silentgo.core.route.support.paramvalueresolve.support;


import com.silentgo.utils.CollectionKit;
import com.silentgo.utils.StringKit;

import java.util.HashMap;
import java.util.Map;

/**
 * Project : silentgo
 * com.silentgo.core.route.support.paramvalueresolve.support
 *
 * @author <a href="mailto:teddyzhu15@gmail.com" target="_blank">teddyzhu</a>
 *         <p>
 *         Created by teddyzhu on 16/8/30.
 */
public class ParameterResolveKit {


    public static Map<String, Object> getParamHash(Map<String, String[]> paramsMap) {

        Map<String, Object> parseParamMapTmp = new HashMap<>();

        paramsMap.forEach((k, v) -> {
            String[] ks = k.indexOf('.') > 0 ? k.split("\\.") : new String[]{k};
            if (ks.length == 0 || StringKit.isBlank(ks[0])) return;


            if (ks.length == 1) {
                if (v.length == 1) {
                    CollectionKit.MapAdd(parseParamMapTmp, ks[0], v[0]);
                } else {
                    CollectionKit.MapAdd(parseParamMapTmp, ks[0], v);
                }
            }
            if (ks.length > 1) {

                Map<String, Object> map = getMap(parseParamMapTmp, ks[0], new HashMap<>());

                for (int i = 1, len = ks.length - 1; i < len; i++) {
                    map = getMap(map, ks[i], new HashMap<>());
                }


                if (v.length == 1) {
                    CollectionKit.MapAdd(map, ks[ks.length - 1], v[0]);
                } else {
                    CollectionKit.MapAdd(map, ks[ks.length - 1], v);
                }
            }
        });
        return parseParamMapTmp;
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> getMap(Map<String, Object> source, String key, Object value) {
        Object obj = source.get(key);
        if (obj == null) obj = value;
        source.put(key, obj);
        return (Map<String, Object>) obj;
    }
}
