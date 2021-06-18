package com.ljs.springbootsearchnotes.entity;

import com.ljs.springbootsearchnotes.annotion.EnableTransformIndex;
import com.ljs.springbootsearchnotes.annotion.EsMapping;
import com.ljs.springbootsearchnotes.util.AbstractESUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * Created by lijunsai on 2021/05/28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@Accessors(chain = true)
@EnableTransformIndex("test_article")
@Component
@Scope("prototype")
public class Article extends AbstractESUtil {
    @EsMapping(type = "text", index = false)
    private String md5;
    @EsMapping(type = "text", analyzer = "ik_max_word")
    private String dir;
    @EsMapping(type = "text", analyzer = "ik_max_word")
    private String context;
}
