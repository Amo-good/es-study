package com.es;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author zxm
 */
@SpringBootApplication
@MapperScan(value = "com.es.mapper")
public class EsProductApplication {
    public static void main(String[] args) {
        SpringApplication.run(EsProductApplication.class,args);
    }
}
