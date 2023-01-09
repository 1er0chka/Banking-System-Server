package com.app.mapper;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.simple.JSONArray;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

public class JSON<T> {
    public T fromJson(String data, Class<T> valueType) throws JsonProcessingException {
        return (new ObjectMapper()).readValue(data, valueType);
    }

    public String toJson(T value) throws IOException {
        StringWriter writer = new StringWriter();
        (new ObjectMapper()).writeValue(writer, value);
        return writer.toString();
    }

    public String toJson(List<T> values) throws IOException {
        StringWriter writer = new StringWriter();
        JSONArray jsonArray = new JSONArray();
        jsonArray.addAll(values);
        (new ObjectMapper()).writeValue(writer, jsonArray);
        return writer.toString();
    }
}
