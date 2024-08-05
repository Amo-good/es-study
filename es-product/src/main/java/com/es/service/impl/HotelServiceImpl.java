package com.es.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.es.mapper.HotelMapper;
import com.es.service.HotelService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.entity.Hotel;
import es.entity.dto.HotelDoc;
import es.entity.vo.HotelResult;
import es.entity.vo.PageResult;
import es.entity.vo.ResultData;
import es.entity.vo.ReturnCodeEnum;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;


/**
 * 酒店数据管理增删改查
 * 对酒店数据的操作需要同步es
 * 通过mq发送
 * @author zxm
 */
@Service
public class HotelServiceImpl extends ServiceImpl<HotelMapper, Hotel> implements HotelService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public ResultData<String> updateHotel(Hotel hotel) {
        boolean flag = saveOrUpdate(hotel);
        if (!flag){
            return ResultData.fail(ReturnCodeEnum.RC500.getCode(), "更新酒店数据失败！");
        }
        //添加成功、推送消息到mq（将商品保存到es中）
        ObjectMapper objectMapper =new ObjectMapper();
        try {
            HotelDoc hotelDoc = new HotelDoc(hotel);
            String jsonMessage = objectMapper.writeValueAsString(hotelDoc);
            rabbitTemplate.convertAndSend("es.hotel","update",jsonMessage.getBytes(StandardCharsets.UTF_8));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResultData.fail(ReturnCodeEnum.RC500.getCode(),"推送商品信息到mq失败！");
        }
        return ResultData.success("更新成功");
    }

    @Override
    public ResultData<String> addHotel(Hotel hotel) {
        long id = generateId();
        hotel.setId(id);
        //添加酒店信息
        boolean save = save(hotel);
        if (!save){
            return ResultData.fail(ReturnCodeEnum.RC500.getCode(), "新增酒店数据失败！");
        }
        //推送mq
        //添加成功、推送消息到mq（将商品保存到es中）
        ObjectMapper objectMapper =new ObjectMapper();
        try {
            HotelDoc hotelDoc = new HotelDoc(hotel);
            String jsonMessage = objectMapper.writeValueAsString(hotelDoc);
            rabbitTemplate.convertAndSend("es.hotel","save",jsonMessage.getBytes(StandardCharsets.UTF_8));
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return ResultData.fail(ReturnCodeEnum.RC500.getCode(),"推送商品信息到mq失败！");
        }
        return ResultData.success("添加成功");
    }

    @Override
    public ResultData<String> deleteHotel(Long id) {
        boolean flag = removeById(id);
        if (!flag){
            return ResultData.fail(ReturnCodeEnum.RC500.getCode(), "删除酒店数据失败！");
        }
        rabbitTemplate.convertAndSend("es.hotel","delete",id.toString());
        return ResultData.success("删除成功");
    }

    @Override
    public HotelResult listHotel(int page, int size) {
        Page<Hotel> hotelPage = this.getBaseMapper().selectPage(new Page<>(page, size), new QueryWrapper<>());
        long total = hotelPage.getTotal();
        HotelResult result = new HotelResult();
        result.setTotal(total);
        result.setHotels(hotelPage.getRecords());
        return result;
    }

    private  long generateId() {
        // 获取当前时间戳（单位：毫秒）
        long timestamp = System.currentTimeMillis();
        // 添加一个随机数
        Random random = new Random();
        long randomSuffix = random.nextInt(1000); // 可以调整范围
        return timestamp * 1000 + randomSuffix; // 确保 ID 是长整型
    }

}
