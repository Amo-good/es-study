package com.es.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.StrUtil;
import com.es.service.EsHotelService;
import es.entity.dto.HotelDoc;
import es.entity.param.HotelParam;
import es.entity.vo.PageResult;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author amo
 */
@Service
public class EsHotelServiceImpl implements EsHotelService {

    @Resource
    private RestHighLevelClient client;

    @Override
    public PageResult list(HotelParam param) {
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
       buildBasicQuery(param,request);
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
    public PageResult filters(HotelParam params) {
        return null;
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
        if (maxPrice!=null){
            //不大于
            boolQuery.mustNot(QueryBuilders.rangeQuery("price").gt(maxPrice));
        }
        Double minPrice = params.getMinPrice();
        if (minPrice!=null){
            //不大于
            boolQuery.mustNot(QueryBuilders.rangeQuery("price").lt(minPrice));
        }
        request.source().query(boolQuery);
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
    }
}
