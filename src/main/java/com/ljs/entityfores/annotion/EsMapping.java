package com.ljs.springbootsearchnotes.annotion;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by lijunsai on 2021/05/28
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface EsMapping {
    String type();
    boolean store() default true;
    boolean index() default true;
    String analyzer() default "standard";
}
