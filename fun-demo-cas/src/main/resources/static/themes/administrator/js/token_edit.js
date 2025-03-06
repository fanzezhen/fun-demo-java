const Token = {};
layui.use(['element', 'form', 'laydate', 'laypage'], function () {
    $ = layui.jquery;
    const form = layui.form, layer = layui.layer, laydate = layui.laydate;

    //执行一个laydate实例
    laydate.render({
        elem: '#expireTimeString', //指定元素
        trigger: 'click', //添加这一行来处理
        done: function(value, date, endDate){
            // form.createTime = value;
        }
    });

    //监听提交
    form.on('submit(save)', function (data) {
        //发异步，把数据提交
        data.field.username = $("#userId").text()
        console.log(JSON.stringify(data.field));
        ajaxCommit(AdminConstant.API.TOKEN_ADD.URL, CommonConstant.AJAX_TYPE.POST, JSON.stringify(data.field),
            CommonConstant.CONTENT_TYPE.JSON, Token.saveSuccess, alert_error);
        return false;
    });

    Token.saveSuccess = function (data) {
        console.debug(data);
        layer.alert("保存成功", {icon: 6}, function () {
            // 获得frame索引
            const index = parent.layer.getFrameIndex(window.name);
            //关闭当前frame
            parent.layer.close(index);
        });
    }
});

