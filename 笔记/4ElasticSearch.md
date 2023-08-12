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
























