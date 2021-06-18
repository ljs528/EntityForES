package com.ljs.springbootsearchnotes.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ljs.springbootsearchnotes.annotion.EnableTransformIndex;
import com.ljs.springbootsearchnotes.annotion.EsJsonIgnore;
import com.ljs.springbootsearchnotes.annotion.EsMapping;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.concurrent.TimeUnit;

/**
 * Created by lijunsai on 2021/05/28
 */

public abstract class AbstractESUtil {

    @EsJsonIgnore
    @Autowired
    protected IJsonUtil jsonUtil;
    @EsJsonIgnore
    @Autowired
    protected RestHighLevelClient client;

    /**
     * 获取当前子类对应的索引库
     * @return 返回EnableTransformIndex的值或类名小写
     */
    public String esIndex(){
        EnableTransformIndex annotation = this.getClass().getAnnotation(EnableTransformIndex.class);
        if (annotation != null) {
            String value = annotation.value();
            if (StringUtils.isEmpty(value)) {
                return this.getClass().getSimpleName().toLowerCase();
            }
            return value;
        }
        return null;
    }

    public IndexRequest esIndexRequest() throws JsonProcessingException {
        return new IndexRequest(this.esIndex()).source(jsonUtil.toJson(this),  XContentType.JSON);
    }

    public SearchResponse search() throws IOException, IllegalAccessException {
        BoolQueryBuilder should = QueryBuilders.boolQuery();
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        Class<? extends AbstractESUtil> aClass = this.getClass();
        Field[] declaredFields = aClass.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            declaredField.setAccessible(true);
            EsJsonIgnore annotation = declaredField.getAnnotation(EsJsonIgnore.class);
            EsMapping mapping = declaredField.getAnnotation(EsMapping.class);
            if (mapping.index() && annotation == null && declaredField.get(this) != null) {
                should.should(QueryBuilders.matchQuery(declaredField.getName(), declaredField.get(this)).analyzer(mapping.analyzer()).operator(Operator.OR));
//                sourceBuilder.query(QueryBuilders.termQuery(declaredField.getName(), declaredField.get(this)));
            }
        }
        sourceBuilder.from(0); //设置确定结果要从哪个索引开始搜索的from选项，默认为0
        sourceBuilder.size(100); //设置确定搜素命中返回数的size选项，默认为10
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS)); //设置一个可选的超时，控制允许搜索的时间。
        sourceBuilder.query(should);
        SearchRequest searchRequest = new SearchRequest(this.esIndex()); //索引

        searchRequest.source(sourceBuilder);
        return  client.search(searchRequest, RequestOptions.DEFAULT);
    }
}
