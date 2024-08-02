package com.es.listen;

import com.es.service.EsHotelService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.sun.istack.internal.NotNull;
import es.entity.dto.HotelDoc;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;

/**
 * @author amo
 */
@Component
@Slf4j
public class ReceiverHotelListener {

    @Resource
    private EsHotelService esHotelService;

    //监听来自hotel的消息
    @RabbitListener(queues = "hotel.saveOrUpdate")
    public void saveHotelListener(@NotNull Message message, Channel channel) throws JsonProcessingException {
        String jsonMessage = new String(message.getBody(), StandardCharsets.UTF_8);
        // 将 JSON 字符串转换为对象
        ObjectMapper objectMapper = new ObjectMapper();
        HotelDoc hotelDoc = objectMapper.readValue(jsonMessage, HotelDoc.class);
        log.info("mq接收到酒店添加信息--------》{}",hotelDoc);
        //保存商品信息到es中
        boolean flag = esHotelService.saveOrUpdate(hotelDoc);
        log.info("mq处理酒店保存信息结果--------》{}",flag);
    }
}
