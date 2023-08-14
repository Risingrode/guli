# ElasticSearch


## 基础概念
- 索引 : 一类具有相同特征的文档集合 对应数据库
- 类型 : 索引下的文档集合 对应表
- 文档 : 类型下的文档 对应行


## 倒排索引
- 倒排索引是实现搜索引擎的核心技术
- 倒排索引是文档中的单词到文档的映射


## 倒排索引的结构
- 倒排索引由倒排索引表和文档列表组成
- 倒排索引表中的每一项都由一个单词和一个指向文档列表中该单词出现位置的指针组成
- 文档列表中存储了包含该单词的文档的编号和出现位置


# crud

## _cat 查看集群信息
* GET /_cat/nodes：查看所有节点
    - http://192.168.199.131:9200/_cat/nodes?v
* GET /_cat/health：查看 es 健康状况
* GET /_cat/master：查看主节点
* GET /_cat/indices：查看所有索引 show databases;

## 保存数据
* PUT /索引/类型/文档id
* POST customer/external/1
    - 在customer索引下的external类型下创建id为1的文档

## 查询数据
* GET customer/external/1
    - 查询customer索引下的external类型下id为1的文档
```txt
{ 
  "_index": "customer", //在哪个索引
  "_type": "external", //在哪个类型
  "_id": "1", //记录 id
  "_version": 2, //版本号
  "_seq_no": 1, //并发控制字段，每次更新就会+1，用来做乐观锁
  "_primary_term": 1, //同上，主分片重新分配，如重启，就会变化
  "found": true, "_source": { //真正的内容
    "name": "John Doe"
  }
}
```
`?if_seq_no=0&if_primary_term=1`: 乐观锁，如果版本号不一致，就更新失败
乐观锁：在更新数据时，先获取数据的版本号，然后更新数据时，带上版本号，如果版本号不一致，就更新失败
如果出现上面的情况，就需要重新获取数据，然后再更新，会报409的错误

## 更新数据

* PUT customer/external/1/_update: 更新数据, 会覆盖原来的数据,这里的post可以换成put
```json
{
  "doc": {
    "name": "Jane Doe"
  }
}
```

* POST customer/external/1: 更新数据, 不会覆盖原来的数据,更新同时增加属性
```json
{
  "doc": {
    "name": "Jane Doe"
  },
  "doc_as_upsert": true
}
```

* POST 操作：当执行 POST 操作时，会比较源文档数据，并根据比较结果决定是否执行操作。如果源文档数据与目标文档数据相同，不会执行任何操作，并且文档的版本号不会增加。
* PUT 操作：无论源文档数据与目标文档数据是否相同，执行 PUT 操作总会将数据重新保存，并增加文档的版本号。
* 带有 _update 的操作：当执行带有 _update 的操作时，会比较元数据（metadata），如果元数据相同，不会执行任何操作。
* 对于大并发更新：可以选择不带 _update 的操作，以避免额外的元数据比较开销。
* 对于大并发查询偶尔更新：可以选择带有 _update 的操作，通过比较元数据来决定是否执行操作，从而避免不必要的更新操作，并重新计算分配规则。
* 另外，如果想要在更新文档的同时增加新属性，可以使用 POST 操作，并在 _update 中使用 doc 字段来指定要更新的属性。

## 删除数据
* DELETE customer/external/1
    - 删除customer索引下的external类型下id为1的文档
* DELETE customer
    - 删除customer索引

## 批量操作

本机登录这个网站：http://192.168.199.131:5601/app/kibana#/home?_g=()

* 批量操作可以在一次请求中执行多个操作，从而减少网络开销。

* POST customer/external/_bulk
```txt
{"index":{"_id":"1"}}
{"name":"John Doe"}
{"index":{"_id":"2"}}
{"name":"Jane Doe"}
```
* POST /_bulk
```txt
{ "delete": { "_index": "website", "_type": "blog", "_id": "123" }}
{ "create": { "_index": "website", "_type": "blog", "_id": "123" }}
{ "title": "My first blog post" }
{ "index": { "_index": "website", "_type": "blog" }}
{ "title": "My second blog post" }
{ "update": { "_index": "website", "_type": "blog", "_id": "123"} }
{ "doc" : {"title" : "My updated blog post"} }
```

`注意：`所有(POST,GET,DELETE,PUT)请求下面不能有空行

# 高级操作

ES 支持两种基本方式检索 :
* 一个是通过使用 REST request URI 发送搜索参数（uri+检索参数）
* 另一个是通过使用 REST request body 来发送它们（uri+请求体）

