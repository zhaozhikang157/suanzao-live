package com.longlian.type;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by liuhan on 2017-09-05.
 */
public class CustomJsonDateDeserializer extends JsonDeserializer<Date> {

    @Override
    public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        if (jp ==null) {
            return null;
        }
        String date = jp.getText();
        if (date == null || date.equals("") ) {
            return null;
        }
        if (date.matches("^[0-9]*[1-9][0-9]*$")){
            return new Date(Long.parseLong(date));
        }

        try {
            if (date != null && date.length() == 16) {
                date = ":00";
            }
            return format.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

}