package com.github.fanzezhen.demo.fun.data.elasticsearch7.enterprise.entity;

import com.github.fanzezhen.fun.framework.core.model.annotation.Entity;
import com.github.fanzezhen.fun.framework.core.model.bucket.CountBucket;
import com.github.fanzezhen.fun.framework.core.model.constant.StrConstant;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.annotation.Aggregation;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.annotation.AggregationField;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.annotation.BucketField;
import com.github.fanzezhen.fun.framework.data.elasticsearch.base.constant.AggregationFieldEnum;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Entity(table = "enterprise_new")
@Aggregation("group_count_status")
public class EnterpriseHitsBucketAggregation {

    @AggregationField(AggregationFieldEnum.BUCKETS)
    private List<EnterpriseHitsBucket> bucketList;

    @Data
    @Accessors(chain = true)
    public static class EnterpriseHitsBucket extends CountBucket {
        @BucketField(aggregationName = StrConstant.RECORDS)
        @AggregationField(AggregationFieldEnum.HITS)
        private List<EnterpriseBO> hitList;

        @Data
        @Accessors(chain = true)
        public static class EnterpriseBO {
            private String name;
        }
    }
}
