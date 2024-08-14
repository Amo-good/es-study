> 导入数据到数据库

1. 运行tb_hotel.sql文件，初始化数据
2. 新建数据库对象Hotel对应表、HotelDoc对应es的文档
3. 在es中创建索引，索引的语法如下
```josn
PUT /hotel
{
  "mappings": {
    "properties": {
      "id": {
        "type": "keyword"
      },
      "name":{
        "type": "text",
        "analyzer": "ik_max_word",
        "copy_to": "all"
      },
      "address":{
        "type": "keyword",
        "index": false
      },
      "price":{
        "type": "integer"
      },
      "score":{
        "type": "integer"
      },
      "brand":{
        "type": "keyword",
        "copy_to": "all"
      },
      "city":{
        "type": "keyword",
        "copy_to": "all"
      },
      "starName":{
        "type": "keyword"
      },
      "business":{
        "type": "keyword"
      },
      "location":{
        "type": "geo_point"
      },
      "pic":{
        "type": "keyword",
        "index": false
      },
      "all":{
        "type": "text",
        "analyzer": "ik_max_word"
      }
    }
  }
}
```
<b>在es-product中创建查询方法，返回HotelDoc的集合，因为es-consumer不连数据库，所以需要去调用es-product的方法。</b>
4. 在SpringBootTest中运行批量插入方法（testAddDocument），将数据库的数据批量导入es中

5. 酒店项目，最终的查询
```bash
GET /hotel/_search
{
  "query": {
    "function_score": {
      "query": {
        "bool": {
          "must": [
            {
              "match": {
                "name": "深圳"
              }
            }
          ],
          "filter": [
            {
              "term": {
                "city": "深圳"
              }
            },
            {
              "term": {
                "brand": "如家"
              }
            },
            {
              "term": {
                "starName": "五钻"
              }
            },
            {
             "range": {
               "price": {
                 "gte": 100,
                 "lte": 300
               }
             }
            }
          ]
        }
      },
      "functions": [
        {
          "filter": {
            "term": {
              "isAD": true
            }
          },
          "weight": 10
        }
      ],
      "boost_mode": "multiply"
    }
  },
  "from": 0,
  "size": 5,
  "sort": [
    {
      "_geo_distance": {
        "location": {
          "lat": 21.5,
          "lon": 120.93
        },
        "order": "asc"
      }
    },
    {
      "price": "desc"
    }
  ],
  "highlight": {
    "fields": {
      "name":{
        "pre_tags": "<em>",
        "post_tags": "</em>"
      }
    }
  }
}



```

6. 添加拼音分词器
> 修改新的酒店索引
```bash
# 先删除原来的索引
DELETE /hotel
# 酒店数据索引库
PUT /hotel
{
  "settings": {
    "analysis": {
      "analyzer": {
        "text_anlyzer": {
          "tokenizer": "ik_max_word",
          "filter": "py"
        },
        "completion_analyzer": {
          "tokenizer": "keyword",
          "filter": "py"
        }
      },
      "filter": {
        "py": {
          "type": "pinyin",
          "keep_full_pinyin": false,
          "keep_joined_full_pinyin": true,
          "keep_original": true,
          "limit_first_letter_length": 16,
          "remove_duplicated_term": true,
          "none_chinese_pinyin_tokenize": false
        }
      }
    }
  },
  "mappings": {
    "properties": {
      "id":{
        "type": "keyword"
      },
      "name":{
        "type": "text",
        "analyzer": "text_anlyzer",
        "search_analyzer": "ik_smart",
        "copy_to": "all"
      },
      "address":{
        "type": "keyword",
        "index": false
      },
      "price":{
        "type": "integer"
      },
      "score":{
        "type": "integer"
      },
      "brand":{
        "type": "keyword",
        "copy_to": "all"
      },
      "city":{
        "type": "keyword"
      },
      "starName":{
        "type": "keyword"
      },
      "business":{
        "type": "keyword",
        "copy_to": "all"
      },
      "location":{
        "type": "geo_point"
      },
      "pic":{
        "type": "keyword",
        "index": false
      },
      "all":{
        "type": "text",
        "analyzer": "text_anlyzer",
        "search_analyzer": "ik_smart"
      },
      "suggestion":{
          "type": "completion",
          "analyzer": "completion_analyzer"
      }
    }
  }
}
```
> 修改酒店HotelDoc的构造方法

我们需要加入自动补齐的词条到`suggestion`字段中去,我们把商圈和品牌列入自动补齐中

> 重新导入酒店的数据
运行`testAddDocument`的方法

> 自动补齐的语法如下

```bash
# 自动补全查询
POST /hotel/_search
{
  "suggest": {
    "suggestions": {
      "text": "s", 
      "completion": {
        "field": "suggestion", 
        "skip_duplicates": true, 
        "size": 10 
      }
    }
  }
}

```
