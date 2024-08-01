package com.es.service;

import es.entity.param.HotelParam;
import es.entity.vo.PageResult;

/**
 * @author amo
 */
public interface EsHotelService {

    PageResult list(HotelParam param);

    PageResult filters(HotelParam param);
}
