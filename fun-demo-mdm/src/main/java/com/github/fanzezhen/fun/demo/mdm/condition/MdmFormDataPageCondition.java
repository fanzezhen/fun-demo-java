package com.github.fanzezhen.fun.demo.mdm.condition;

import com.github.fanzezhen.fun.framework.core.model.condition.PageCondition;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 表单数据分页查询条件
 * Service/DAO 层传递分页查询条件
 *
 * @author Claude
 * @since 4.0.6
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class MdmFormDataPageCondition extends PageCondition {

    /**
     * 表单ID
     */
    private Long formId;

    public MdmFormDataPageCondition() {
        super(1, 10); // 默认第1页，每页10条
    }

    public MdmFormDataPageCondition(int current, int size) {
        super(current, size);
    }
}
