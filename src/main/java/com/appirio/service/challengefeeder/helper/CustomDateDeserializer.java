/*
 * Copyright (C) 2017 TopCoder Inc., All Rights Reserved.
 */
package com.appirio.service.challengefeeder.helper;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * CustomDateDeserializer is used to deserialize the date format
 * 
 * @author TCCoder
 * @version 1.0
 *
 */
public class CustomDateDeserializer extends JsonDeserializer<Date> {
    /**
     * Deserialize the date string
     *
     * @param jp the jp to use
     * @param ctxt the ctxt to use
     * @throws IOException if any error occurs
     * @throws JsonProcessingException if any error occurs
     * @return the Date result
     */
    @Override
    public Date deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        String value = jp.getValueAsString();
        if (value == null) {
            return null;
        }
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        try {
            return dateFormat.parse(value);
        } catch (ParseException e) {
            throw new IOException("Can not parse the value:" + value, e);
        }
    }
}