## 搜索参数
* GET /bank/_search?q=*&sort=account_number:asc&pretty
    - 搜索bank索引下的所有文档,并按照account_number升序排序
* GET /bank/_search            
    - 搜索bank索引下的所有文档
```json
{
  "query": {
    "match_all": {}
  },
    "sort": [
        {
        "account_number": {
            "order": "asc"
          }
        }
    ]
}
```
> 这种方式使用的比较多，叫做 DSL 查询


## 匹配查询

### 多词匹配
`GET bank/_search?q=address:mill&pretty`
```json
{
  "query": {
    "match": {
      "address": "mill road"
    }
  }
}
```
> 最终查询出 address 中包含 mill 或者 road 或者 mill road 的所有记录，并给出相


### 短语匹配
> 将需要匹配的值当成一个整体单词（不分词）进行检索
`GET bank/_search`
```json
{
  "query": {
    "match_phrase": {
      "address": "mill road"
    }
  }
}
```

### 多字段匹配
> 有时候我们需要在多个字段中进行匹配，比如我们需要在 address 和 city 字段中同时匹配 mill road，这时候我们可以使用 multi_match 查询
`GET bank/_search`
```json
{
  "query": {
    "multi_match": {
      "query": "mill road",
      "fields": ["address", "city"]
    }
  }
}
```

### 复合查询
> 复合查询是将多个查询组合在一起，比如我们需要在 address 字段中匹配 mill road，这时候我们可以使用 bool 查询
`GET bank/_search`
```json
{
  "query": {
    "bool": {
      "must": [
        {
          "match": {
            "address": "mill road"
          }
        }
      ]
    }
  }
}
```

#### must_not, should, match
`GET bank/_search`
```json
{
  "query": {
    "bool": {
      "must": [
        {
          "match": {
            "address": "mill"
          }
        },
        {
          "match": {
            "gender": "M"
          }
        }
      ],
      "should": [
        {
          "match": {
            "address": "lane"
          }
        }
      ],
      "must_not": [
        {
          "match": {
            "email": "baluba.com"
          }
        }
      ]
    }
  }
}

```

> must：必须匹配，相当于 and , should：可以匹配，相当于 or ,  must_not：不能匹配，相当于 not
> must_not 不能单独使用，必须和 must 或者 should 一起使用
> should 可以单独使用，但是没有意义，因为没有 must，所以不会影响查询结果
> address 包含 mill，并且 gender 是 M，如果 address 里面有 lane 最好不过，但是 email 必须不包含 baluba.com

### 结果过滤 filter
> filter 可以用来过滤查询结果，比如我们需要查询 address 中包含 mill 的所有记录，并且 account_number 大于 10，这时候我们可以使用 filter
> filter 会缓存查询结果，所以效率比较高
`GET bank/_search`
```json
{
  "query": {
    "bool": {
      "filter": [
        {
          "match": {
            "address": "mill"
          }
        },
        {
          "range": {
            "account_number": {
              "gt": 10
            }
          }
        }
      ]
    }
  }
}
```

### term
> term 查询是精确查询，不会对查询条件进行分词，比如我们需要查询 address 中包含 mill road 的所有记录，这时候我们可以使用 term 查询
> 全文检索字段用 match，其他非 text 字段匹配用 term
`GET bank/_search`
```json
{
  "query": {
    "term": {
      "address": "mill road"
    }
  }
}
```

### aggregations 聚合
> 聚合是对查询结果进行统计分析，比如我们需要统计每个 state 下的人数，这时候我们可以使用聚合
`GET bank/_search`
```json
{
  "size": 0,
  "aggs": {
    "group_by_state": {
      "terms": {
        "field": "state.keyword"
      }
    }
  }
}
```
> size: 0 表示不返回查询结果，只返回聚合结果
> aggs: 表示聚合
> group_by_state: 聚合名称
> terms: 表示使用 terms 聚合，也就是分组
> field: 表示分组字段

复杂聚合： `GET bank/account/_search`
```txt
{
  "query": {
    "match_all": {}// 匹配所有
  },
  "aggs": {
    "age_avg": {
      "terms": {// 按照age字段查找，最多找1000个
        "field": "age",
        "size": 1000
      },
      "aggs": {
        "balances_avg": {
          "avg": {//"balances_avg" 聚合项将计算 "balance" 字段的平均值，然后可以在查询结果中看到这个平均值。
            "field": "balance"
          }
        }
      }
    }
  },
  "size": 1000
}
```

