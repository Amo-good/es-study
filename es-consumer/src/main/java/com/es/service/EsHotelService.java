package com.es.service;

import es.entity.dto.HotelDoc;
import es.entity.param.HotelParam;
import es.entity.vo.PageResult;

/**
 * @author amo
 */
public interface EsHotelService {

    PageResult list(HotelParam param);

    boolean saveOrUpdate(HotelDoc hotelDoc);

    boolean delete(String id);
}
