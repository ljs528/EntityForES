package com.ljs.springbootsearchnotes.util.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ljs.springbootsearchnotes.util.IJsonUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by lijunsai on 2021/06/15
 */
@Service
public class JsonUtilImpl implements IJsonUtil {
    @Autowired
    private ObjectMapper objectMapper;
    @Override
    public String toJson(Object obj) throws JsonProcessingException {
        return objectMapper.writeValueAsString(obj);
    }
}
