package com.es.listen;

import com.es.service.ShopService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.sun.istack.internal.NotNull;
import es.entity.Shop;
import es.entity.vo.ResultData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

/**
 * @author zxm
 */
@Component
@Slf4j
public class EsConsumerListener {

    @Resource
    private ShopService shopService;


    @RabbitListener(queues = "product-save")
    public void saveListener(@NotNull Message message, Channel channel) throws JsonProcessingException {
        String jsonMessage = new String(message.getBody(), StandardCharsets.UTF_8);
        // 将 JSON 字符串转换为对象
        ObjectMapper objectMapper = new ObjectMapper();
        Shop shop = objectMapper.readValue(jsonMessage, Shop.class);
        log.info("mq接收到商品添加信息--------》{}",shop);
        //保存商品信息到es中
        ResultData<Shop> resultData = shopService.save(shop);
        log.info("添加商品信息到es:{}",resultData.getData());
    }
}
