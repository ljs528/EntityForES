package com.ljs.springbootsearchnotes.util;

import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * Created by lijunsai on 2021/06/15
 */
public interface IJsonUtil {
    String toJson(Object obj) throws JsonProcessingException;
}
