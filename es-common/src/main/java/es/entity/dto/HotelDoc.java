package es.entity.dto;

import es.entity.Hotel;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Data
@NoArgsConstructor
public class HotelDoc {
    private Long id;
    private String name;
    private String address;
    private Integer price;
    private Integer score;
    private String brand;
    private String city;
    private String starName;
    private String business;
    private String location;
    private String pic;
    // 排序时的 距离值
    private Object distance;
    /**
     * 添加广告标识
     POST /hotel/_update/id
     {
         "doc": {
             "isAD": true
         }
     }

     */
    private Boolean isAD;
    //自动补全的词条
    private List<String> suggestion;

    public HotelDoc(Hotel hotel) {
        this.id = hotel.getId();
        this.name = hotel.getName();
        this.address = hotel.getAddress();
        this.price = hotel.getPrice();
        this.score = hotel.getScore();
        this.brand = hotel.getBrand();
        this.city = hotel.getCity();
        this.starName = hotel.getStarName();
        this.business = hotel.getBusiness();
        this.location = hotel.getLatitude() + ", " + hotel.getLongitude();
        this.pic = hotel.getPic();
        //品牌、商圈（商圈可能由/组成"business" : "三里屯/工体/东直门地区"）
        if (this.business.contains("/")){
            String[] arr = business.split("/");
            this.suggestion = new ArrayList<>();
            this.suggestion.add(this.brand);
            Collections.addAll(this.suggestion,arr);
        }else{
            this.suggestion = Arrays.asList(this.brand,this.business);
        }
    }
}
