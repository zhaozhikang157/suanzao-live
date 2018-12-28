package com.huaxin.conversion;

import com.huaxin.util.Utility;
import org.springframework.core.convert.converter.Converter;

/**
 * Created by syl on 2016/5/4.
 */
public class CustomerDoubleConversion implements Converter<String , Double> {
    @Override
    public Double convert(String source) {
        Double doubleObj = 0d;
        if(Utility.isNumber(source)){
            source = source.replace("," ,"");
            doubleObj = Double.parseDouble(source);
        }
        return doubleObj;
    }
}
