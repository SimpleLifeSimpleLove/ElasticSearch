package com.wxx.controller;

import com.wxx.service.IGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * Date: 2020/11/23 17:50
 * Content: 请求编写
 */
@RestController
public class GoodsController {

    @Autowired
    private IGoodsService iGoodsService ;

    // http://localhost:9090/parse/java 获取关于 java 的数据
    // http://localhost:9090/parse/linux 获取关于 linux 的数据
    @GetMapping("/parse/{keyword}")
    public Boolean parse(@PathVariable("keyword") String keyword) {
        return iGoodsService.parseContent(keyword);
    }

    @GetMapping("/search/{keyword}/{pageNo}/{pageSize}")
    public List<Map<String, Object>> search(@PathVariable("keyword") String keyword,
                                            @PathVariable("pageNo") int pageNo,
                                            @PathVariable("pageSize") int pageSize) {
        return iGoodsService.searchPageHighlighterBuilder(keyword, pageNo, pageSize);
    }

}
