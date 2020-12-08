package com.wxx.utils;

import com.wxx.pojo.Goods;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Date: 2020/11/23 17:20
 * Content:
 */
@Component
public class HtmlParseUtil {

    public static List<Goods> parseGoods(String keyword) throws Exception {
        // 获取请求 https://search.jd.com/Search?keyword=java
        String url = "https://search.jd.com/Search?keyword=" + keyword;

        // 解析网页(Jsoup返回的Document就是浏览器中的Document对象，所有在js中的方法，这里都可以用)
        Document document = Jsoup.parse(new URL(url), 30000);

        Element element = document.getElementById("J_goodsList");
        // 获取所有的 li 标签
        Elements elements = element.getElementsByTag("li");

        // 使用 list 存放数据
        ArrayList<Goods> goodsList = new ArrayList<>();
        // 获取元素中的内容，这里的 el 就是每一个 li 标签了
        for (Element el : elements) {
            // 关于图片特别多的网站，所有的图片都是延迟加载的
            // data-lazy-img
            String title = el.getElementsByClass("p-name").eq(0).text();
            String price = el.getElementsByClass("p-price").eq(0).text();
            String img = el.getElementsByTag("img").eq(0).attr("data-lazy-img");
            // "//item.jd.com/12185501.html"，需要拼接
            String goodUrl = "https:" + el.getElementsByTag("a").eq(0).attr("href");

            goodsList.add(new Goods(title, price, img, goodUrl));
        }
        System.out.println(goodsList);

        return goodsList;
    }

}
