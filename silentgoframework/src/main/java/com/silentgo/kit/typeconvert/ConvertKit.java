package com.silentgo.kit.typeconvert;

import com.silentgo.kit.ClassKit;
import com.silentgo.kit.typeconvert.support.CommonConvertor;
import com.silentgo.kit.typeconvert.support.TypeConvert;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Project : silentgo
 * com.silentgo.kit.typeconvert
 *
 * @author <a href="mailto:teddyzhu15@gmail.com" target="_blank">teddyzhu</a>
 *         <p>
 *         Created by teddyzhu on 16/7/26.
 */
public class ConvertKit {


    static Map<Class<?>, Map<Class<?>, ITypeConvertor>> convertMap = new HashMap<>();

    public ConvertKit() {
    }

    public ITypeConvertor getTypeConvert(Class<?> source, Class<?> target) {
        return getTypeConvert(source, target, new CommonConvertor());
    }

    public ITypeConvertor getTypeConvert(Class<?> source, Class<?> target, ITypeConvertor defaultVal) {
        if (target.isPrimitive()) {
            target = TypeConvert.getConvertType(target);
        }

        Map<Class<?>, ITypeConvertor> convertorMap = convertMap.get(source);
        return convertorMap == null ? defaultVal : convertorMap.getOrDefault(target, defaultVal);
    }

    public boolean addConvert(ITypeConvertor convert) {
        Class<?> convertClass = convert.getClass();
        Type[] types = ClassKit.getGenericClass(convertClass);

        Class<?> source = (Class<?>) types[0];

        Class<?> target = (Class<?>) types[1];

        Map converts = convertMap.get(source);
        if (converts == null) {
            converts = new HashMap<Class<?>, ITypeConvertor>() {{
                put(target, convert);
            }};
            convertMap.put(source, converts);
        } else {
            if (!converts.containsKey(target))
                converts.put(target, convert);
        }
        return true;
    }

    public boolean addConvert(Class<?> convert) {
        try {
            return addConvert((ITypeConvertor) convert.newInstance());
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }
}
