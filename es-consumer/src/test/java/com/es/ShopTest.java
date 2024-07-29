package com.es;

import com.es.service.ShopService;
import es.entity.Shop;
import es.entity.vo.ResultData;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class ShopTest {

    @Resource
    private ShopService shopService;


    @Test
    public void test(){
        ResultData<Shop> byId = shopService.getById(6L);
        System.out.println(byId.getData());
    }
}
