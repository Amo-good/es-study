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



