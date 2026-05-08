package com.github.fanzezhen.fun.demo.mdm.request;

import com.github.fanzezhen.fun.framework.core.model.request.PageRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 表单分页查询请求
 * Controller 层接收前端分页参数
 *
 * @author Claude
 * @since 4.0.6
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@Schema(description = "表单分页查询请求")
public class MdmFormPageRequest extends PageRequest {

    @Schema(description = "表单名称（模糊查询）")
    private String name;

    @Schema(description = "是否已发布")
    private Boolean released;

    public MdmFormPageRequest() {
        super(1, 10); // 默认第1页，每页10条
    }

    public MdmFormPageRequest(Integer current, Integer size) {
        super(current, size);
    }
}
