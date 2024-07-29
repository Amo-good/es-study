package com.es.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.es.mapper.ShopMapper;
import com.es.service.ShopService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.entity.Shop;
import es.entity.vo.ResultData;
import es.entity.vo.ReturnCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;


/**
 * @author HY
 */
@Service
@Slf4j
public class ShopServiceImpl extends ServiceImpl<ShopMapper, Shop> implements ShopService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public ResultData<String> saveEsShop(Shop shop) {
        log.info("接收到商品信息---------》"+shop);
        boolean save = saveOrUpdate(shop);
        if (!save){
            return ResultData.fail(ReturnCodeEnum.RC500.getCode(),"添加商品信息失败！");
        }
        //添加成功、推送消息到mq（将商品保存到es中）
        ObjectMapper objectMapper =new ObjectMapper();
        try {
            String jsonMessage = objectMapper.writeValueAsString(shop);
            rabbitTemplate.convertAndSend("es-product","save",jsonMessage.getBytes(StandardCharsets.UTF_8));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResultData.fail(ReturnCodeEnum.RC500.getCode(),"推送商品信息到mq失败！");
        }
        return ResultData.success("添加商品信息成功！");
    }
}
