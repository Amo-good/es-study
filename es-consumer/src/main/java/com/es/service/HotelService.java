package com.es.service;

import es.entity.dto.HotelDoc;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author zxm
 */
@Service
public class HotelService {
    @Resource
    private RestTemplate restTemplate;

    //获取酒店的数据
    public List<HotelDoc> getHotelList(){
        String url="http://localhost:8888/hotel/listDoc";
        ResponseEntity<List<HotelDoc>> responseEntity = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<List<HotelDoc>>() {});
        return responseEntity.getBody();
    }
}
