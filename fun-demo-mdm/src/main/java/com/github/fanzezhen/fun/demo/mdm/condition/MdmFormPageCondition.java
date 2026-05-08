package com.github.fanzezhen.fun.demo.mdm.condition;

import com.github.fanzezhen.fun.framework.core.model.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 表单分页查询条件
 * Service/DAO 层传递分页查询条件
 *
 * @author Claude
 * @since 4.0.6
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class MdmFormPageCondition extends PageCondition {

    /**
     * 表单名称（模糊查询）
     */
    private String name;

    /**
     * 是否已发布
     */
    private Boolean released;

    public MdmFormPageCondition() {
        super(1, 10); // 默认第1页，每页10条
    }

    public MdmFormPageCondition(int current, int size) {
        super(current, size);
    }
}
