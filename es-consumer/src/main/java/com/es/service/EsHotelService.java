package com.es.service;

import es.entity.dto.HotelDoc;
import es.entity.param.HotelParam;
import es.entity.vo.PageResult;

import java.util.List;
import java.util.Map;

/**
 * @author amo
 */
public interface EsHotelService {

    PageResult list(HotelParam param);

    boolean saveOrUpdate(HotelDoc hotelDoc);

    boolean delete(String id);

    Map<String, List<String>> filters(HotelParam param);
}
