package com.atguigu.gulimall.search.config;

import org.apache.http.HttpHost;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
// 1. 导入依赖
// 2. 编写配置

@SpringBootConfiguration
@Configuration
public class GulimallElasticSearchConfig {

    public static final RequestOptions COMMON_OPTIONS;
    static{
        RequestOptions.Builder builder=RequestOptions.DEFAULT.toBuilder();
        COMMON_OPTIONS = builder.build();
    }


    @Bean
    public RestHighLevelClient esRestClient(){
        // 1. 创建es客户端连接
        RestClientBuilder builder=null;
        builder=RestClient.builder( new HttpHost("192.168.199.132",9200,"http"));
        RestHighLevelClient client=new RestHighLevelClient(builder);
//        RestHighLevelClient client=new RestHighLevelClient(
//                RestClient.builder(
//                        new HttpHost("192.168.199.132",9200,"http")));
        return client;
    }

}
