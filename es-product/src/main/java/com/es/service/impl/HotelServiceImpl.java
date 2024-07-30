package com.es.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.es.mapper.HotelMapper;
import com.es.service.HotelService;
import es.entity.Hotel;
import org.springframework.stereotype.Service;

@Service
public class HotelServiceImpl extends ServiceImpl<HotelMapper, Hotel> implements HotelService {

}