> 复杂：查出所有年龄分布，并且这些年龄段中 M 的平均薪资和 F 的平均薪资以及这个年龄 段的总体平均薪资
`GET bank/account/_search`
```json
{
  "query": {
    "match_all": {}
  },
  "aggs": {
    "age_agg": {
      "terms": {
        "field": "age",
        "size": 100
      },
      "aggs": {
        "gender_agg": {
          "terms": {
            "field": "gender.keyword",
            "size": 100
          },
          "aggs": {
            "balance_avg": {
              "avg": {
                "field": "balance"
              }
            }
          }
        }
      }
    },
    "balance_avg": {
      "avg": {
        "field": "balance"
      }
    }
  },
  "size": 1000
}
```

1. `"query": { "match_all": {} }`: 这是一个查询部分，表示使用 "match_all" 查询来匹配所有文档。

2. `"aggs": { ... }`: 这是聚合部分，它包含多个级别的聚合操作。

   - `"age_agg": { "terms": { "field": "age", "size": 100 }, ... }`: 这是一个桶聚合，根据 "age" 字段进行分桶，最多创建 100 个桶。每个桶将代表一个不同的年龄值。

     - `"gender_agg": { "terms": { "field": "gender.keyword", "size": 100 }, ... }`: 在每个年龄桶内，根据 "gender" 字段进行进一步的分桶，最多创建 100 个桶。每个桶将代表一个不同的性别值。

       - `"balance_avg": { "avg": { "field": "balance" } }`: 在每个性别桶内，计算 "balance" 字段的平均值。

   - `"balance_avg": { "avg": { "field": "balance" } }`: 计算整个索引中 "balance" 字段的平均值。

3. `"size": 1000`: 这是返回的文档数量限制，最多返回 1000 个文档。

综合起来，这段代码的作用是查询索引中的所有文档，并进行多级聚合操作，包括对不同年龄和性别的文档进行桶分组，并计算每个桶内的平均余额。最终，还计算整个索引中 "balance" 字段的平均值。

## Mapping
> Mapping 是 Elasticsearch 中的一个重要概念，它类似于关系型数据库中的 schema，用于定义文档的各种属性，包括数据类型、分词器、是否索引等等。
> Mapping 一旦定义，就不能修改，只能删除索引重新创建，所以在创建索引之前，一定要先设计好 Mapping。
> Mapping 有两种创建方式，一种是自动创建，一种是手动创建，自动创建是 Elasticsearch 根据数据自动推断出来的，手动创建是我们自己定义的。
> 自动创建的 Mapping 有时候并不是我们想要的，所以我们一般都会手动创建 Mapping。
> Mapping 一旦定义，就不能修改，只能删除索引重新创建，所以在创建索引之前，一定要先设计好 Mapping。

### 自己创建
> PUT /my-index
```json
{
  "mappings": {
    "properties": {
      "name": {
        "type": "text"
      },
      "age": {
        "type": "integer"
      },
      "birthday": {
        "type": "date"
      },
      "address": {
        "type": "text"
      },
      "remark": {
        "type": "text"
      }
    }
  }
}
```

> text字段适用于全文检索，keyword字段适用于精确匹配

### 为已有索引添加字段
> PUT /my-index/_mapping
```json
{
  "properties": {
    "remark": {
      "type": "text",
      "index": false
    }
  }
}
```
> 添加一个字段，字段名为 remark，类型为 text，index: false 表示不索引，也就是不分词，只能精确匹配,不可以全文检索
> index: false 表示不索引，也就是不分词，只能精确匹配,不可以全文检索

### 修改字段类型
> PUT /my-index/_mapping
```json
{
  "properties": {
    "age": {
      "type": "text"
    }
  }
}
```

> 正常情况下，字段类型是不能修改的，但是可以通过以下方式修改：
> 1. 先删除字段，再添加字段
> 2. 添加一个新字段，然后将旧字段的数据复制到新字段，最后删除旧字段
> 3. 使用 reindex API 将旧字段的数据复制到新字段，最后删除旧字段

### reindex
> POST _reindex
```json
{
  "source": {
    "index": "my-index"
  },
  "dest": {
    "index": "my-index-new"
  }
}
```

# 分词
> 分词是指将一段文本按照一定规则切分成一个个词语的过程，这些词语就是分词后的结果。
> 分词是搜索引擎的基础，因为搜索引擎的目的就是根据用户输入的关键词，从文档中检索出相关的文档，而用户输入的关键词就是分词后的结果。
> 分词的结果会被存储到倒排索引中，所以分词的好坏直接影响到搜索的效果。

## 分词器






































