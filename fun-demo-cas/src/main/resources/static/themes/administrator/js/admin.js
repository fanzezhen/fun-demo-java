

const AdminEnum = {
    ROLE_TYPE: {0: "超级管理员", 1: "管理员", 2: "普通角色", 3: "访客角色"},
    RULE_TYPE: {1: "菜单", 2: "按钮"},
};

const Admin = {};

layui.use(['element', 'form', 'laypage'], function () {
    const form = layui.form;

    //监听提交
    form.on('submit(search)', function () {
        Admin.searchPage();
        return false;   // 阻止表单跳转。如果需要表单跳转，去掉这段即可。
    });

});

