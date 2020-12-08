package com.wxx.service;

import java.util.List;
import java.util.Map;

/**
 * Date: 2020/11/23 17:49
 * Content:
 */
public interface IGoodsService {

    // 解析数据放入到 ES 中
    Boolean parseContent(String keyword);

    // 实现高亮搜索功能
    List<Map<String, Object>> searchPageHighlighterBuilder(String keyword, int pageNo, int pageSize);
}
