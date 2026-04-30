package com.github.fanzezhen.fun.demo.mdm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.fanzezhen.fun.demo.mdm.MdmApplication;
import com.github.fanzezhen.fun.demo.mdm.bo.MdmFormBO;
import com.github.fanzezhen.fun.demo.mdm.bo.MdmFormDataBO;
import com.github.fanzezhen.fun.demo.mdm.bo.MdmFormItemDataBO;
import com.github.fanzezhen.fun.demo.mdm.entity.MdmForm;
import com.github.fanzezhen.fun.demo.mdm.entity.MdmFormData;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 动态表单数据服务测试
 * 演示一主多从结构的数据处理
 *
 * @author Claude
 * @since 2026-04-30
 */
@SpringBootTest(classes = MdmApplication.class)
class MdmFormDataServiceTest {

    @Resource
    private IMdmFormService mdmFormService;

    @Resource
    private IMdmFormDataService mdmFormDataService;

    private Long testFormId;
    private Long testFormDefId = 1L; // 假设表单定义ID为1

    @BeforeEach
    void setUp() {
        // 创建测试表单
        MdmForm form = new MdmForm();
        form.setName("用户反馈表");
        form.setRemark("收集用户反馈");
        form.setReleased(true);
        form.setOrderNum(1);
        MdmFormBO formBO = mdmFormService.create(form);
        testFormId = formBO.getId();
    }

    @Test
    void testSubmit() {
        // 1. 准备表单字段数据
        List<MdmFormItemDataBO> itemDataList = new ArrayList<>();

        // 字段1: 用户姓名
        MdmFormItemDataBO userName = new MdmFormItemDataBO();
        userName.setFormItemCode("user_name");
        userName.setSeq(-1); // 单值字段
        userName.setValue("张三");
        userName.setValueType("string");
        itemDataList.add(userName);

        // 字段2: 反馈内容
        MdmFormItemDataBO feedback = new MdmFormItemDataBO();
        feedback.setFormItemCode("feedback");
        feedback.setSeq(-1);
        feedback.setValue("产品很好用，希望增加批量导入功能");
        feedback.setValueType("string");
        itemDataList.add(feedback);

        // 字段3: 评分
        MdmFormItemDataBO rating = new MdmFormItemDataBO();
        rating.setFormItemCode("rating");
        rating.setSeq(-1);
        rating.setValue("5");
        rating.setValueType("number");
        itemDataList.add(rating);

        // 字段4: 标签（列表类型）
        for (int i = 0; i < 3; i++) {
            MdmFormItemDataBO tag = new MdmFormItemDataBO();
            tag.setFormItemCode("tags");
            tag.setSeq(i); // 列表从0开始
            tag.setValue("标签" + (i + 1));
            tag.setValueType("string");
            itemDataList.add(tag);
        }

        // 2. 提交表单数据
        MdmFormDataBO result = mdmFormDataService.submit(testFormId, testFormDefId, itemDataList);

        // 3. 验证结果
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(testFormId, result.getFormId());
        assertEquals(testFormDefId, result.getFormDefId());
        assertNotNull(result.getItemDataList());
        assertEquals(6, result.getItemDataList().size()); // 3个单值字段 + 3个列表项

        System.out.println("✅ 提交表单数据成功: " + result.getId());
        System.out.println("   字段数据数量: " + result.getItemDataList().size());
    }

    @Test
    void testGetById() {
        // 1. 先提交表单数据
        List<MdmFormItemDataBO> itemDataList = new ArrayList<>();
        MdmFormItemDataBO item = new MdmFormItemDataBO();
        item.setFormItemCode("test_field");
        item.setSeq(-1);
        item.setValue("测试值");
        item.setValueType("string");
        itemDataList.add(item);

        MdmFormDataBO submitted = mdmFormDataService.submit(testFormId, testFormDefId, itemDataList);

        // 2. 根据ID查询
        MdmFormDataBO found = mdmFormDataService.getById(submitted.getId());

        // 3. 验证结果
        assertNotNull(found);
        assertEquals(submitted.getId(), found.getId());
        assertNotNull(found.getItemDataList());
        assertEquals(1, found.getItemDataList().size());
        assertEquals("test_field", found.getItemDataList().get(0).getFormItemCode());
        assertEquals("测试值", found.getItemDataList().get(0).getValue());

        System.out.println("✅ 查询表单数据成功: " + found);
    }

