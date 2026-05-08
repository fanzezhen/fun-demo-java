package com.github.fanzezhen.fun.demo.mdm.controller;

import com.github.fanzezhen.fun.demo.mdm.bo.MdmFormDataBO;
import com.github.fanzezhen.fun.demo.mdm.bo.MdmFormItemDataBO;
import com.github.fanzezhen.fun.demo.mdm.condition.MdmFormDataPageCondition;
import com.github.fanzezhen.fun.demo.mdm.request.MdmFormDataPageRequest;
import com.github.fanzezhen.fun.demo.mdm.request.MdmFormDataSubmitRequest;
import com.github.fanzezhen.fun.demo.mdm.service.IMdmFormDataService;
import com.github.fanzezhen.fun.framework.core.model.dto.PageDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 动态表单数据 Controller
 * 演示表单数据提交和查询功能
 * 使用框架统一分页模型：Controller 接收 Request，转换为 Condition 传递给 Service
 *
 * @author Claude
 * @since 4.0.6
 */
@RestController
@RequestMapping("/mdm/form-data")
@Tag(name = "动态表单数据管理", description = "表单数据提交和查询接口")
public class MdmFormDataController {

    @Resource
    private IMdmFormDataService mdmFormDataService;

    @PostMapping("/submit")
    @Operation(summary = "提交表单数据", description = "提交表单填写的数据")
    public MdmFormDataBO submit(@Valid @RequestBody MdmFormDataSubmitRequest request) {
        // 转换 Request 为 BO
        List<MdmFormItemDataBO> itemDataList = request.getItemDataList().stream()
                .map(itemRequest -> {
                    MdmFormItemDataBO bo = new MdmFormItemDataBO();
                    BeanUtils.copyProperties(itemRequest, bo);
                    return bo;
                })
                .toList();

        return mdmFormDataService.submit(request.getFormId(), request.getFormDefId(), itemDataList);
    }

    @GetMapping("/{id}")
    @Operation(summary = "查询表单数据", description = "根据ID查询表单数据详情（包含所有字段值）")
    public MdmFormDataBO getById(@Parameter(description = "表单数据ID") @PathVariable Long id) {
        return mdmFormDataService.getById(id);
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询表单数据", description = "根据表单ID分页查询所有提交的数据")
    public PageDTO<MdmFormDataBO> page(@Valid MdmFormDataPageRequest request) {
        // Controller 负责 Request → Condition 转换
        MdmFormDataPageCondition condition = new MdmFormDataPageCondition(request.getCurrent(), request.getSize());
        condition.setFormId(request.getFormId());

        // Service 层使用 Condition
        return mdmFormDataService.page(condition);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "删除表单数据", description = "根据ID删除表单数据（逻辑删除）")
    public Boolean deleteById(@Parameter(description = "表单数据ID") @PathVariable Long id) {
        return mdmFormDataService.deleteById(id);
    }
}
