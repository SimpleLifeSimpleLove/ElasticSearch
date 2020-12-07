package com.wxx.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Date: 2020/11/23 17:30
 * Content:
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Goods {

    private String title;
    private String price;
    private String img;
    private String goodUrl;

}
