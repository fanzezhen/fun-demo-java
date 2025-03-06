layui.use(['element', 'form', 'laypage'], function () {
    const Api = {};
    const $ = layui.jquery;
    const form = layui.form, layer = layui.layer;

    //自定义验证规则
    form.verify({
        name: function (value) {
            if (value.length < 3) {
                return '名称至少得3个字符啊';
            }
        }
    });

    //监听提交
    form.on('submit(save)', function (data) {
        //发异步，把数据提交
        console.log(JSON.stringify(data.field));
        const reqList = [];
        if (data.field.reqName)
            reqList.push({name: data.field.reqName, class: data.field.reqClass, msg: data.field.reqMsg});
        if (data.field.reqName1)
            reqList.push({name: data.field.reqName1, class: data.field.reqClass1, msg: data.field.reqMsg1});
        if (data.field.reqName2)
            reqList.push({name: data.field.reqName2, class: data.field.reqClass2, msg: data.field.reqMsg2});
        data.field.reqList = reqList;
        const resList = [];
        if (data.field.resName)
            resList.push({name: data.field.resName, class: data.field.resClass, msg: data.field.resMsg});
        if (data.field.resName1)
            resList.push({name: data.field.resName1, class: data.field.resClass1, msg: data.field.resMsg1});
        data.field.resList = resList;
        ajaxCommit(data.field.id ? AdminConstant.API.API_ADD.URL : AdminConstant.API.API_EDIT.URL,
            CommonConstant.AJAX_TYPE.POST, JSON.stringify(data.field), CommonConstant.CONTENT_TYPE.JSON, Api.saveSuccess, alert_error);
        return false;
    });

    Api.saveSuccess = function (data) {
        console.debug(data);
        layer.alert("保存成功", {icon: 6}, function () {
            // 获得frame索引
            const index = parent.layer.getFrameIndex(window.name);
            //关闭当前frame
            parent.layer.close(index);
        });
    }
});

