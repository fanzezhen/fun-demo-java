package com.github.fanzezhen.demo.warm.flow;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 登录接口（供 RuoYi-Vue3 使用）
 * 先写死，后期对接公司 SSO
 */
@RestController
public class SysLoginController {
    @GetMapping("/getInfo")
    public JSONObject getInfo() {
        return JSON.parseObject("{\"msg\":\"操作成功\",\"code\":200,\"permissions\":[\"*:*:*\"],\"roles\":[\"admin\"],\"isDefaultModifyPwd\":false,\"isPasswordExpired\":false,\"user\":{\"createBy\":\"admin\",\"createTime\":\"2025-06-13 14:22:07\",\"updateBy\":null,\"updateTime\":null,\"remark\":\"管理员\",\"params\":{\"@type\":\"java.util.HashMap\"},\"userId\":\"1\",\"deptId\":null,\"userName\":\"admin\",\"nickName\":\"若依\",\"email\":\"ry@163.com\",\"phonenumber\":\"15888888888\",\"sex\":\"1\",\"avatar\":\"/profile/avatar/2026/01/06/08d3cf895eaa44ac93396bf566a689a9.png\",\"status\":\"0\",\"delFlag\":\"0\",\"loginIp\":\"116.238.248.228\",\"loginDate\":\"2026-04-01 18:23:42\",\"pwdUpdateDate\":\"2025-06-13 14:22:07\",\"dept\":null,\"roles\":[{\"createBy\":null,\"createTime\":null,\"updateBy\":null,\"updateTime\":null,\"remark\":null,\"params\":{\"@type\":\"java.util.HashMap\"},\"roleId\":\"1\",\"roleName\":\"超级管理员\",\"roleKey\":\"admin\",\"roleSort\":1,\"dataScope\":\"1\",\"menuCheckStrictly\":false,\"deptCheckStrictly\":false,\"status\":\"0\",\"delFlag\":null,\"flag\":false,\"menuIds\":null,\"deptIds\":null,\"permissions\":null,\"admin\":true}],\"roleIds\":null,\"postIds\":null,\"roleId\":null,\"admin\":true}}");
    }

