package com.es.controller;

import com.es.service.HotelService;
import es.entity.Hotel;
import es.entity.dto.HotelDoc;
import es.entity.vo.HotelResult;
import es.entity.vo.ResultData;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/hotel")
public class HotelProductController {

    @Resource
    private HotelService hotelService;

    @GetMapping("/listDoc")
    public List<HotelDoc> listDoc(){
        List<Hotel> hotelList = hotelService.list();
        return hotelList.stream().map(HotelDoc::new).collect(Collectors.toList());
    }

    @GetMapping("/list")
    public HotelResult listHotel(int page, int size){
        return hotelService.listHotel(page,size);
    }

    /**
     * 修改酒店信息
     */
    @PutMapping
    public ResultData<String> updateHotel(@RequestBody Hotel hotel){
        return hotelService.updateHotel(hotel);
    }

    /**
     * 添加酒店信息
     */
    @PostMapping
    public ResultData<String> addHotel(@RequestBody Hotel hotel){
        return hotelService.addHotel(hotel);
    }


    @DeleteMapping("/{id}")
    public ResultData<String> deleteHotel(@PathVariable("id") Long id){
        return hotelService.deleteHotel(id);
    }


}
