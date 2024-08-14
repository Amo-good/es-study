package com.es.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.es.service.EsHotelService;
import es.entity.dto.HotelDoc;
import es.entity.param.HotelParam;
import es.entity.vo.PageResult;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.functionscore.FunctionScoreQueryBuilder;
import org.elasticsearch.index.query.functionscore.ScoreFunctionBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author amo
 */
@Service
public class EsHotelServiceImpl implements EsHotelService {

    @Resource
    private RestHighLevelClient client;

    @Override
    public Map<String, List<String>> filters(HotelParam param) {
        //条件筛选也需要根据当前的输入框的值筛选
        Map<String,List<String>> result = new HashMap<>();
        SearchRequest request = new SearchRequest("hotel");
        //查询一致
        buildBasicQuery(param,request);
        //聚合操作
        buildAggregation(request);
        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            Aggregations aggregations = response.getAggregations();
            result.put("brand", getBucketKeyList(aggregations,"brandAgg"));
            result.put("city", getBucketKeyList(aggregations,"cityAgg"));
            result.put("starName", getBucketKeyList(aggregations,"starNameAgg"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public List<String> suggestion(String prefix) {
        List<String> result = new ArrayList<>();
        //自动补齐功能
        SearchRequest request = new SearchRequest("hotel");
        //跳过重复的
        request.source().suggest(new SuggestBuilder().addSuggestion(
                "suggestions",
                SuggestBuilders.completionSuggestion("suggestion").text(prefix).skipDuplicates(true).size(10)
        ));
        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            CompletionSuggestion completionSuggestion=response.getSuggest().getSuggestion("suggestions");
            for (CompletionSuggestion.Entry.Option option : completionSuggestion.getOptions()) {
                //获取自动补全的值
                String text = option.getText().toString();
                result.add(text);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    private List<String> getBucketKeyList(Aggregations aggregations,String name) {
        Terms brandTerm = aggregations.get(name);
        List<? extends Terms.Bucket> buckets = brandTerm.getBuckets();
        return buckets.stream().map(bucket -> bucket.getKey().toString()).collect(Collectors.toList());
    }


    private void buildAggregation(SearchRequest request) {
        //品牌聚合
        request.source().aggregation(AggregationBuilders.terms("brandAgg").field("brand").size(10));
        //城市
        request.source().aggregation(AggregationBuilders.terms("cityAgg").field("city").size(10));
        //星级
        request.source().aggregation(AggregationBuilders.terms("starNameAgg").field("starName").size(10));
        //不需要查询文档
        request.source().size(0);
    }

    @Override
    public PageResult list(HotelParam params) {
        PageResult pageResult = new PageResult();
        //GET /hotel/_search
        SearchRequest request = new SearchRequest("hotel");
        /**
         * {
         *     "query":{
         *         "match":{
         *              "all":"上海"
         *         }
         *     }
         * }
         */
        //复合查询
       buildBasicQuery(params,request);
        //结果集处理
        //处理分页
        Integer size = params.getSize();
        Integer page = params.getPage();
        request.source().from((page-1)*size);
        request.source().size(size);
        //处理排序 （默认、评价score、价格price）
        String sortBy = params.getSortBy();
        if (!"default".equals(sortBy)){
            request.source().sort(sortBy);
        }
        //按距离远近排序(只是对结果处理)
        String location = params.getLocation();
        if (StrUtil.isNotBlank(location)){
            request.source().sort(SortBuilders.geoDistanceSort("location",new GeoPoint(location)).order(SortOrder.ASC).unit(DistanceUnit.KILOMETERS));
        }
        //结果转化
        try {
            SearchResponse response = client.search(request, RequestOptions.DEFAULT);
            //获取结果集合
            SearchHits hits = response.getHits();
            TotalHits totalHits = hits.getTotalHits();
            SearchHit[] searchHits = hits.getHits();
            List<HotelDoc> hotelDocs = new ArrayList<>();
            for (SearchHit searchHit : searchHits) {
                //每个对象的键值对
                Map<String, Object> sourceAsMap = searchHit.getSourceAsMap();
                HotelDoc hotelDoc = new HotelDoc();
                BeanUtil.fillBeanWithMap(sourceAsMap,hotelDoc ,true);
                //获取酒店距离
                Object[] sortValues = searchHit.getSortValues();
                if (sortValues.length>0){
                    hotelDoc.setDistance(sortValues[0]);
                }
                hotelDocs.add(hotelDoc);
            }
            pageResult.setHotels(hotelDocs);
            pageResult.setTotal(totalHits.value);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return pageResult;
    }

    @Override
    public boolean saveOrUpdate(HotelDoc hotelDoc) {
        //更新酒店信息
        IndexRequest indexRequest = new IndexRequest("hotel");
        indexRequest.id(hotelDoc.getId().toString()).source(JSON.toJSONString(hotelDoc), XContentType.JSON);
        try {
            IndexResponse indexResponse = client.index(indexRequest, RequestOptions.DEFAULT);
            System.out.println("执行结果:"+indexResponse.getResult());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean delete(String id) {
        DeleteRequest deleteRequest = new DeleteRequest("hotel");
        deleteRequest.id(id);
        try {
            DeleteResponse response = client.delete(deleteRequest, RequestOptions.DEFAULT);
            System.out.println("执行结果:"+response.getResult());
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * 构建查询条件
     * @param params 查询条件
     * @param request es查询
     */
    private void buildBasicQuery(HotelParam params, SearchRequest request) {
        //复合查询
        BoolQueryBuilder boolQuery = QueryBuilders.boolQuery();
        //获取输入框的值
        String key = params.getKey();
        //分词参与算分、所以要用must连接
        if (StrUtil.isNotBlank(key)){
            boolQuery.must(QueryBuilders.matchQuery("all",key));
        }else {
            boolQuery.must(QueryBuilders.matchAllQuery());
        }
        //条件查询筛选
        String city = params.getCity();
        if (StrUtil.isNotBlank(city)){
            boolQuery.filter(QueryBuilders.termQuery("city",city));
        }
        String starName = params.getStarName();
        if (StrUtil.isNotBlank(starName)){
            boolQuery.filter(QueryBuilders.termQuery("starName",starName));
        }
        String brand = params.getBrand();
        if (StrUtil.isNotBlank(brand)){
            boolQuery.filter(QueryBuilders.termQuery("brand",brand));
        }
        //价格区间 must_not
        Double maxPrice = params.getMaxPrice();
        Double minPrice = params.getMinPrice();
        if (maxPrice!=null&&minPrice!=null){
            //价格区间
            boolQuery.filter(QueryBuilders.rangeQuery("price").gte(minPrice).lte(maxPrice));
        }
        //添加算分函数
        /**
         * "query":{
         *     "function_score":{
         *         "query":{
         *             "bool":{.....}
         *         },
         *         "functions":[
         *         {
         *             "filter":{
         *                 "term":{
         *                     "isAD":true
         *                 }
         *             },
         *             "weight":10
         *         }
         *         ],
         *         "boost_mode":"multiply"
         *     }
         * }
         */
        FunctionScoreQueryBuilder functionScoreQuery =  QueryBuilders.functionScoreQuery(
                //复合查询，计算相关分数
                boolQuery,
                // function score的数组
                new FunctionScoreQueryBuilder.FilterFunctionBuilder[]{
                // 其中的一个function score 元素
                new FunctionScoreQueryBuilder.FilterFunctionBuilder(
                        // 过滤条件
                        QueryBuilders.termQuery("isAD", true),
                        // 算分函数
                        ScoreFunctionBuilders.weightFactorFunction(10)
                )
        });
        request.source().query(functionScoreQuery);
    }
}
