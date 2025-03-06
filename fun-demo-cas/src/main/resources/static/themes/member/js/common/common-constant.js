const CommonConstant = {
    CONTENT_TYPE: {
        JSON: "application/json;charset=utf-8",
        FORM: "application/x-www-form-urlencoded;charset=utf-8"
    },
    AJAX_TYPE: {
        POST: "POST"
    },
    API: {
        BASE: {URL: "", TYPE: "html", DESC: "用户首页"}
    }
};

// 初始化API接口列表
CommonConstant.API["HOME"] = {URL: CommonConstant.API.BASE.URL + "/hello", TYPE: "html", DESC: "首页"};

// admin模块常量
const AdminConstant = {
    API: {BASE: {URL: "/admin", TYPE: "html", DESC: "管理员首页"}}
};
// 初始化API接口列表
AdminConstant.API["APP_LIST"] = {URL: AdminConstant.API.BASE.URL + "/app/list", TYPE: "html", DESC: "应用管理"};
AdminConstant.API["APP_LIST_PAGE"] = {URL: AdminConstant.API.BASE.URL + "/app/list/page", TYPE: "data", DESC: "应用分页查询"};
AdminConstant.API["APP_ADD"] = {URL: AdminConstant.API.BASE.URL + "/app/add", TYPE: "html/data", DESC: "应用添加"};
AdminConstant.API["APP_EDIT"] = {URL: AdminConstant.API.BASE.URL + "/app/edit", TYPE: "html/data", DESC: "应用编辑"};
AdminConstant.API["API_LIST"] = {URL: AdminConstant.API.BASE.URL + "/api/list", TYPE: "html/data", DESC: "接口管理"};
AdminConstant.API["API_ADD"] = {URL: AdminConstant.API.BASE.URL + "/api/add", TYPE: "html/data", DESC: "接口添加"};
AdminConstant.API["API_EDIT"] = {URL: AdminConstant.API.BASE.URL + "/api/edit", TYPE: "html/data", DESC: "接口编辑"};
AdminConstant.API["USER_LIST"] = {URL: AdminConstant.API.BASE.URL + "/user/list", TYPE: "html/data", DESC: "用户管理"};
AdminConstant.API["USER_ADD"] = {URL: AdminConstant.API.BASE.URL + "/user/add", TYPE: "html/data", DESC: "新增用户"};
AdminConstant.API["USER_EDIT"] = {URL: AdminConstant.API.BASE.URL + "/user/edit", TYPE: "html/data", DESC: "修改用户"};
AdminConstant.API["USER_API"] = {URL: AdminConstant.API.BASE.URL + "/user/api", TYPE: "html/data", DESC: "绑定用户API"};
AdminConstant.API["USER_DEL_BATCH"] = {URL: AdminConstant.API.BASE.URL + "/role", TYPE: "html", DESC: "角色管理"};
AdminConstant.API["TOKEN_LIST"] = {URL: AdminConstant.API.BASE.URL + "/token/list", TYPE: "html/data", DESC: "令牌管理"};
AdminConstant.API["TOKEN_ADD"] = {URL: AdminConstant.API.BASE.URL + "/token/add", TYPE: "html/data", DESC: "令牌申请"};
AdminConstant.API["ROLE_SAVE"] = {URL: AdminConstant.API.BASE.URL + "/role/save", TYPE: "data", DESC: "保存角色"};
AdminConstant.API["RULE_LIST"] = {URL: AdminConstant.API.BASE.URL + "/rule", TYPE: "html", DESC: "权限管理"};
AdminConstant.API["RULE_LIST_PAGE"] = {URL: AdminConstant.API.BASE.URL + "/rule/page", TYPE: "data", DESC: "权限分页查询"};
AdminConstant.API["RULE_EDIT"] = {URL: AdminConstant.API.BASE.URL + "/rule/edit", TYPE: "html", DESC: "编辑权限"};
AdminConstant.API["RULE_SAVE"] = {URL: AdminConstant.API.BASE.URL + "/rule/save", TYPE: "data", DESC: "保存权限"};
AdminConstant.API["RULE_DEL_BATCH"] = {
    URL: AdminConstant.API.BASE.URL + "/rule/del/batch",
    TYPE: "data",
    DESC: "批量删除"
};
