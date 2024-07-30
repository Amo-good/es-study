package com.es.controller;

import com.es.service.HotelService;
import es.entity.Hotel;
import es.entity.dto.HotelDoc;
import es.entity.vo.ResultData;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/product/hotel")
public class HotelProductController {

    @Resource
    private HotelService hotelService;

    @GetMapping("/listDoc")
    public List<HotelDoc> listDoc(){
        List<Hotel> hotelList = hotelService.list();
        return hotelList.stream().map(HotelDoc::new).collect(Collectors.toList());
    }
}
