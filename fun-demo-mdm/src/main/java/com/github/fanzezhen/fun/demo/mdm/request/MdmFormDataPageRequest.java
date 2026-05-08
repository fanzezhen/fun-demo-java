package com.github.fanzezhen.fun.demo.mdm.request;

import com.github.fanzezhen.fun.framework.core.model.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 表单数据分页查询请求
 * Controller 层接收前端分页参数
 *
 * @author Claude
 * @since 4.0.6
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Schema(description = "表单数据分页查询请求")
public class MdmFormDataPageRequest extends PageRequest {

    @Schema(description = "表单ID")
    private Long formId;

    public MdmFormDataPageRequest() {
        super(1, 10); // 默认第1页，每页10条
    }

    public MdmFormDataPageRequest(Integer current, Integer size) {
        super(current, size);
    }
}
