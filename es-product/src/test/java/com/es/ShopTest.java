package com.es;

import com.es.service.ShopService;
import es.entity.Shop;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest
public class ShopTest {
    @Resource
    private ShopService service;

    @Test
    public void test(){
        Shop byId = service.getById(1);
        System.out.println(byId);
    }
}
