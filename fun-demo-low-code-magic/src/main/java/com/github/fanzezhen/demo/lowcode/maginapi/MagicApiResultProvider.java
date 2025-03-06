package com.github.fanzezhen.demo.lowcode.maginapi;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.fanzezhen.fun.framework.core.model.response.ActionResult;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.ssssssss.magicapi.core.context.RequestEntity;
import org.ssssssss.magicapi.core.interceptor.ResultProvider;

import java.sql.SQLException;
import java.util.List;
import java.util.Map;

/**
 * 自定义MagicApi返回结果
 *
 * @author fanzezhen
 * @createTime 2024/1/8 16:33
 * @since 1.0
 */
@Component
public class MagicApiResultProvider implements ResultProvider {
    /**
     * 统一异常处理
     */
    @Override
    public Object buildException(RequestEntity requestEntity, Throwable throwable) {
        if (throwable instanceof SQLException || throwable.getCause() instanceof SQLException) {
            return buildResult(requestEntity, -1, "magic-api 数据库语句执行失败 " + throwable.getLocalizedMessage());
        }
        return buildResult(requestEntity, 500, "系统内部出现错误" + throwable.getLocalizedMessage());
    }

    /**
     * 定义返回结果，默认返回JsonBean
     */
    @Override
    public Object buildResult(RequestEntity requestEntity, int code, String message, Object data) {
        // 如果对分页格式有要求的话，可以对data的类型进行判断，进而返回不同的格式
        ActionResult<Object> result = ActionResult.success(data);
        MDC.put("接口返回结果", JSON.toJSONString(result));
        return result;
    }

    /**
     * 定义分页返回结果，该项会被封装在Json结果内， 此方法可以不覆盖，默认返回PageResult
     */
    @Override
    public Object buildPageResult(RequestEntity requestEntity, org.ssssssss.magicapi.modules.db.model.Page page, long total, List<Map<String, Object>> data) {
        return new Page<Map<String, Object>>(page.getOffset() / page.getLimit() + 1, page.getLimit(), total).setRecords(data);
    }
}
