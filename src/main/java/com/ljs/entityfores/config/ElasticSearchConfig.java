package com.ljs.springbootsearchnotes.config;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ljs.springbootsearchnotes.annotion.EnableTransformIndex;
import com.ljs.springbootsearchnotes.annotion.EsMapping;
import com.ljs.springbootsearchnotes.util.scan.ScanExecutor;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by lijunsai on 2021/05/28
 */
@Configuration
public class ElasticSearchConfig {
    @Autowired
    MyElasticsearchProperties myElasticsearchProperties;

    @Bean
    public RestHighLevelClient restHighLevelClient(ObjectMapper mapper) throws IOException {
        String clusterNodes = myElasticsearchProperties.getClusterNodes();
        String[] nodes = clusterNodes.split(",");
        HttpHost[] hosts = new HttpHost[nodes.length];
        for (int i = 0; i < nodes.length; i++) {
            String[] node = nodes[i].split(":");
            hosts[i] = new HttpHost(node[0], Integer.parseInt(node[1]), "http");
        }
        RestClientBuilder http = RestClient.builder(hosts);
        if (myElasticsearchProperties.getNeedCheck()) {
            final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(myElasticsearchProperties.getEsName(), myElasticsearchProperties.getEsPassword()));  //es?????????????????????????????????elastic???
            http.setHttpClientConfigCallback((x) -> {
                x.disableAuthCaching();
                return x.setDefaultCredentialsProvider(credentialsProvider);
            });
        }
        RestHighLevelClient client = new RestHighLevelClient(http);

        if (myElasticsearchProperties.getEntityTransform()) {
            String entityPath = myElasticsearchProperties.getEntityPath();
            Set<Class<?>> search = ScanExecutor.getInstance().search(entityPath);
            for (Class<?> aClass : search) {
                EnableTransformIndex annotation = aClass.getAnnotation(EnableTransformIndex.class);
                if (annotation != null) {
                    String index = annotation.value();
                    if (StringUtils.isEmpty(index)) {
                        // ??????????????????????????????????????????????????????
                        index = aClass.getSimpleName().toLowerCase();
                    }
                    GetIndexRequest get = new GetIndexRequest(index);
                    if (!client.indices().exists(get, RequestOptions.DEFAULT)) {
                        Field[] fields = aClass.getDeclaredFields();
                        Map<String, Map<String, Object>> mappings = new HashMap<>();
                        for (Field field : fields) {
                            EsMapping esMapping = field.getAnnotation(EsMapping.class);
                            if (esMapping != null) {
                                Map<String, Object> value = new HashMap<>();
                                value.put("type", esMapping.type());
                                value.put("store", esMapping.store());
                                value.put("index", esMapping.index());
                                if (esMapping.index()) {
                                    value.put("analyzer", esMapping.analyzer());
                                }
                                mappings.put(field.getName(), value);
                            }
                        }
                        CreateIndexRequest create = new CreateIndexRequest(index);
                        if (mappings.size() > 0) {
                            create.mapping("{\n" +
                                            "  \"properties\":" +
                                            mapper.writeValueAsString(mappings) +
                                            "}",
                                    XContentType.JSON);
                        }
                        client.indices().create(create, RequestOptions.DEFAULT);
                    }
                }
            }
        }
//        RestHighLevelClient	client = new RestHighLevelClient(
//                RestClient.builder(
//                        new HttpHost("192.168.43.45",9200,"http")));
        return client;
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        //?????????????????????????????????????????????
        objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);
        //?????????????????????????????????????????????,???????????????
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        // ????????????
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        // ??????JSON ????????????("")?????????null?????????:
        objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
        //???????????????????????????,????????????
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        objectMapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        objectMapper.setDateFormat(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"));
        return objectMapper;
    }
}
