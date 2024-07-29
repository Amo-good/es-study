package es.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author amo
 */
@Data
//@Document 文档对象 （索引信息、文档类型 ）
@Document(indexName = "shop")
public class Shop implements Serializable {

    @Id
    //主键自增
    @TableId(type = IdType.AUTO)
    //@Field 每个文档的字段配置（类型、是否分词、是否存储、分词器 ）
    @Field(store = true,index = false,type = FieldType.Integer)
    private Long id;

    @Field(store = true, analyzer = "ik_smart",type = FieldType.Text)
    private String name;

    @Field(store = true,index = false,type = FieldType.Double)
    private BigDecimal price;

    @Field(store = true,index = false,type = FieldType.Integer)
    private Integer store;

    @Field(store = true, analyzer = "ik_smart",type = FieldType.Text)
    private String content;
}
