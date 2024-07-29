package com.es.service;

import com.baomidou.mybatisplus.extension.service.IService;
import es.entity.Shop;
import es.entity.vo.ResultData;


public interface ShopService extends IService<Shop> {
    ResultData<String> saveEsShop(Shop shop);
}
