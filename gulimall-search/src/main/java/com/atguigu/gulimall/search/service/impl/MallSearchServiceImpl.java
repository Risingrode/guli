package com.atguigu.gulimall.search.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.atguigu.common.to.es.SkuEsModel;
import com.atguigu.common.utils.R;
import com.atguigu.gulimall.search.config.GulimallElasticSearchConfig;
import com.atguigu.gulimall.search.constant.EsConstant;
import com.atguigu.gulimall.search.feign.ProductFeignService;
import com.atguigu.gulimall.search.service.MallSearchService;
import com.atguigu.gulimall.search.vo.AttrResponseVo;
import com.atguigu.gulimall.search.vo.SearchParam;
import com.atguigu.gulimall.search.vo.SearchResVo;
import com.atguigu.gulimall.search.vo.SearchResult;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class MallSearchServiceImpl implements MallSearchService {
    @Autowired
    private RestHighLevelClient client;
    @Autowired
    private ProductFeignService productFeignService;

    @Override
    // 去es进行检索
    public Object search(SearchParam param) {
        // 1. 动态构建出查询需要的DSL语句
        SearchResult result = null;
        // 准备请求
        SearchRequest searchRequest = buildSearchRequest(param);
        try {
            // 执行检索请求
            SearchResponse response = client.search(searchRequest, GulimallElasticSearchConfig.COMMON_OPTIONS);
            // 分析我们的响应数据封装成我们需要的格式
            result = buildSearchResult(response,param);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    // 构建结果数据
    private SearchResult buildSearchResult(SearchResponse response,SearchParam param) {
        SearchResult result = new SearchResult();

        //1、返回的所有查询到的商品
        SearchHits hits = response.getHits();

        List<SkuEsModel> esModels = new ArrayList<>();
        //遍历所有商品信息
        if (hits.getHits() != null) {
            for (SearchHit hit : hits.getHits()) {
                String sourceAsString = hit.getSourceAsString();
                SkuEsModel esModel = JSON.parseObject(sourceAsString, SkuEsModel.class);
                //判断是否按关键字检索，若是就显示高亮，否则不显示
                if (!StringUtils.isEmpty(param.getKeyword())) {
                    //拿到高亮信息显示标题
                    HighlightField skuTitle = hit.getHighlightFields().get("skuTitle");
                    String skuTitleValue = skuTitle.getFragments()[0].string();
                    esModel.setSkuTitle(skuTitleValue);
                }
                esModels.add(esModel);
            }
        }
        result.setSkuEsModels(esModels);

        //2、当前商品涉及到的所有属性信息
        List<SearchResult.Attrs> attrVos = new ArrayList<>();
        //获取属性信息的聚合
        ParsedNested attrsAgg = response.getAggregations().get("attr_agg");
        ParsedLongTerms attrIdAgg = attrsAgg.getAggregations().get("attr_id_agg");
        for (Terms.Bucket bucket : attrIdAgg.getBuckets()) {
            SearchResult.Attrs attrVo = new SearchResult.Attrs();
            //1、得到属性的id
            long attrId = bucket.getKeyAsNumber().longValue();
            attrVo.setAttrId(attrId);

            //2、得到属性的名字
            ParsedStringTerms attrNameAgg = bucket.getAggregations().get("attr_name_agg");
            String attrName = attrNameAgg.getBuckets().get(0).getKeyAsString();
            attrVo.setAttrName(attrName);

            //3、得到属性的所有值
            ParsedStringTerms attrValueAgg = bucket.getAggregations().get("attr_value_agg");
            List<String> attrValues = attrValueAgg.getBuckets().stream().map(item -> item.getKeyAsString()).collect(Collectors.toList());
            attrVo.setAttrValue(attrValues);

            attrVos.add(attrVo);
        }
        result.setAttrs(attrVos);

        //3、当前商品涉及到的所有品牌信息
        List<SearchResult.BrandVo> brandVos = new ArrayList<>();
        //获取到品牌的聚合
        ParsedLongTerms brandAgg = response.getAggregations().get("brand_agg");
        for (Terms.Bucket bucket : brandAgg.getBuckets()) {
            SearchResult.BrandVo brandVo = new SearchResult.BrandVo();

            //1、得到品牌的id
            long brandId = bucket.getKeyAsNumber().longValue();
            brandVo.setBrandId(brandId);

            //2、得到品牌的名字
            ParsedStringTerms brandNameAgg = bucket.getAggregations().get("brand_name_agg");
            String brandName = brandNameAgg.getBuckets().get(0).getKeyAsString();
            brandVo.setBrandName(brandName);

            //3、得到品牌的图片
            ParsedStringTerms brandImgAgg = bucket.getAggregations().get("brand_img_agg");
            String brandImg = brandImgAgg.getBuckets().get(0).getKeyAsString();
            brandVo.setBrandImg(brandImg);

            brandVos.add(brandVo);
        }
        result.setBrandVos(brandVos);

        //4、当前商品涉及到的所有分类信息
        //获取到分类的聚合
        List<SearchResult.CatalogVo> catalogVos = new ArrayList<>();
        ParsedLongTerms catalogAgg = response.getAggregations().get("catalog_agg");
        for (Terms.Bucket bucket : catalogAgg.getBuckets()) {
            SearchResult.CatalogVo catalogVo = new SearchResult.CatalogVo();
            //得到分类id
            String keyAsString = bucket.getKeyAsString();
            catalogVo.setCatalogId(Long.parseLong(keyAsString));
            //得到分类名
            ParsedStringTerms catalogNameAgg = bucket.getAggregations().get("catalog_name_agg");
            String catalogName = catalogNameAgg.getBuckets().get(0).getKeyAsString();
            catalogVo.setCatalogName(catalogName);
            catalogVos.add(catalogVo);
        }
        result.setCatalogVos(catalogVos);

        //===============以上可以从聚合信息中获取====================//
        //5、分页信息-页码
        result.setPageNum(param.getPageNum());
        //5、1分页信息、总记录数
        long total = hits.getTotalHits().value;
        result.setTotalRecords(total);
        //5、2分页信息-总页码
        int totalPages = (int)total % EsConstant.PRODUCT_PAGESIZE == 0 ?
                (int)total / EsConstant.PRODUCT_PAGESIZE : ((int)total / EsConstant.PRODUCT_PAGESIZE + 1);

        result.setTotalPages(totalPages);
        //  6.封装面包屑导航栏数据
        //List<SearchResult.NavVo> navVos = new ArrayList<>();
        if(param.getAttrs()!=null&&param.getAttrs().size()>0){
            // 有attrs参数，才做面包屑导航栏
            List<SearchResult.NavVo> attrs = param.getAttrs().stream().map(attr -> {
                String[] s = attr.split("_");
                SearchResult.NavVo navVo = new SearchResult.NavVo();
                // 封装属性值
                navVo.setNavValue(s[1]);
                // 远程调用
                R r=productFeignService.info(Long.parseLong(s[0]));
                if(r.getCode()==0) {
                    AttrResponseVo attrVo = r.getData("attr", new TypeReference<AttrResponseVo>() {});
                    navVo.setNavName(attrVo.getAttrName());
                } else{
                    // 出现异常则封装id
                    navVo.setNavName(s[0]);
                }
                // 封装面包屑导航栏的链接
                String replace = getString(param, attr);
                navVo.setLink("http://search.gulimall.com/list.html?" + replace);
                return navVo;
            }).collect(Collectors.toList());
            result.setNavVos(attrs);
        }

        // 品牌面包屑导航
        if(param.getBrandId()!=null&&param.getBrandId().size()>0){

        }




        return result;
    }

    // URL 替换方法
    private static String getString(SearchParam param, String attr) {
        String encode=null;
        try{
            encode= URLEncoder.encode(attr,"UTF-8");
            // 有些符号 浏览器编码和java不一致
            encode=encode.replace("+","%20").replace("%28","(").replace("%29",")");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        String replace = param.get_queryString().replace("&attrs=" + attr, "");
        return replace;
    }

    // 准备检索请求
    // 模糊匹配，过滤，排序，高亮，聚合分析
    private SearchRequest buildSearchRequest(SearchParam param) {
        SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();// 构建DSL语句
        // 1. 构建bool-query
        BoolQueryBuilder boolQueryBuilder = new BoolQueryBuilder();
        // 1.1 must-模糊匹配
        if (!StringUtils.isEmpty(param.getKeyword())) {
            boolQueryBuilder.must(QueryBuilders.matchQuery("skuTitle", param.getKeyword()));
        }
        // 1.2 filter-按照属性，分类，品牌，价格区间，库存等信息进行过滤
        // 1.2.1 按照三级分类id进行过滤
        if (param.getCatalog3Id() != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("catalogId", param.getCatalog3Id()));
        }
        // 1.2.2 按照品牌id进行过滤
        if (param.getBrandId() != null && param.getBrandId().size() > 0) {
            boolQueryBuilder.filter(QueryBuilders.termsQuery("brandId", param.getBrandId()));
        }
        // 1.2.3 按照是否有货进行过滤
        if (param.getHasStock() != null) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("hasStock", param.getHasStock() == 1));
        }
        // 1.2.4 按照价格区间进行过滤
        if (!StringUtils.isEmpty(param.getSkuPrice())) {
            // skuPrice=1_500/_500/500_/
            String[] price = param.getSkuPrice().split("_");// 进行分割
            if (price.length == 2) {
                boolQueryBuilder.filter(QueryBuilders.rangeQuery("skuPrice").gte(price[0]).lte(price[1]));
            } else if (price.length == 1) {
                if (param.getSkuPrice().startsWith("_")) {
                    boolQueryBuilder.filter(QueryBuilders.rangeQuery("skuPrice").lte(price[0]));
                } else {
                    boolQueryBuilder.filter(QueryBuilders.rangeQuery("skuPrice").gte(price[0]));
                }
            }
        }
        // 1.2.5 按照属性进行过滤
        if (param.getAttrs() != null && param.getAttrs().size() > 0) {
            for (String attr : param.getAttrs()) {
                // attrs=1_2寸:4.5寸&attrs=2_16G:8G
                BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
                String[] attrSplit = attr.split("_");
                String attrId=attrSplit[0];
                // attrSplit[0] = 1
                queryBuilder.must(QueryBuilders.termQuery("attrs.attrId", attrId));
                // attrSplit[1] = 2寸:4.5寸
                String[] attrValues = attrSplit[1].split(":");
                // attrValues[0] = 2寸
                // attrValues[1] = 4.5寸
                queryBuilder.must(QueryBuilders.termsQuery("attrs.attrId", attrId));
                queryBuilder.must(QueryBuilders.termsQuery("attrs.attrValue", attrValues));
                // 每一个都要生成nest查询
                NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("attrs", queryBuilder, ScoreMode.None);
                boolQueryBuilder.filter(nestedQueryBuilder);
            }
        }
        sourceBuilder.query(boolQueryBuilder);

        // 1.2.6 sort-排序
        if (!StringUtils.isEmpty(param.getSort())) {
            // sort=hotScore_asc/desc
            String sort = param.getSort();
            String[] sortSplit = sort.split("_");
            // sortSplit[0] = hotScore
            // sortSplit[1] = asc/desc
            SortOrder order = sortSplit[1].equalsIgnoreCase("asc") ? SortOrder.ASC : SortOrder.DESC;
            sourceBuilder.sort(sortSplit[0]);
        }
        // 分页
        sourceBuilder.from((param.getPageNum() - 1) * EsConstant.PRODUCT_PAGESIZE);
        sourceBuilder.size( EsConstant.PRODUCT_PAGESIZE);
        // 1.4 highlight-高亮
        if(!StringUtils.isEmpty(param.getKeyword())){
            HighlightBuilder highlightBuilder = new HighlightBuilder();
            highlightBuilder.field("skuTitle");
            highlightBuilder.preTags("<b style='color:red'>");
            highlightBuilder.postTags("</b>");
            sourceBuilder.highlighter(highlightBuilder);
        }
        // TODO : 这里的名字不对，别忘了改
        // 1.5 aggs-聚合分析
        // 品牌聚合
        TermsAggregationBuilder brand_agg = AggregationBuilders.terms("brand_agg").field("brandId").size(50);
        // 品牌聚合下的子聚合
        brand_agg.subAggregation(AggregationBuilders.terms("brandName_agg").field("brandName").size(1));
        brand_agg.subAggregation(AggregationBuilders.terms("brandImg_agg").field("brandImg").size(1));
        sourceBuilder.aggregation(brand_agg);
        // 分类聚合
        TermsAggregationBuilder catalog_agg = AggregationBuilders.terms("catalog_agg").field("catalogId").size(20);
        // 分类聚合下的子聚合
        catalog_agg.subAggregation(AggregationBuilders.terms("catalogName_agg").field("catalogName").size(1));
        sourceBuilder.aggregation(catalog_agg);
        // 属性聚合
        NestedAggregationBuilder attr_agg = AggregationBuilders.nested("attr_agg", "attrs");
        // 属性聚合下的子聚合
        TermsAggregationBuilder attrId_agg = AggregationBuilders.terms("attrId_agg").field("attrs.attrId").size(20);
        attrId_agg.subAggregation(AggregationBuilders.terms("attrName_agg").field("attrs.attrName").size(1));
        attrId_agg.subAggregation(AggregationBuilders.terms("attrValue_agg").field("attrs.attrValue").size(50));
        attr_agg.subAggregation(attrId_agg);
        sourceBuilder.aggregation(attr_agg);

        String s=sourceBuilder.toString();
        System.out.println("构建的DSL语句："+s);
        SearchRequest searchRequest = new SearchRequest(new String[]{EsConstant.PRODUCT_INDEX}, sourceBuilder);

        return null;
    }

}
