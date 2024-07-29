package com.es.repository;

import es.entity.Shop;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * @author zxm
 */
@Repository
public interface ShopRepository extends ElasticsearchRepository<Shop, Long> {

}
