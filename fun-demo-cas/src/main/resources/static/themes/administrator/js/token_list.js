const Token = {};
layui.use(['element', 'form', 'laypage'], function () {
    const $ = layui.$;
    const element = layui.element;
    const form = layui.form;
    const laypage = layui.laypage;
    let current = 1;    // 当前页
    let size = 5;   // 每页条数
    let total = 0; // 总条数

    // 监听全选
    form.on('checkbox(checkall)', function (data) {
        if (data.elem.checked) {
            $('tbody input').prop('checked', true);
        } else {
            $('tbody input').prop('checked', false);
        }
        form.render('checkbox');
    });

    //监听提交
    form.on('submit(search)', function () {
        Token.searchPage();
        return false;   // 阻止表单跳转。如果需要表单跳转，去掉这段即可。
    });

    Token.queryData = function () {
        const searchForm = form.val("searchForm");
        return {
            "current": current,
            "size": size,
            "param": searchForm
        };
    };


    Token.searchPage = function () {
        ajaxCommit(AdminConstant.API.TOKEN_LIST.URL, CommonConstant.AJAX_TYPE.POST, JSON.stringify(Token.queryData()),
            CommonConstant.CONTENT_TYPE.JSON, pageSuccess, alert_error);
    };

    function pageSuccess(data) {
        const $tbody = $("#tbody");
        $tbody.children().filter('tr').remove();
        data.records.forEach(function (token) {
            const $tr = $("<tr></tr>"); // 生成一个 tr 元素
            $tr.append($("<td><input type=\"checkbox\" name=\"id\" value=\"" + token.id + "\" lay-skin=\"primary\"></td>"));
            $tr.append($("<td>" + token.token + "</td>"));
            $tr.append($("<td>" + token.valid + "</td>"));
            $tr.append($("<td>" + token.statusDesc + "</td>"));
            $tr.append($("<td>" + LocalDateUtil.formatDate(token.expireTime) + "</td>"));
            $tr.append($("<td>" + token.createUserName + "</td>"));
            $tr.append($("<td>" + LocalDateUtil.formatDateTime(token.createTime) + "</td>"));
            $tbody.append($tr);
        });

        total = data.total;
        Token.paged();
        element.init();
        form.render();
    }

    /**
     * 分页方法
     */
    Token.paged = function () {
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
                    Token.searchPage();
                }
            }
        });
    };

    Token.searchPage();
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
        ajaxCommit(AdminConstant.API.DEL_BATCH.URL, CommonConstant.AJAX_TYPE.POST, "idList=" + idList, CommonConstant.CONTENT_TYPE.FORM, successDel, alert_error);
    });
}

function successDel() {
    layer.msg('删除成功', {icon: 1});
    $(".layui-form-checked").not('.header').parents('tr').remove();
}

