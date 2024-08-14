package com.es.controller;

import com.es.service.EsHotelService;
import es.entity.param.HotelParam;
import es.entity.vo.PageResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hotel")
public class EsHotelController {

    @Resource
    private EsHotelService esHotelService;

    @PostMapping("/list")
    public PageResult list(@RequestBody HotelParam param){
        return esHotelService.list(param);
    }


    /**
     * 酒店过滤条件筛选
     */
    @PostMapping("/filters")
    public Map<String, List<String>> filters(@RequestBody HotelParam param){
        return esHotelService.filters(param);
    }
}
