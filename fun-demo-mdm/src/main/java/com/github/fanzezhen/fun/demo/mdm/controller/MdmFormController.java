package com.github.fanzezhen.fun.demo.mdm.controller;

import com.github.fanzezhen.fun.demo.mdm.bo.MdmFormBO;
import com.github.fanzezhen.fun.demo.mdm.condition.MdmFormPageCondition;
import com.github.fanzezhen.fun.demo.mdm.entity.MdmFormEntity;
import com.github.fanzezhen.fun.demo.mdm.request.MdmFormCreateRequest;
import com.github.fanzezhen.fun.demo.mdm.request.MdmFormPageRequest;
import com.github.fanzezhen.fun.demo.mdm.request.MdmFormUpdateRequest;
import com.github.fanzezhen.fun.demo.mdm.service.IMdmFormService;
import com.github.fanzezhen.fun.framework.core.model.dto.PageDTO;
import com.github.fanzezhen.fun.framework.core.model.util.MapperFacadeUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

/**
 * 动态表单 Controller
 * 演示框架提供的能力:
 * - Controller 直接返回 BO，框架自动封装为 Result<T> 格式（ResponseBodyWrapper）
 * - 使用 @Valid 进行参数校验，校验失败自动返回标准错误格式（GlobalExceptionHandler）
 * - 抛出 BusinessException，自动返回标准错误格式
 * - SpringDoc 自动生成接口文档
 * - 使用框架统一分页模型：Controller 接收 Request，转换为 Condition 传递给 Service
 *
 * @author Claude
 * @since 4.0.6
 */
@RestController
@RequestMapping("/mdm/form")
@Tag(name = "动态表单管理", description = "动态表单 CRUD 接口")
public class MdmFormController {

    @Resource
    private IMdmFormService mdmFormService;

    @PostMapping
    @Operation(summary = "创建表单", description = "创建一个新的动态表单")
    public MdmFormBO create(@Valid @RequestBody MdmFormCreateRequest request) {
        MdmFormEntity entity = MapperFacadeUtil.map(request, MdmFormEntity.class);
        // Controller 直接返回 BO，框架自动封装为 Result<MdmFormBO>
        return mdmFormService.create(entity);
    }

    @PutMapping
    @Operation(summary = "更新表单", description = "更新已有表单信息")
    public MdmFormBO update(@Valid @RequestBody MdmFormUpdateRequest request) {
        MdmFormEntity entity = MapperFacadeUtil.map(request, MdmFormEntity.class);
        return mdmFormService.update(entity);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询表单", description = "根据ID查询表单详情")
    public MdmFormBO getById(@Parameter(description = "表单ID") @PathVariable Long id) {
        return mdmFormService.getById(id);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除表单", description = "根据ID删除表单（逻辑删除）")
    public Boolean deleteById(@Parameter(description = "表单ID") @PathVariable Long id) {
        return mdmFormService.deleteById(id);
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询表单", description = "分页查询表单列表，支持按名称和发布状态过滤")
    public PageDTO<MdmFormBO> page(@Valid MdmFormPageRequest request) {
        // Controller 负责 Request → Condition 转换
        MdmFormPageCondition condition = new MdmFormPageCondition(request.getCurrent(), request.getSize());
        condition.setName(request.getName());
        condition.setReleased(request.getReleased());

        // Service 层使用 Condition
        return mdmFormService.page(condition);
    }

    @PutMapping("/{id}/release")
    @Operation(summary = "发布表单", description = "发布表单，使其可供填写")
    public Boolean release(@Parameter(description = "表单ID") @PathVariable Long id) {
        return mdmFormService.release(id);
    }
}
