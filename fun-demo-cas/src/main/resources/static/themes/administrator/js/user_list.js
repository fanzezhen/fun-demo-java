const User = {};
layui.use(['element', 'form', 'laypage'], function () {
    const $ = layui.$;
    const element = layui.element;
    const form = layui.form;
    const laypage = layui.laypage;
    let current = 1;    // 当前页
    let size = 5;   // 每页条数
    let total = 0; // 总条数

    //监听提交
    form.on('submit(search)', function () {
        User.searchPage();
        return false;   // 阻止表单跳转。如果需要表单跳转，去掉这段即可。
    });

    User.queryData = function () {
        const searchForm = form.val("searchForm");
        return {
            "current": current,
            "size": size,
            "startDate": searchForm.start,
            "endDate": searchForm.end,
            "param": searchForm
        };
    };


    User.searchPage = function () {
        ajaxCommit(AdminConstant.API.USER_LIST.URL, CommonConstant.AJAX_TYPE.POST, JSON.stringify(User.queryData()),
            CommonConstant.CONTENT_TYPE.JSON, pageSuccess, alert_error);
    };

    function pageSuccess(data) {
        const $tbody = $("#tbody");
        $tbody.children().filter('tr').remove();
        data.records.forEach(function (user) {
            const $tr = $("<tr></tr>"); // 生成一个 tr 元素
            $tr.append($("<td><input type=\"checkbox\" name=\"id\" value=\"" + user.id + "\" lay-skin=\"primary\"></td>"));
            $tr.append($("<td>" + user.username + "</td>"));
            $tr.append($("<td>" + user.phone + "</td>"));
            $tr.append($("<td>" + user.email + "</td>"));
            $tr.append($("<td>" + LocalDateUtil.formatDateTime(user.createTime) + "</td>"));
            $tr.append($("<td class=\"td-status\">" + CommonEnum.STATUS[user.status].HTML + "</td>"));
            const $tdManage = $(document.createElement('td')); // 生成一个 td 元素
            const $aStatus = $("<a href=\"javascript:;\"></a>");
            $aStatus.attr("title", CommonEnum.STATUS[CommonEnum.STATUS[user.status].CHANGE].NAME);
            $aStatus.attr("onclick", "user_stop(" + user.id + ", $(this), CommonEnum.STATUS[" + user.status + "])");
            $aStatus.append("<i class=\"layui-icon\">" + CommonEnum.STATUS[CommonEnum.STATUS[user.status].CHANGE].ICONFONT + "</i>");
            $tdManage.append($aStatus);
            const $aEdit = $("<a title=\"编辑\" href=\"javascript:;\"></a>");
            $aEdit.attr("onclick", "xadmin.open('修改用户', '" + AdminConstant.API.USER_EDIT.URL + "?userId=" + escape(user.id) + "', 700, 500)");
            $aEdit.append("<i class=\"layui-icon\">&#xe642;</i>");
            $tdManage.append($aEdit);
            const $aApiEdit = $("<a title=\"用户API绑定\" href=\"javascript:;\"></a>");
            $aApiEdit.attr("onclick", "xadmin.open('用户API绑定', '" + AdminConstant.API.USER_API.URL + "?userId=" + escape(user.id) + "', 700, 500)");
            $aApiEdit.append("<i class=\"layui-icon\">&#xe642;</i>");
            $tdManage.append($aApiEdit);
            const $aDel = $("<a title=\"删除\" onclick=\"user_del(this, " + user.id + ")\" href=\"javascript:;\"></a>");
            $aDel.append("<i class=\"layui-icon\">&#xe640;</i>");
            $tdManage.append($aDel);
            $tr.append($tdManage);
            $tbody.append($tr);
        });

        total = data.total;
        User.paged();
        element.init();
        form.render();
    }

    /**
     * 分页方法
     */
    User.paged = function () {
        laypage.render({
            elem: 'ym',
            count: total, //数据总数，从服务端得到
            curr: current,
            limit: size,
            layout: ['prev', 'page', 'next', 'count', 'skip'],
            jump: function (obj, first) {
                current = obj.curr;
                total = obj.count;
                //首次不执行
                if (!first) {
                    User.searchPage();
                }
            }
        });
    };

    User.searchPage();
});

function delAll() {
    const idList = [];

    // 获取选中的id
    $('tbody input').each(function () {
        if ($(this).prop('checked')) {
            idList.push($(this).val())
        }
    });

    layer.confirm('确认要删除吗？' + idList.toString(), function () {
        //捉到所有被选中的，发异步进行删除
        ajaxCommit(AdminConstant.API.USER_DEL_BATCH.URL, CommonConstant.AJAX_TYPE.POST, "idList=" + idList,
            CommonConstant.CONTENT_TYPE.FORM, successDel, alert_error);
    });
}

function successDel() {
    layer.msg('删除成功', {icon: 1});
    $(".layui-form-checked").not('.header').parents('tr').remove();
}

/*角色-启用/停用*/
function user_stop(userId, $obj, STATUS) {
    layer.confirm('确认要' + $obj.attr('title') + '吗？', function () {
        ajaxCommit(AdminConstant.API.USER_ADD.URL, CommonConstant.AJAX_TYPE.POST, JSON.stringify({
            id: userId,
            status: STATUS.CHANGE
        }), CommonConstant.CONTENT_TYPE.JSON, success, alert_error);
    });

    function success() {
        $obj.attr('title', STATUS.NAME);
        $obj.attr("onclick", "user_stop(this, CommonEnum.STATUS[" + STATUS.CHANGE + "])");
        $obj.find('i').html(STATUS.ICONFONT);
        $obj.parents("tr").find(".td-status").find('span')
            .removeClass('layui-btn-normal')
            .removeClass('layui-btn-disabled')
            .addClass(CommonEnum.STATUS[STATUS.CHANGE].BTN_CLASS)
            .html(CommonEnum.STATUS[STATUS.CHANGE].NAME);
        layer.msg("已" + CommonEnum.STATUS[STATUS.CHANGE].NAME, {
            icon: CommonEnum.STATUS[STATUS.CHANGE].ICON,
            time: 1000
        });
    }
}

/*角色-删除*/
function user_del(obj, userId) {
    layer.confirm('确认要删除吗？', function () {
        ajaxCommit(AdminConstant.API.USER_DEL_BATCH.URL, CommonConstant.AJAX_TYPE.POST, JSON.stringify({
            id: userId,
            delFlag: 1
        }), CommonConstant.CONTENT_TYPE.JSON, success, alert_error);
    });

    function success() {
        $(obj).parents("tr").remove();
        layer.msg('已删除!', {icon: 1, time: 1000});
    }
}


