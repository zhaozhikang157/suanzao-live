package com.huaxin.conversion;

import com.huaxin.util.Utility;
import org.springframework.core.convert.converter.Converter;

import java.math.BigDecimal;

/**
 * Created by syl on 2016/5/4.
 */
public class CustomerBigDecimalConversion implements Converter<String , BigDecimal> {
    @Override
    public BigDecimal convert(String source) {
        BigDecimal bigDecimal = new BigDecimal(0);
        if(Utility.isNumber(source)){
            source = source.replace("," ,"");
            bigDecimal = new BigDecimal(source);
        }
        return bigDecimal;
    }

}
