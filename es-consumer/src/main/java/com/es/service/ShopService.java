package com.es.service;

import com.es.repository.ShopRepository;
import es.entity.Shop;
import es.entity.vo.ResultData;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Optional;

/**
 * @author zxm
 */
@Service
public class ShopService {

    @Resource
    private ShopRepository shopRepository;

    public ResultData<Shop> save(Shop shop){
        Shop save = shopRepository.save(shop);
        return ResultData.success(save);
    }

    public ResultData<Shop> getById(Long id){
        Optional<Shop> optional = shopRepository.findById(id);
        return ResultData.success(optional.get());
    }
}
