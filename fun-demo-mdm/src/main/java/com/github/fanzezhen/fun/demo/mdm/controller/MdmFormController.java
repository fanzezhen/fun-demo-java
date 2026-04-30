package com.github.fanzezhen.fun.demo.mdm.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.fanzezhen.fun.demo.mdm.bo.MdmFormBO;
import com.github.fanzezhen.fun.demo.mdm.entity.MdmForm;
import com.github.fanzezhen.fun.demo.mdm.request.MdmFormCreateRequest;
import com.github.fanzezhen.fun.demo.mdm.request.MdmFormUpdateRequest;
import com.github.fanzezhen.fun.demo.mdm.service.IMdmFormService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 动态表单 Controller
 * 演示框架提供的能力:
 * - Controller 直接返回 BO，框架自动封装为 Result<T> 格式（ResponseBodyWrapper）
 * - 使用 @Valid 进行参数校验，校验失败自动返回标准错误格式（GlobalExceptionHandler）
 * - 抛出 BusinessException，自动返回标准错误格式
 * - SpringDoc 自动生成接口文档
 *
 * @author Claude
 * @since 2026-04-30
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
        MdmForm entity = new MdmForm();
        BeanUtils.copyProperties(request, entity);
        // Controller 直接返回 BO，框架自动封装为 Result<MdmFormBO>
        return mdmFormService.create(entity);
    }

    @PutMapping
    @Operation(summary = "更新表单", description = "更新已有表单信息")
    public MdmFormBO update(@Valid @RequestBody MdmFormUpdateRequest request) {
        MdmForm entity = new MdmForm();
        BeanUtils.copyProperties(request, entity);
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
    public Page<MdmFormBO> page(
            @Parameter(description = "当前页", example = "1") @RequestParam(defaultValue = "1") Long current,
            @Parameter(description = "每页大小", example = "10") @RequestParam(defaultValue = "10") Long size,
            @Parameter(description = "表单名称（模糊查询）") @RequestParam(required = false) String name,
            @Parameter(description = "是否已发布") @RequestParam(required = false) Boolean released
    ) {
        Page<MdmForm> page = new Page<>(current, size);
        return mdmFormService.page(page, name, released);
    }

    @PutMapping("/{id}/release")
    @Operation(summary = "发布表单", description = "发布表单，使其可供填写")
    public Boolean release(@Parameter(description = "表单ID") @PathVariable Long id) {
        return mdmFormService.release(id);
    }
}
