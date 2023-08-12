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
```json
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



























