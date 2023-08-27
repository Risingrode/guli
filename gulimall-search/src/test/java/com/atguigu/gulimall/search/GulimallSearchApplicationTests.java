package com.atguigu.gulimall.search;

import com.atguigu.gulimall.search.config.GulimallElasticSearchConfig;
import lombok.Data;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.aggregations.metrics.Avg;
import org.elasticsearch.search.aggregations.metrics.AvgAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSON;

import java.io.IOException;
import java.util.Map;


@RunWith(SpringRunner.class)
@SpringBootTest
class GulimallSearchApplicationTests {
    @Autowired
    public RestHighLevelClient client;

    // 保存数据
    @Test
    void indexData() throws IOException {
        // 设置索引
        IndexRequest indexRequest = new IndexRequest("users");
        indexRequest.id("1");
        User user = new User();
        user.setUsername("zhangsan");
        user.setAge(18);
        user.setGender("男");
        String jsonString = JSON.toJSONString(user);
        indexRequest.source(jsonString, XContentType.JSON);// 要保存的内容
        // 执行操作
        IndexResponse index= client.index(indexRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);
        // 提取有用的响应数据
        System.out.println(index);
    }

    @Data
    class User{
        private String username;
        private String gender;
        private Integer age;
    }

    @Test
    public void contextLoads() {
        System.out.println(client);
    }

    @Test
    public void searchData() throws IOException{
        // 创建检索请求
        SearchRequest searchRequest=new SearchRequest();
        // 指定索引
        searchRequest.indices("bank");
        // 指定DSL，检索条件
        SearchSourceBuilder sourceBuilder=new SearchSourceBuilder();
        searchRequest.source(sourceBuilder);
        // 分析结果
        sourceBuilder.query(QueryBuilders.matchQuery("address","mill"));
        // 按照年龄的值进行聚合 terms:聚合的类型 ageAgg:聚合的名字  field:聚合的字段  size:聚合的个数
        TermsAggregationBuilder ageAgg = AggregationBuilders.terms("ageAgg").field("age").size(10);
        sourceBuilder.aggregation(ageAgg);
        // 按照年龄的平均值进行聚合
        AvgAggregationBuilder balanceAvg = AggregationBuilders.avg("balanceAvg").field("balance");
        sourceBuilder.aggregation(balanceAvg);

        System.out.println("检索条件"+sourceBuilder.toString());
        searchRequest.source(sourceBuilder);
        // 执行检索
        SearchResponse searchResponse = client.search(searchRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);
        // 分析结果
        System.out.println("响应："+searchResponse.toString());
        // JSON.parseObject(searchResponse.toString(), Map.class);
        // 获取所有查到的数据
        SearchHits hits = searchResponse.getHits();
        for(SearchHit hit:hits){
            // 获取每条数据的json字符串
            String sourceAsString = hit.getSourceAsString();
            // 把字符串转为对象
            Account account = JSON.parseObject(sourceAsString, Account.class);
            System.out.println("account:"+account);
        }
        // 获取聚合的分析数据
        Aggregations aggregations=searchResponse.getAggregations();
//        for (Aggregation aggregation : aggregations.asList()) {
//            System.out.println("当前聚合名字："+aggregation.getName());
//        }
        Terms ageAgg1=aggregations.get("ageAgg");
        for(Terms.Bucket bucket:ageAgg1.getBuckets()){
            String keyAsString = bucket.getKeyAsString();
            System.out.println("年龄："+keyAsString+"==>"+bucket.getDocCount());
        }

    }

    @Data
    public static class Account {
        private int account_number;
        private int balance;
        private String firstname;
        private String lastname;
        private int age;
        private String gender;
        private String address;
        private String employer;
        private String email;
        private String city;
        private String state;
    }

//    public static final RequestOptions COMMON_OPTIONS;
//    static{
//        RequestOptions.Builder builder=RequestOptions.DEFAULT.toBuilder();
//        COMMON_OPTIONS = builder.build();
//    }
       // 查询
//    public void find() throws IOException {
//        // 1 创建检索请求
//        SearchRequest searchRequest = new SearchRequest();
//        searchRequest.indices("bank");
//        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
//        // 构造检索条件
////        sourceBuilder.query();
////        sourceBuilder.from();
////        sourceBuilder.size();
////        sourceBuilder.aggregation();
//        sourceBuilder.query(QueryBuilders.matchQuery("address","mill"));
//        System.out.println(sourceBuilder.toString());
//        searchRequest.source(sourceBuilder);
//        // 2 执行检索
//        SearchResponse response = client.search(searchRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);
//        // 3 观察响应
//        System.out.println(response.toString());
//    }
  // 聚合查询
    @Test
    public void find() throws IOException {
        // 1 创建检索请求
        SearchRequest searchRequest = new SearchRequest();
        searchRequest.indices("bank");
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
        // 构造检索条件
        //        sourceBuilder.query();
        //        sourceBuilder.from();
        //        sourceBuilder.size();
        //        sourceBuilder.aggregation();
        sourceBuilder.query(QueryBuilders.matchQuery("address","mill"));
        //AggregationBuilders工具类构建AggregationBuilder
        // 构建第一个聚合条件:按照年龄的值分布
        TermsAggregationBuilder agg1 = AggregationBuilders.terms("agg1").field("age").size(10);// 聚合名称
        // 参数为AggregationBuilder
        sourceBuilder.aggregation(agg1);
        // 构建第二个聚合条件:平均薪资
        AvgAggregationBuilder agg2 = AggregationBuilders.avg("agg2").field("balance");
        sourceBuilder.aggregation(agg2);

        System.out.println("检索条件"+sourceBuilder.toString());

        searchRequest.source(sourceBuilder);

        // 2 执行检索
        SearchResponse response = client.search(searchRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);
        // 3 分析响应结果
        System.out.println(response.toString());
    }

    /**
    * @Description:
    * @Param: {
     *     skuId: 1
     *     spuId: 2
     *     skuTitle: 3
     *     skuPrice: 4
     *     skuImg: 5
     *     attrs:{
     *         {1,2,3,4,5,6,7,8,9,10},
     *     }
     * }
     *
    */






}