    @GetMapping("/getRouters")
    public JSONArray getRouters() {
        return JSON.parseArray("[{\"name\":\"System\",\"path\":\"/system\",\"hidden\":false,\"redirect\":\"noRedirect\",\"component\":\"Layout\",\"alwaysShow\":true,\"meta\":{\"title\":\"系统管理\",\"icon\":\"system\",\"noCache\":false,\"link\":null},\"children\":[{\"name\":\"User\",\"path\":\"user\",\"hidden\":false,\"component\":\"system/user/index\",\"meta\":{\"title\":\"用户管理\",\"icon\":\"user\",\"noCache\":false,\"link\":null}},{\"name\":\"Role\",\"path\":\"role\",\"hidden\":false,\"component\":\"system/role/index\",\"meta\":{\"title\":\"角色管理\",\"icon\":\"peoples\",\"noCache\":false,\"link\":null}},{\"name\":\"Menu\",\"path\":\"menu\",\"hidden\":false,\"component\":\"system/menu/index\",\"meta\":{\"title\":\"菜单管理\",\"icon\":\"tree-table\",\"noCache\":false,\"link\":null}},{\"name\":\"Dept\",\"path\":\"dept\",\"hidden\":false,\"component\":\"system/dept/index\",\"meta\":{\"title\":\"部门管理\",\"icon\":\"tree\",\"noCache\":false,\"link\":null}},{\"name\":\"Post\",\"path\":\"post\",\"hidden\":false,\"component\":\"system/post/index\",\"meta\":{\"title\":\"岗位管理\",\"icon\":\"post\",\"noCache\":false,\"link\":null}},{\"name\":\"Dict\",\"path\":\"dict\",\"hidden\":false,\"component\":\"system/dict/index\",\"meta\":{\"title\":\"字典管理\",\"icon\":\"dict\",\"noCache\":false,\"link\":null}},{\"name\":\"Config\",\"path\":\"config\",\"hidden\":false,\"component\":\"system/config/index\",\"meta\":{\"title\":\"参数设置\",\"icon\":\"edit\",\"noCache\":false,\"link\":null}},{\"name\":\"Notice\",\"path\":\"notice\",\"hidden\":false,\"component\":\"system/notice/index\",\"meta\":{\"title\":\"通知公告\",\"icon\":\"message\",\"noCache\":false,\"link\":null}},{\"name\":\"Log\",\"path\":\"log\",\"hidden\":false,\"redirect\":\"noRedirect\",\"component\":\"ParentView\",\"alwaysShow\":true,\"meta\":{\"title\":\"日志管理\",\"icon\":\"log\",\"noCache\":false,\"link\":null},\"children\":[{\"name\":\"Operlog\",\"path\":\"operlog\",\"hidden\":false,\"component\":\"monitor/operlog/index\",\"meta\":{\"title\":\"操作日志\",\"icon\":\"form\",\"noCache\":false,\"link\":null}},{\"name\":\"Logininfor\",\"path\":\"logininfor\",\"hidden\":false,\"component\":\"monitor/logininfor/index\",\"meta\":{\"title\":\"登录日志\",\"icon\":\"logininfor\",\"noCache\":false,\"link\":null}}]}]},{\"name\":\"Monitor\",\"path\":\"/monitor\",\"hidden\":false,\"redirect\":\"noRedirect\",\"component\":\"Layout\",\"alwaysShow\":true,\"meta\":{\"title\":\"系统监控\",\"icon\":\"monitor\",\"noCache\":false,\"link\":null},\"children\":[{\"name\":\"Online\",\"path\":\"online\",\"hidden\":false,\"component\":\"monitor/online/index\",\"meta\":{\"title\":\"在线用户\",\"icon\":\"online\",\"noCache\":false,\"link\":null}},{\"name\":\"Job\",\"path\":\"job\",\"hidden\":false,\"component\":\"monitor/job/index\",\"meta\":{\"title\":\"定时任务\",\"icon\":\"job\",\"noCache\":false,\"link\":null}},{\"name\":\"Druid\",\"path\":\"druid\",\"hidden\":false,\"component\":\"monitor/druid/index\",\"meta\":{\"title\":\"数据监控\",\"icon\":\"druid\",\"noCache\":false,\"link\":null}},{\"name\":\"Server\",\"path\":\"server\",\"hidden\":false,\"component\":\"monitor/server/index\",\"meta\":{\"title\":\"服务监控\",\"icon\":\"server\",\"noCache\":false,\"link\":null}},{\"name\":\"Cache\",\"path\":\"cache\",\"hidden\":false,\"component\":\"monitor/cache/index\",\"meta\":{\"title\":\"缓存监控\",\"icon\":\"redis\",\"noCache\":false,\"link\":null}},{\"name\":\"CacheList\",\"path\":\"cacheList\",\"hidden\":false,\"component\":\"monitor/cache/list\",\"meta\":{\"title\":\"缓存列表\",\"icon\":\"redis-list\",\"noCache\":false,\"link\":null}}]},{\"name\":\"Tool\",\"path\":\"/tool\",\"hidden\":false,\"redirect\":\"noRedirect\",\"component\":\"Layout\",\"alwaysShow\":true,\"meta\":{\"title\":\"系统工具\",\"icon\":\"tool\",\"noCache\":false,\"link\":null},\"children\":[{\"name\":\"Build\",\"path\":\"build\",\"hidden\":false,\"component\":\"tool/build/index\",\"meta\":{\"title\":\"表单构建\",\"icon\":\"build\",\"noCache\":false,\"link\":null}},{\"name\":\"Gen\",\"path\":\"gen\",\"hidden\":false,\"component\":\"tool/gen/index\",\"meta\":{\"title\":\"代码生成\",\"icon\":\"code\",\"noCache\":false,\"link\":null}},{\"name\":\"Swagger\",\"path\":\"swagger\",\"hidden\":false,\"component\":\"tool/swagger/index\",\"meta\":{\"title\":\"系统接口\",\"icon\":\"swagger\",\"noCache\":false,\"link\":null}}]},{\"name\":\"Https://warm-flow.dromara.org/\",\"path\":\"https://warm-flow.dromara.org/\",\"hidden\":false,\"component\":\"Layout\",\"meta\":{\"title\":\"Warm-Flow官网\",\"icon\":\"guide\",\"noCache\":false,\"link\":\"https://warm-flow.dromara.org/\"}},{\"path\":\"/\",\"hidden\":false,\"component\":\"Layout\",\"children\":[{\"name\":\"Approve\",\"path\":\"system/process/approve\",\"hidden\":false,\"meta\":{\"title\":\"测试\",\"icon\":\"#\",\"noCache\":false,\"link\":null}}]},{\"name\":\"Flow\",\"path\":\"/flow\",\"hidden\":false,\"redirect\":\"noRedirect\",\"component\":\"Layout\",\"alwaysShow\":true,\"meta\":{\"title\":\"流程管理\",\"icon\":\"cascader\",\"noCache\":false,\"link\":null},\"children\":[{\"name\":\"Definition\",\"path\":\"definition\",\"hidden\":false,\"component\":\"flow/definition/index\",\"meta\":{\"title\":\"流程定义\",\"icon\":\"online\",\"noCache\":false,\"link\":null}},{\"name\":\"Todo\",\"path\":\"todo\",\"hidden\":false,\"component\":\"flow/task/todo/index\",\"meta\":{\"title\":\"待办任务\",\"icon\":\"guide\",\"noCache\":false,\"link\":null}},{\"name\":\"1\",\"path\":\"1\",\"hidden\":false,\"component\":\"flow/task/done/index\",\"meta\":{\"title\":\"已办任务\",\"icon\":\"druid\",\"noCache\":false,\"link\":null}},{\"name\":\"Notice\",\"path\":\"notice\",\"hidden\":false,\"component\":\"flow/notice/index\",\"meta\":{\"title\":\"抄送任务\",\"icon\":\"email\",\"noCache\":false,\"link\":null}}]},{\"name\":\"Test\",\"path\":\"/test\",\"hidden\":false,\"redirect\":\"noRedirect\",\"component\":\"Layout\",\"alwaysShow\":true,\"meta\":{\"title\":\"测试菜单\",\"icon\":\"example\",\"noCache\":false,\"link\":null},\"children\":[{\"name\":\"Leave\",\"path\":\"leave\",\"hidden\":false,\"component\":\"system/leave/index\",\"meta\":{\"title\":\"OA 请假申请\",\"icon\":\"#\",\"noCache\":false,\"link\":null}},{\"name\":\"Process\",\"path\":\"process\",\"hidden\":false,\"component\":\"system/process/index\",\"meta\":{\"title\":\"合同流程\",\"icon\":\"#\",\"noCache\":false,\"link\":null}},{\"name\":\"Steps\",\"path\":\"steps\",\"hidden\":false,\"component\":\"system/steps/index\",\"meta\":{\"title\":\"企业采购\",\"icon\":\"#\",\"noCache\":false,\"link\":null}}]}]");
    }

    @PostMapping("/login")
    public JSONObject login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        String code = params.get("code");
        String uuid = params.get("uuid");

        // ===================== 测试写死 =====================
        // 后期这里调用公司 SSO 接口
        if ("admin".equals(username) && "admin123".equals(password)) {
            JSONObject tokenMap = new JSONObject();
            tokenMap.put("token", "FUN-DEMO-WARM-FLOW-TEST-TOKEN-123456789");
            return tokenMap;
        }
        throw new ServiceException("用户名或密码错误");
    }

    @PostMapping("/logout")
    public void logout() {
    }
}
