package com.summer.serialize.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.summer.serialize.Serializer;

import java.io.IOException;

/**
 * 对象序列化
 */
public class JacksonSerializer implements Serializer {

    private final ObjectMapper objectMapper = new ObjectMapper();
    @Override
    public byte[] serialize(Object object) {
        byte[] bytes = new byte[0];
        try{
            bytes = objectMapper.writeValueAsBytes(object);
        } catch (JsonProcessingException e){
            e.printStackTrace();
        }
        return bytes;
    }

    @Override
    public <T> T deserialize(byte[] bytes, Class<T> clazz) {
        T object = null;
        try {
            object = objectMapper.readValue(bytes, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return object;
    }
}
