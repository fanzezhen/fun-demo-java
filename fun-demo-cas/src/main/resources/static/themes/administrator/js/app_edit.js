layui.use(['element', 'form', 'laypage'], function () {
    const App = {};
    $ = layui.jquery;
    var form = layui.form
        , layer = layui.layer;

    //自定义验证规则
    form.verify({
        username: function (value) {
            if (value.length < 3) {
                return '名称至少得3个字符啊';
            }
        }
    });

    //监听提交
    form.on('submit(save)', function (data) {
        //发异步，把数据提交
        console.log(JSON.stringify(data.field));
        console.log(data.field.id == null);
        console.log(data.field.id === undefined);
        console.log(data.field.id);
        ajaxCommit(data.field.id == null ? AdminConstant.API.APP_ADD.URL : AdminConstant.API.APP_EDIT.URL,
            CommonConstant.AJAX_TYPE.POST, JSON.stringify(data.field), CommonConstant.CONTENT_TYPE.JSON, App.saveSuccess, alert_error);
        return false;
    });
    //10.10.11.56:9090/

    App.saveSuccess = function (data) {
        console.debug(data);
        layer.alert("保存成功", {icon: 6}, function () {
            // 获得frame索引
            const index = parent.layer.getFrameIndex(window.name);
            //关闭当前frame
            parent.layer.close(index);
        });
    }
});