    @Test
    void testPageByFormId() {
        // 1. 提交多条表单数据
        for (int i = 1; i <= 3; i++) {
            List<MdmFormItemDataBO> itemDataList = new ArrayList<>();
            MdmFormItemDataBO item = new MdmFormItemDataBO();
            item.setFormItemCode("test_field");
            item.setSeq(-1);
            item.setValue("测试值" + i);
            item.setValueType("string");
            itemDataList.add(item);

            mdmFormDataService.submit(testFormId, testFormDefId, itemDataList);
        }

        // 2. 分页查询
        Page<MdmFormData> page = new Page<>(1, 10);
        Page<MdmFormDataBO> result = mdmFormDataService.pageByFormId(page, testFormId);

        // 3. 验证结果
        assertNotNull(result);
        assertTrue(result.getTotal() >= 3);
        assertTrue(result.getRecords().size() >= 3);

        System.out.println("✅ 分页查询成功，总数: " + result.getTotal());
    }

    @Test
    void testDeleteById() {
        // 1. 提交表单数据
        List<MdmFormItemDataBO> itemDataList = new ArrayList<>();
        MdmFormItemDataBO item = new MdmFormItemDataBO();
        item.setFormItemCode("test_field");
        item.setSeq(-1);
        item.setValue("待删除的值");
        item.setValueType("string");
        itemDataList.add(item);

        MdmFormDataBO submitted = mdmFormDataService.submit(testFormId, testFormDefId, itemDataList);

        // 2. 删除表单数据
        Boolean result = mdmFormDataService.deleteById(submitted.getId());

        // 3. 验证结果
        assertTrue(result);

        // 4. 尝试查询（应抛出异常）
        assertThrows(Exception.class, () -> mdmFormDataService.getById(submitted.getId()));

        System.out.println("✅ 删除表单数据成功（逻辑删除）");
    }

    @Test
    void testSubmitComplexData() {
        // 测试复杂场景：包含单值、列表、不同类型的字段
        List<MdmFormItemDataBO> itemDataList = new ArrayList<>();

        // 文本字段
        itemDataList.add(createItem("title", -1, "需求标题", "string", null));

        // 数字字段
        itemDataList.add(createItem("priority", -1, "3", "number", null));

        // 日期字段
        itemDataList.add(createItem("due_date", -1, "2026-05-31", "date", "yyyy-MM-dd"));

        // 列表字段（参与人）
        for (int i = 0; i < 2; i++) {
            itemDataList.add(createItem("participants", i, "用户" + (i + 1), "string", null));
        }

        // 提交
        MdmFormDataBO result = mdmFormDataService.submit(testFormId, testFormDefId, itemDataList);

        // 验证
        assertNotNull(result);
        assertEquals(5, result.getItemDataList().size());

        // 验证列表字段顺序
        List<MdmFormItemDataBO> participants = result.getItemDataList().stream()
                .filter(item -> "participants".equals(item.getFormItemCode()))
                .sorted((a, b) -> a.getSeq().compareTo(b.getSeq()))
                .toList();
        assertEquals(2, participants.size());
        assertEquals(0, participants.get(0).getSeq());
        assertEquals(1, participants.get(1).getSeq());

        System.out.println("✅ 提交复杂表单数据成功");
        System.out.println("   包含字段: title, priority, due_date, participants[2]");
    }

    /**
     * 辅助方法：创建字段数据
     */
    private MdmFormItemDataBO createItem(String code, int seq, String value, String type, String format) {
        MdmFormItemDataBO item = new MdmFormItemDataBO();
        item.setFormItemCode(code);
        item.setSeq(seq);
        item.setValue(value);
        item.setValueType(type);
        item.setFormat(format);
        return item;
    }
}
