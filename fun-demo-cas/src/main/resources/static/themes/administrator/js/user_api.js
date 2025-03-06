const UserApi = {};
layui.use(['element', 'form', 'laypage'], function () {
    $ = layui.jquery;
    const form = layui.form, layer = layui.layer;

    form.on('checkbox(father)', function (data) {
        if (data.elem.checked) {
            $(data.elem).parent().siblings('td').find('input').prop("checked", true);
            form.render();
        } else {
            $(data.elem).parent().siblings('td').find('input').prop("checked", false);
            form.render();
        }
    });

    //监听提交
    form.on('submit(edit)', function (data) {
        //获取checkbox[name='apis']的值
        const apiIdSet = [];
        $("input:checkbox[name='apis']:checked").each(function (i) {
            apiIdSet[i] = $(this).val();
        });
        data.field.apiIdSet = apiIdSet;
        //发异步，把数据提交
        console.log(JSON.stringify(data.field));
        ajaxCommit(AdminConstant.API.USER_API.URL, CommonConstant.AJAX_TYPE.POST, JSON.stringify(data.field),
            CommonConstant.CONTENT_TYPE.JSON, UserApi.saveSuccess, alert_error);
        return false;
    });

    UserApi.saveSuccess = function (data) {
        console.debug(data);
        layer.alert("保存成功", {icon: 6}, function () {
            // 获得frame索引
            const index = parent.layer.getFrameIndex(window.name);
            //关闭当前frame
            parent.layer.close(index);
        });
    }
});

