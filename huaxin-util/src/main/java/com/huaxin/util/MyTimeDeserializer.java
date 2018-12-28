package com.huaxin.util;

import java.lang.reflect.Type;
import java.sql.Time;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.parser.DefaultJSONParser;
import com.alibaba.fastjson.parser.JSONLexer;
import com.alibaba.fastjson.parser.JSONScanner;
import com.alibaba.fastjson.parser.JSONToken;
import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.parser.deserializer.TimeDeserializer;
@Component("myTimeDeserializer")
public class MyTimeDeserializer extends TimeDeserializer  implements InitializingBean {
    public final static MyTimeDeserializer instance = new MyTimeDeserializer();
    
    @SuppressWarnings("unchecked")
    public <T> T deserialze(DefaultJSONParser parser, Type clazz, Object fieldName) {
        JSONLexer lexer = parser.getLexer();
        
        if (lexer.token() == JSONToken.COMMA) {
            lexer.nextToken(JSONToken.LITERAL_STRING);
            
            if (lexer.token() != JSONToken.LITERAL_STRING) {
                throw new JSONException("syntax error");
            }
            
            lexer.nextTokenWithColon(JSONToken.LITERAL_INT);
            
            if (lexer.token() != JSONToken.LITERAL_INT) {
                throw new JSONException("syntax error");
            }
            
            long time = lexer.longValue();
            lexer.nextToken(JSONToken.RBRACE);
            if (lexer.token() != JSONToken.RBRACE) {
                throw new JSONException("syntax error");
            }
            lexer.nextToken(JSONToken.COMMA);
            
            return (T) new java.sql.Time(time);
        }
        
        Object val = parser.parse();

        if (val == null) {
            return null;
        }

        if (val instanceof java.sql.Time) {
            return (T) val;
        } else if (val instanceof Number) {
            return (T) new java.sql.Time(((Number) val).longValue());
        } else if (val instanceof String) {
            String strVal = (String) val;
            if (strVal.length() == 0) {
                return null;
            }
            
            long longVal;
            JSONScanner dateLexer = new JSONScanner(strVal);
            if (dateLexer.scanISO8601DateIfMatch(false)) {
                longVal = dateLexer.getCalendar().getTimeInMillis();
            } else {
                longVal = Long.parseLong(strVal);
            }
            dateLexer.close();
            return (T) new java.sql.Time(longVal);
        }
        
        throw new JSONException("parse error");
    }

    public int getFastMatchToken() {
        return JSONToken.LITERAL_INT;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        ParserConfig.getGlobalInstance().putDeserializer(Time.class, instance);
    }
}
