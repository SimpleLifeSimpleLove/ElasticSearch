package com.wxx;

import com.alibaba.fastjson.JSON;
import com.wxx.pojo.User;
import com.wxx.utils.ESConst;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.FetchSourceContext;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

/**
 * ES 7.6.1 高级客户端 API
 */

@SpringBootTest
class EsApiApplicationTests {

    @Autowired
    @Qualifier("restHighLevelClient")
    private RestHighLevelClient client;

    // =======================================索引===========================================
    // 测试索引的创建
    @Test
    public void testCreatIndex() throws IOException {
        // 1.创建索引请求
        CreateIndexRequest request = new CreateIndexRequest(ESConst.ES_INDEX);
        // 2.客户端执行请求 Indices，请求后获得相应
        CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);

        System.out.println(createIndexResponse);
    }

    // 测试获取索引，判断其是否存在
    @Test
    public void testExistIndex() throws IOException {
        GetIndexRequest request = new GetIndexRequest(ESConst.ES_INDEX);
        boolean exists = client.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    // 测试删除索引
    @Test
    public void testDeleteIndex() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest(ESConst.ES_INDEX);
        AcknowledgedResponse delete = client.indices().delete(request, RequestOptions.DEFAULT);
        System.out.println(delete.isAcknowledged());
    }

    // =======================================文档===========================================
    // 测试添加文档   PUT /wxx_index/_doc/1
    @Test
    void testAddDocument() throws IOException {
        // 创建请求
        IndexRequest indexRequest = new IndexRequest(ESConst.ES_INDEX);

        // 设置规则  PUT /wxx_index/_doc/1
        indexRequest.id("1");  // 设置文档id
        // indexRequest.timeout("1s");  // 设置超时时间
        indexRequest.timeout(TimeValue.timeValueSeconds(1));  // 设置超时时间

        // 创建对象，并将我们的对象以json形式放入请求中
        User user = new User("王肖肖", 3);
        indexRequest.source(JSON.toJSONString(user), XContentType.JSON);

        // 向客户端发送请求(得到响应)
        IndexResponse index = client.index(indexRequest, RequestOptions.DEFAULT);

        System.out.println(index.toString());
        System.out.println(index.status());  // 对应我们命令返回的状态
    }

    // 获取文档，判断是否存在  GET /wxx_index/_doc/1
    @Test
    void testExistDocument() throws IOException {
        // 创建请求
        GetRequest getRequest = new GetRequest(ESConst.ES_INDEX, "1");

        // 不需要返回 _score 的上下文了
        getRequest.fetchSourceContext(new FetchSourceContext(false));
        getRequest.storedFields("_none_");

        boolean exists = client.exists(getRequest, RequestOptions.DEFAULT);
        System.out.println(exists);
    }

    // 获取文档信息  GET /wxx_index/_doc/1
    @Test
    void testGetDocument() throws IOException {
        // 创建请求
        GetRequest getRequest = new GetRequest(ESConst.ES_INDEX, "1");

        // 获取文档信息(得到响应)
        GetResponse getResponse = client.get(getRequest, RequestOptions.DEFAULT);

        // 打印文档信内容
        System.out.println(getResponse.getSourceAsString());  // {"age":3,"name":"王肖肖"}
        System.out.println(getResponse);  // 返回的内容和命令行操作是一样的，如下：
        /*
        {
            "_index":"wxx_index",
            "_type":"_doc",
            "_id":"1",
            "_version":1,
            "_seq_no":0,
            "_primary_term":1,
            "found":true,
            "_source":
                {
                    "age":3,
                    "name":"王肖肖"
                }
          }
         */
    }

    // 更新文档信息  POST /wxx_index/_doc/1  ...
    @Test
    void testUpdateDocument() throws IOException {
        // 创建请求
        UpdateRequest updateRequest = new UpdateRequest(ESConst.ES_INDEX, "1");

        // 设置超时时间
        updateRequest.timeout("1s");

        // 创建需要更新的数据，并放入请求中
        User user = new User("王肖肖", 23);
        updateRequest.doc(JSON.toJSONString(user), XContentType.JSON);

        // 向客户端发送更新请求(得到响应)
        UpdateResponse updateResponse = client.update(updateRequest, RequestOptions.DEFAULT);

        System.out.println(updateResponse);
        System.out.println(updateResponse.status());
    }

    // 删除文档信息  DELETE /wxx_index/_doc/1
    @Test
    void testDeleteDocument() throws IOException {
        // 创建请求
        DeleteRequest deleteRequest = new DeleteRequest(ESConst.ES_INDEX, "1");

        // 设置超时时间
        deleteRequest.timeout("1s");

        // 向客户端发送更新请求(得到响应)
        DeleteResponse deleteResponse = client.delete(deleteRequest, RequestOptions.DEFAULT);

        System.out.println(deleteResponse.status());
    }

    // 批量插入文档信息  PUT /wxx_index/_doc/1
    @Test
    void testBulkDocument() throws IOException {
        // 创建请求
        BulkRequest bulkRequest = new BulkRequest();

        // 设置超时时间
        bulkRequest.timeout("10s");

        // 创建对象，并将我们的对象以json形式放入请求中
        ArrayList<User> userList = new ArrayList<>();
        userList.add(new User("wxx1", 1));
        userList.add(new User("wxx2", 10));
        userList.add(new User("wxx3", 15));
        userList.add(new User("wxx4", 23));
        for (int i = 0; i < userList.size(); i++) {
            // 批量更新和批量删除，就在这里修改对应的请求就可以了
            bulkRequest.add(
                    new IndexRequest(ESConst.ES_INDEX)
                            .id("" + (i + 1))  // 不用id，会生成随机id
                            .source(JSON.toJSONString(userList.get(i)), XContentType.JSON)
            );
        }

        // 向客户端发送请求(得到响应)
        BulkResponse bulkResponse = client.bulk(bulkRequest, RequestOptions.DEFAULT);
        System.out.println(!bulkResponse.hasFailures());  // 是否成功
    }

    // 查询
    /*
        SearchSourceBuilder : 条件构造
            HighlightBuilder : 构建高亮
            termQueryBuilder : 精确查询
            MatchAllQueryBuilder :
            xxxQueryBuilder :
     */
    @Test
    public void testSearch() throws IOException {
        // 创建请求
        SearchRequest searchRequest = new SearchRequest(ESConst.ES_INDEX);

        // 构建搜索条件
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 查询条件
        // QueryBuilders.termQuery 精确匹配
        // QueryBuilders.matchAllQuery() 匹配所有
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("name", "wxx1");
        sourceBuilder.query(termQueryBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        // 将搜索条件放入请求中
        searchRequest.source(sourceBuilder);

        // 向客户端发送请求(得到响应)
        SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);

        System.out.println(JSON.toJSONString(searchResponse.getHits()));

        for (SearchHit documentFields : searchResponse.getHits().getHits()) {
            System.out.println(documentFields.getSourceAsMap());
        }
    }

}
