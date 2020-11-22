package com.wxx;

import com.wxx.utils.ESConst;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.support.master.AcknowledgedResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

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



}
