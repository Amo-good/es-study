package com.es.controller;

import com.es.service.EsHotelService;
import es.entity.param.HotelParam;
import es.entity.vo.PageResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/hotel")
public class EsHotelController {

    @Resource
    private EsHotelService esHotelService;

    @PostMapping("/list")
    public PageResult list(@RequestBody HotelParam param){
        return esHotelService.list(param);
    }

}
