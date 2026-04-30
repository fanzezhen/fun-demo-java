package com.github.fanzezhen.fun.demo.mdm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.fanzezhen.fun.demo.mdm.MdmApplication;
import com.github.fanzezhen.fun.demo.mdm.bo.MdmFormBO;
import com.github.fanzezhen.fun.demo.mdm.entity.MdmForm;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 动态表单服务测试
 * 演示框架的 MyBatis-Plus 能力
 *
 * @author Claude
 * @since 2026-04-30
 */
@SpringBootTest(classes = MdmApplication.class)
class MdmFormServiceTest {

    @Resource
    private IMdmFormService mdmFormService;

    @Test
    void testCreate() {
        // 1. 准备测试数据
        MdmForm form = new MdmForm();
        form.setName("用户反馈表");
        form.setRemark("收集用户反馈意见");
        form.setReleased(false);
        form.setOrderNum(1);

        // 2. 创建表单
        MdmFormBO bo = mdmFormService.create(form);

        // 3. 验证结果
        assertNotNull(bo);
        assertNotNull(bo.getId());
        assertEquals("用户反馈表", bo.getName());
        assertFalse(bo.getReleased());
        assertNotNull(bo.getCreateTime()); // 框架自动填充
        assertNotNull(bo.getUpdateTime()); // 框架自动填充

        System.out.println("✅ 创建表单成功: " + bo);
    }

    @Test
    void testUpdate() {
        // 1. 先创建一个表单
        MdmForm form = new MdmForm();
        form.setName("测试表单");
        form.setRemark("测试");
        form.setReleased(false);
        form.setOrderNum(1);
        MdmFormBO created = mdmFormService.create(form);

        // 2. 更新表单
        MdmForm updateForm = new MdmForm();
        updateForm.setId(created.getId());
        updateForm.setName("测试表单（已修改）");
        updateForm.setRemark("更新后的说明");
        updateForm.setVersion(created.getVersion()); // 乐观锁版本号

        MdmFormBO updated = mdmFormService.update(updateForm);

        // 3. 验证结果
        assertNotNull(updated);
        assertEquals("测试表单（已修改）", updated.getName());
        assertEquals("更新后的说明", updated.getRemark());
        assertEquals(created.getVersion() + 1, updated.getVersion()); // 版本号自增

        System.out.println("✅ 更新表单成功: " + updated);
    }

    @Test
    void testGetById() {
        // 1. 先创建一个表单
        MdmForm form = new MdmForm();
        form.setName("查询测试表单");
        form.setRemark("测试查询");
        form.setReleased(true);
        form.setOrderNum(2);
        MdmFormBO created = mdmFormService.create(form);

        // 2. 根据ID查询
        MdmFormBO found = mdmFormService.getById(created.getId());

        // 3. 验证结果
        assertNotNull(found);
        assertEquals(created.getId(), found.getId());
        assertEquals("查询测试表单", found.getName());
        assertTrue(found.getReleased());

        System.out.println("✅ 查询表单成功: " + found);
    }

    @Test
    void testPage() {
        // 1. 创建几个测试表单
        for (int i = 1; i <= 3; i++) {
            MdmForm form = new MdmForm();
            form.setName("分页测试表单" + i);
            form.setRemark("第" + i + "个表单");
            form.setReleased(i % 2 == 0); // 偶数已发布
            form.setOrderNum(i);
            mdmFormService.create(form);
        }

        // 2. 分页查询（查询所有）
        Page<MdmForm> page = new Page<>(1, 10);
        Page<MdmFormBO> result = mdmFormService.page(page, null, null);

        // 3. 验证结果
        assertNotNull(result);
        assertTrue(result.getTotal() >= 3);
        assertTrue(result.getRecords().size() >= 3);

        System.out.println("✅ 分页查询成功，总数: " + result.getTotal());

        // 4. 按名称模糊查询
        Page<MdmForm> page2 = new Page<>(1, 10);
        Page<MdmFormBO> result2 = mdmFormService.page(page2, "分页测试", null);

        assertNotNull(result2);
        assertTrue(result2.getTotal() >= 3);

        System.out.println("✅ 模糊查询成功，总数: " + result2.getTotal());

        // 5. 按发布状态过滤
        Page<MdmForm> page3 = new Page<>(1, 10);
        Page<MdmFormBO> result3 = mdmFormService.page(page3, null, true);

        assertNotNull(result3);
        result3.getRecords().forEach(bo -> assertTrue(bo.getReleased()));

        System.out.println("✅ 状态过滤查询成功，已发布表单数: " + result3.getTotal());
    }

    @Test
    void testRelease() {
        // 1. 创建未发布的表单
        MdmForm form = new MdmForm();
        form.setName("待发布表单");
        form.setRemark("测试发布功能");
        form.setReleased(false);
        form.setOrderNum(1);
        MdmFormBO created = mdmFormService.create(form);

        assertFalse(created.getReleased());

        // 2. 发布表单
        Boolean result = mdmFormService.release(created.getId());

        // 3. 验证结果
        assertTrue(result);

        MdmFormBO released = mdmFormService.getById(created.getId());
        assertTrue(released.getReleased());

        System.out.println("✅ 发布表单成功: " + released);
    }

    @Test
    void testDeleteById() {
        // 1. 创建表单
        MdmForm form = new MdmForm();
        form.setName("待删除表单");
        form.setRemark("测试删除功能");
        form.setReleased(false);
        form.setOrderNum(1);
        MdmFormBO created = mdmFormService.create(form);

        // 2. 删除表单（逻辑删除）
        Boolean result = mdmFormService.deleteById(created.getId());

        // 3. 验证结果
        assertTrue(result);

        // 4. 尝试查询（应抛出异常，因为已逻辑删除）
        assertThrows(Exception.class, () -> mdmFormService.getById(created.getId()));

        System.out.println("✅ 删除表单成功（逻辑删除）");
    }
}
