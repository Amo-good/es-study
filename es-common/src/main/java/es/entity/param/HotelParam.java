package es.entity.param;

import lombok.Data;

/**
 * 啊莫旅游查询参数
 * @author amo
 */
@Data
public class HotelParam {
    private String key;
    private String city;
    private String brand;
    private String starName;
    private Double maxPrice;
    private Double minPrice;
    private Integer page;
    private Integer size;
    private String sortBy;
}
