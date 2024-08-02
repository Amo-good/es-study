package es.entity.vo;

import es.entity.Hotel;
import lombok.Data;

import java.util.List;

@Data
public class HotelResult {
    private Long total;
    private List<Hotel> hotels;

    public HotelResult() {
    }

    public HotelResult(Long total, List<Hotel> hotels) {
        this.total = total;
        this.hotels = hotels;
    }
}
