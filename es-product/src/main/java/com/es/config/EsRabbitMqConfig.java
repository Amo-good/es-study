package com.es.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 交换机 es-product
 *  队列 product-save
 *  队列 product-delete
 * @author amo
 */
@Configuration
public class EsRabbitMqConfig {
    //交换机
    private static final String EXCHANGE="es-product";
    //普通队列
    private static final String QUEUE_SAVE="product-save";
    private static final String QUEUE_DELETE="product-delete";

    //酒店管理相关的队列
    //交换机
    private static final String EXCHANGE_HOTEL="es.hotel";
    //普通队列
    private static final String HOTEL_SAVE="hotel.saveOrUpdate";
    private static final String HOTEL_DELETE="hotel-delete";

    //声明交换机
    @Bean("es-product")
    public DirectExchange esProductExchange(){
        return new DirectExchange(EXCHANGE,true,false);
    }

    //声明交换机
    @Bean("es.hotel")
    public DirectExchange esHotelExchange(){
        return new DirectExchange(EXCHANGE_HOTEL,true,false);
    }
    //声明队列
    @Bean
    public Queue esSaveQueue() {
        return new Queue(QUEUE_SAVE, true);
    }

    @Bean
    public Queue esDeleteQueue() {
        return new Queue(QUEUE_DELETE, true);
    }

    @Bean
    public Queue hotelSaveQueue() {
        return new Queue(HOTEL_SAVE, true);
    }

    @Bean
    public Queue hotelDeleteQueue() {
        return new Queue(HOTEL_DELETE, true);
    }

    //绑定队列和交换机
    @Bean
    public Binding esSaveBindingExchange(@Qualifier("esSaveQueue") Queue esSaveQueue, @Qualifier("es-product") DirectExchange esProductExchange){
        return BindingBuilder.bind(esSaveQueue).to(esProductExchange).with("save");
    }

    @Bean
    public Binding esDeleteBindingExchange(@Qualifier("esDeleteQueue") Queue esDeleteQueue, @Qualifier("es-product") DirectExchange esProductExchange){
        return BindingBuilder.bind(esDeleteQueue).to(esProductExchange).with("delete");
    }

    @Bean
    public Binding hotelSaveBindingExchange(@Qualifier("hotelSaveQueue") Queue esSaveQueue, @Qualifier("es.hotel") DirectExchange esProductExchange){
        return BindingBuilder.bind(esSaveQueue).to(esProductExchange).with("save");
    }

    @Bean
    public Binding hotelUpdateBindingExchange(
            @Qualifier("hotelSaveQueue") Queue hotelSaveQueue,
            @Qualifier("es.hotel") DirectExchange esProductExchange) {

        // 绑定 update 路由键
        return BindingBuilder.bind(hotelSaveQueue)
                .to(esProductExchange)
                .with("update");
    }

    @Bean
    public Binding hotelDeleteBindingExchange(@Qualifier("hotelDeleteQueue") Queue esDeleteQueue, @Qualifier("es.hotel") DirectExchange esProductExchange){
        return BindingBuilder.bind(esDeleteQueue).to(esProductExchange).with("delete");
    }

}
