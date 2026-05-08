package com.github.fanzezhen.demo.fun.data.elasticsearch7.enterprise.entity;

import com.github.fanzezhen.fun.framework.core.data.annotation.Entity;
import com.github.fanzezhen.fun.framework.core.model.bucket.RangeCountBucket;
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
    private List<RangeCountBucket> a;


    /**
     */
    @Aggregation("B")
    @AggregationField(AggregationFieldEnum.BUCKETS)
    private List<RangeCountBucket> b;


    /**
     */
    @Aggregation("C")
    @AggregationField(AggregationFieldEnum.BUCKETS)
    private List<RangeCountBucket> c;


    /**
     */
    @Aggregation("D")
    @AggregationField(AggregationFieldEnum.BUCKETS)
    private List<RangeCountBucket> d;


    /**
     */
    @Aggregation("E")
    @AggregationField(AggregationFieldEnum.BUCKETS)
    private List<RangeCountBucket> e;

    /**
     */
    @Aggregation("allValue")
    @AggregationField(AggregationFieldEnum.VALUE)
    private Double allValue;

}
