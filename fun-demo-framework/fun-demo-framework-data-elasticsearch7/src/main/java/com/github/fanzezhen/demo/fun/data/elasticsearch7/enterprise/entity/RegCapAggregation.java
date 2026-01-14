package com.github.fanzezhen.demo.fun.data.elasticsearch7.enterprise.entity;

import com.github.fanzezhen.fun.framework.core.data.annotation.Entity;
import com.github.fanzezhen.fun.framework.core.model.bucket.RangeBucket;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.annotation.Aggregation;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.annotation.AggregationField;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.annotation.Aggregations;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.constant.AggregationFieldEnum;
import lombok.Data;

import java.util.List;

/**
 * 聚合测试
 */
@Data
@Aggregations
@Entity(table = "enterprise_new")
public class RegCapAggregation {


    /**
     */
    @Aggregation("A")
    @AggregationField(AggregationFieldEnum.BUCKETS)
    private List<RangeBucket> a;


    /**
     */
    @Aggregation("B")
    @AggregationField(AggregationFieldEnum.BUCKETS)
    private List<RangeBucket> b;


    /**
     */
    @Aggregation("C")
    @AggregationField(AggregationFieldEnum.BUCKETS)
    private List<RangeBucket> c;


    /**
     */
    @Aggregation("D")
    @AggregationField(AggregationFieldEnum.BUCKETS)
    private List<RangeBucket> d;


    /**
     */
    @Aggregation("E")
    @AggregationField(AggregationFieldEnum.BUCKETS)
    private List<RangeBucket> e;

    /**
     */
    @Aggregation("allValue")
    @AggregationField(AggregationFieldEnum.VALUE)
    private Double allValue;

}
