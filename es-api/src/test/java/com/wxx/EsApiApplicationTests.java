package com.wxx;

import com.wxx.utils.ESConst;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.CreateIndexResponse;
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

	// 测试索引的创建
	@Test
	public void testCreatIndex() throws IOException {
		// 1.创建索引请求
		CreateIndexRequest request = new CreateIndexRequest(ESConst.ES_INDEX);
		// 2.客户端执行请求 IndicesClient，请求后获得相应
		CreateIndexResponse createIndexResponse = client.indices().create(request, RequestOptions.DEFAULT);

		System.out.println(createIndexResponse);
	}


}
