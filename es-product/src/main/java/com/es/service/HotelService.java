package com.es.service;


import com.baomidou.mybatisplus.extension.service.IService;
import es.entity.Hotel;
import es.entity.vo.HotelResult;
import es.entity.vo.ResultData;

public interface HotelService extends IService<Hotel> {
    ResultData<String> updateHotel(Hotel hotel);

    ResultData<String> addHotel(Hotel hotel);

    ResultData<String> deleteHotel(Long id);

    HotelResult listHotel(int page, int size);
}
