package com.huaxin.conversion;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * json 处理对象属性字符串
 */
public class CustomerObjectMapper extends ObjectMapper {

    public CustomerObjectMapper() {
        super();
        this.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true); // 允许单引号
        // 空值处理为空串
        this.getSerializerProvider().setNullValueSerializer(new JsonSerializer<Object>() {
            @Override
            public void serialize(Object value, JsonGenerator jgen,
                                  SerializerProvider provider) throws IOException,
                    JsonProcessingException {
                if (jgen != null) {
                    jgen.writeString("");
                }
            }
        });

//        this.getSerializerProvider().setDefaultKeySerializer(new JsonSerializer<Object>() {
//            @Override
//            public void serialize(Object value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
//                if(value != null){
//                    System.out.println(value.getClass());
//                }else{
//                    System.out.println("value is null");
//                }
//            }
//        });

    }
}
