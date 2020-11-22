# ElasticSearch

* ES中的类型：
    * 字符串类型：text、keyword
    * 数值类型：long、integer、short、byte、double、float、half float、scaled float
    * 日期类型：date
    * 布尔值类型：boolean
    * 二进制类型：binary
    * 等等......

## ES索引的基本操作
```java
    /*
    关于索引(就相当于数据库)的基本操作：
        1.创建一个索引(完成创建索引的同时，数据也插入成功)
            PUT /test1/type1/1
            {
              "name": "王肖肖",
              "age": 23
            }
        2.创建一个索引(但是不向索引中添加数据)
            PUT /test2
            {
              "mappings": {
                "properties": {
                  "name": {
                    "type": "text"
                  },
                  "age": {
                    "type": "long"
                  },
                  "birthday": {
                    "type": "date"
                  }
                }
              }
            }
        3.通过 GET _cat/ 可以获得es的当前很多信息
            GET _cat/indices?v
        4.修改索引中的数据
            方式一：(不推荐) 不能缺少字段
                PUT /test1/type1/1
                {
                  "name": "王肖肖123",
                  "age": 23
                }
            方式二：(推荐)
                POST /test1/_doc/1/_update
                {
                  "doc": {
                    "name": "张三"
                  }
                }
        5.删除索引
            DELETE test1

     */
```

## ES关于文档的操作
```java
    /*
	关于文档(就相当于数据库中的数据)的操作：
		--> 基本操作
		1.添加数据
			PUT /people/user/1
			{
			  "name": "张三123",
			  "age": 23,
			  "desc": "一顿操作猛如虎，一看工资2500",
			  "tags": ["技术宅", "温暖", "直男"]
			}
			PUT /people/user/2
			{
			  "name": "李四",
			  "age": 20,
			  "desc": "哈哈哈",
			  "tags": ["交友", "旅游", "渣男"]
			}
			PUT /people/user/3
			{
			  "name": "王五",
			  "age": 3,
			  "desc": "6666",
			  "tags": ["交友", "玩耍", "学习"]
			}
			PUT /people/user/4
			{
			  "name": "张三",
			  "age": 3,
			  "desc": "一顿操作猛如虎，一看工资2500",
			  "tags": ["技术宅", "温暖", "直男"]
			}

		2.获取数据
			GET people/user/1
		3.更新数据
			POST /people/user/1/_update
			{
			  "doc": {
				"name": "张三123"
			  }
			}
		4.简单的搜索
			下面的语句，对于查询出来的多条记录，name的匹配度越高，查询结果hits中的_score越高
			GET /people/user/_search?q=name:张三123

		--> 复杂操作(搜索)
		1.查询，相当于相面的4
			GET /people/user/_search
			{
			  "query": {
				"match": {
				  "name": "张三123"
				}
			  }
			}
		2.不查询全部信息，查询指定字段信息(结果的过滤)
			GET /people/user/_search
			{
			  "query": {
				"match": {
				  "name": "张三123"
				}
			  },
			  "_source": ["name", "desc"]
			}
		3.查询结果按照某字段排序(会导致查询结果中的_score全为null)
			GET /people/user/_search
			{
			  "query": {
				"match": {
				  "name": "张三123"
				}
			  },
			  "sort": [
				{
				  "age": {
					"order": "asc"
				  }
				}
			  ]
			}
		4.分页(从0开始，只显示1条查询出来的数据)
			GET /people/user/_search
			{
			  "query": {
				"match": {
				  "name": "张三123"
				}
			  },
			  "from": 0,
			  "size": 1
			}
		5.布尔值查询
		must : 所有的条件都要满足 and
			GET /people/user/_search
			{
			  "query": {
				"bool": {
				  "must": [
					{
					  "match": {
						"name": "张三"
					  }
					},
					{
					  "match": {
						"age": "23"
					  }
					}
				  ]
				}
			  }
			}
		should : 满足某一个条件即可 or
			GET /people/user/_search
			{
			  "query": {
				"bool": {
				  "should": [
					{
					  "match": {
						"name": "张三"
					  }
					},
					{
					  "match": {
						"age": "23"
					  }
					}
				  ]
				}
			  }
			}
		must_not : 不满足该条件的数据 not
			GET /people/user/_search
			{
			  "query": {
				"bool": {
				  "must_not": [
					{
					  "match": {
						"age": "23"
					  }
					}
				  ]
				}
			  }
			}
		6.过滤器 filter
			GET /people/user/_search
			{
			  "query": {
				"bool": {
				  "must": [
					{
					  "match": {
						"name": "张三"
					  }
					}
				  ],
				  "filter": {
					"range": {
					  "age": {
						"gte": 3,
						"lt": 23
					  }
					}
				  }
				}
			  }
			}
		7.匹配多个条件(多个条件用空格隔开，只要满足其中一个条件即可被查出)
			GET /people/user/_search
			{
			  "query": {
				"match": {
				  "tags": "男 技术"
				}
			  }
			}
		8.精确查询(term查询是直接通过倒排索引指定的词条进行精确的查找的)
		term 是直接查找精确的
		match 会使用分词器解析
		-- 两个类型：text 和 keyword
			-- text 会被分词器解析
			-- keyword字段类型 不会被分词器解析
		测试：
			PUT testdb
			{
			  "mappings": {
				"properties": {
				  "name": {
					"type": "text"
				  },
				  "desc": {
					"type": "keyword"
				  }
				}
			  }
			}
			PUT testdb/_doc/1
			{
			  "name": "Java name",
			  "desc": "Java desc"
			}
			PUT testdb/_doc/2
			{
			  "name": "Java name",
			  "desc": "Java desc2"
			}
			GET _analyze
			{
			  "analyzer": "keyword",
			  "text": "Java name"
			}
			GET _analyze
			{
			  "analyzer": "standard",
			  "text": "Java name"
			}
			GET testdb/_search
			{
			  "query": {
				"match": {
				  "name": "java"
				}
			  }
			}
			GET testdb/_search
			{
			  "query": {
				"match": {
				  "desc": "Java desc"
				}
			  }
			}
		9.多个值匹配的精确查询
			PUT test4/_doc/1
			{
			  "t1": "22",
			  "t2": "2020-11-22"
			}
			PUT test4/_doc/2
			{
			  "t1": "33",
			  "t2": "2020-11-23"
			}
			GET test4/_search
			{
			  "query": {
				"bool": {
				  "should": [
					{
					  "term": {
						"t1": "22"
					  }
					},
					{
					  "term": {
						"t1": "33"
					  }
					}
				  ]
				}
			  }
			}
		10.高亮查询
			GET people/_search
			{
			  "query": {
				"match": {
				  "name": "张三"
				}
			  },
			  "highlight": {
				"fields": {
				  "name": {}
				}
			  }
			}
		自定义搜索高亮条件
			GET people/_search
			{
			  "query": {
				"match": {
				  "name": "张三"
				}
			  },
			  "highlight": {
				"pre_tags": "<p class='key' style='color:red'>", 
				"post_tags": "</p>", 
				"fields": {
				  "name": {}
				}
			  }
			}
	 */
```

