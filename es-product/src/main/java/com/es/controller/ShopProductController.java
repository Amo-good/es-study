package com.es.controller;

import com.es.service.ShopService;
import es.entity.Shop;
import es.entity.vo.ResultData;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author amo
 */
@RestController
@RequestMapping("/product/shop/")
public class ShopProductController {

    @Resource
    private ShopService shopService;


    /**
     * 添加商品
     * 将商品插入数据库-shop
     * 推送mq消息到队列
     * @param shop 商品
     * @return 操作成功与否标识
     */
    @PostMapping("/save")
    public ResultData<String> save(@RequestBody Shop shop){
        return shopService.saveEsShop(shop);
    }


}
