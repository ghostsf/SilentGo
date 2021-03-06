package com.silentgo.core.typeconvert.support;


import com.silentgo.core.typeconvert.ITypeConvertor;
import com.silentgo.core.typeconvert.annotation.Convertor;
import com.silentgo.utils.logger.Logger;
import com.silentgo.utils.logger.LoggerFactory;

/**
 * Project : silentgo
 * com.silentgo.kit.typeconvert.support
 *
 * @author <a href="mailto:teddyzhu15@gmail.com" target="_blank">teddyzhu</a>
 *         <p>
 *         Created by teddyzhu on 16/8/20.
 */
@Convertor
public class StringToLongConvertor implements ITypeConvertor<String, Long> {

    public static final Logger LOGGER = LoggerFactory.getLog(StringToLongConvertor.class);

    @Override
    public Long convert(String source) {
        Long result = null;
        try {
            result = Long.parseLong(source);
        } catch (NumberFormatException e) {
            LOGGER.error(e.getMessage());
        }
        return result;
    }
}
