package com.ljs.springbootsearchnotes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ljs.springbootsearchnotes.entity.Article;
import com.ljs.springbootsearchnotes.util.BeanUtils;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by lijunsai on 2021/05/28
 */
//@RestController
public class TestController {
    @Autowired
    private RestHighLevelClient client;
    @Autowired
    ObjectMapper objectMapper;

    // 1.创建索引
    @GetMapping("test1")
    public void testCreateIndex() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest("test_index");
//        request.mapping()
//        client.indices().get()
        CreateIndexResponse response = client.indices().create(request, RequestOptions.DEFAULT);

    }

    // 2.获取索引信息
    @GetMapping("test2")
    public void testGetIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest("test_index");
        boolean b = client.indices().exists(request, RequestOptions.DEFAULT);

        System.out.println(b);
    }

    // 3. 删除索引
    @GetMapping("test3")
    public void testDeleteIndex() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest("test_index");
        AcknowledgedResponse delete = client.indices().delete(request, RequestOptions.DEFAULT);

        System.out.println(delete.isAcknowledged());
    }

    // 4.添加文档
    @GetMapping("test4")
    void testAddDoc() throws IOException {
//        User user = new User("古力娜扎", 26);
//        IndexRequest request = new IndexRequest("test_index");
//        // 规则 put /test_index/_doc/1
//        request.id("1");
//        request.timeout(TimeValue.timeValueSeconds(1));
////            request.timeout("1s");
//
//        request.source(objectMapper.writeValueAsString(user), XContentType.JSON);
////
//        // 发送
//        IndexResponse response = client.index(request, RequestOptions.DEFAULT);
//        System.out.println(response.toString());
//        System.out.println(response.status());
    }

    // 5.获取文档
    void testGetDoc() throws IOException {
        GetRequest request = new GetRequest("test_index", "1");
        request.fetchSourceContext(new FetchSourceContext(false));
        request.storedFields("_none_");
        // 是否存在？？？
        boolean b = client.exists(request, RequestOptions.DEFAULT);
        System.out.println(b);

        // 获取
        GetResponse response = client.get(request, RequestOptions.DEFAULT);
        System.out.println(response.getSourceAsString());
        System.out.println(response);
    }

    // 6. 更新文档
    void testUpdateDoc() throws IOException {
//        UpdateRequest updateRequest = new UpdateRequest("test_index", "1");
//        updateRequest.timeout("1s");
//        User user = new User("迪丽热巴", 28);
//        updateRequest.doc(objectMapper.writeValueAsString(user), XContentType.JSON);
//        UpdateResponse response = client.update(updateRequest, RequestOptions.DEFAULT);
//        System.out.println(response);
//        System.out.println(response.status());
    }

    // 7.删除文档
    void testDeleteDoc() throws IOException {
        DeleteRequest request = new DeleteRequest("test_index", "1");
        request.timeout("1s");
        DeleteResponse response = client.delete(request, RequestOptions.DEFAULT);
        System.out.println(response);
        System.out.println(response.status());
    }

    // 8.批量插入数据
    @GetMapping("test12")
    void testBulkRequest() throws IOException {
        BulkRequest bulkRequest = new BulkRequest();
//        bulkRequest.timeout("15s");
        String[] content = new String[]{"广州此次疫情传播有多快？吃顿饭就可以非直接接触传播","浙江、云南两位市委书记晋升副省长有","湖南衡阳特大涉黑系列案一审宣判 57人获刑其中4人死刑","湖南"};
        ArrayList<Article> userList = new ArrayList<>();
        Article article = new Article("123", "123", "123");
        String s = objectMapper.writeValueAsString(article);
        System.out.println(s);
        for (int i = 0; i < content.length; i++) {
            bulkRequest.add(BeanUtils.getBean(Article.class).setMd5("测试" + i).setDir("测试" + i).setContext(content[i]).esIndexRequest());
        }
        // 批量插入
        BulkResponse response = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(response.toString());
        System.out.println(response.status());

    }


    // 9.条件搜索
    @GetMapping("test13")
    void testSearch() throws IOException, IllegalAccessException {
        SearchResponse response = BeanUtils.getBean(Article.class).setDir("2").setContext("有").search();
//        MultiSearchResponse search = BeanUtils.getBean(Article.class).setContext("湖南").setDir("测试3").search();
//        System.out.println(search);
        SearchHits hits = response.getHits();  //SearchHits提供有关所有匹配的全局信息，例如总命中数或最高分数：
        SearchHit[] searchHits = hits.getHits();
//        for (SearchHit hit : searchHits) {
////            log.info("search -> {}",hit.getSourceAsString());
//        }
        System.out.println(Arrays.toString(searchHits));
    }
}
