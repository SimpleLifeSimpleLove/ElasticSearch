package com.wxx.service.impl;

import com.alibaba.fastjson.JSON;
import com.wxx.pojo.Goods;
import com.wxx.service.IGoodsService;
import com.wxx.utils.ESConst;
import com.wxx.utils.HtmlParseUtil;
import lombok.SneakyThrows;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Date: 2020/12/07 16:50
 * Content: 业务编写
 */
@Service
public class GoodsService implements IGoodsService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    @SneakyThrows
    @Override
    public Boolean parseContent(String keyword) {
        List<Goods> goods = HtmlParseUtil.parseGoods(keyword);
        // 把查询的结果放入到 ES 中
        BulkRequest bulkRequest = new BulkRequest();
        bulkRequest.timeout("2m");

        for (int i = 0; i < goods.size(); i++) {
            bulkRequest.add(
                    new IndexRequest(ESConst.ES_INDEX_GOODS).source(JSON.toJSONString(goods.get(i)), XContentType.JSON)
            );
        }
        BulkResponse bulk = restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);

        return !bulk.hasFailures();
    }

    // 实现高亮搜索功能
    @SneakyThrows
    @Override
    public List<Map<String, Object>> searchPageHighlighterBuilder(String keyword, int pageNo, int pageSize) {
        if (pageNo < 1) {
            pageNo = 1;
        }

        // 条件搜索
        SearchRequest searchRequest = new SearchRequest(ESConst.ES_INDEX_GOODS);

        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 分页
        sourceBuilder.from(pageNo);
        sourceBuilder.size(pageSize);
        // 精准匹配
//        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("title", keyword);
        // 可以匹配汉字，将 keyword 拆分成一个个字，这些字中有匹配的即可，比如 "洗衣液"，则衣服类产品也会被搜出来
//        MatchQueryBuilder matchQueryBuilder = QueryBuilders.matchQuery("title", keyword);
        // 可以匹配汉字，将 keyword 整体匹配，不拆
        MatchPhraseQueryBuilder queryBuilder = QueryBuilders.matchPhraseQuery("title", keyword);
        sourceBuilder.query(queryBuilder);
        sourceBuilder.timeout(new TimeValue(60, TimeUnit.SECONDS));

        // 高亮
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.requireFieldMatch(false);  // 只高亮第一个
        highlightBuilder.preTags("<span style='color:red'>");
        highlightBuilder.postTags("</span>");
        sourceBuilder.highlighter(highlightBuilder);


        // 执行搜索
        searchRequest.source(sourceBuilder);
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        // 解析结果
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        for (SearchHit hit : searchResponse.getHits().getHits()) {

            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField title = highlightFields.get("title");
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();  // 原来的结果
            // 解析高亮的字段，将原来的字段替换为我们高亮的字段即可
            if (title != null) {
                Text[] fragments = title.fragments();
                StringBuilder sb = new StringBuilder();
                for (Text text : fragments) {
                    sb.append(text);
                }
                sourceAsMap.put("title", sb.toString());
            }

            list.add(sourceAsMap);
        }

        return list;
    }
}
