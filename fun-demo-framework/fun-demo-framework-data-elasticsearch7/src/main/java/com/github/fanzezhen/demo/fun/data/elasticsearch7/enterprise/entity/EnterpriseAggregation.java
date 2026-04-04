package com.github.fanzezhen.demo.fun.data.elasticsearch7.enterprise.entity;

import com.github.fanzezhen.fun.framework.core.data.annotation.Entity;
import com.github.fanzezhen.fun.framework.core.model.bucket.Bucket;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.annotation.Aggregation;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.annotation.AggregationField;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.constant.AggregationFieldEnum;
import lombok.Data;

import java.util.List;

@Data
@Aggregation("business_status")
@Entity(table = "enterprise_new")
public class EnterpriseAggregation {

    @AggregationField(AggregationFieldEnum.BUCKETS)
    private List<Bucket> bucketList;

}
