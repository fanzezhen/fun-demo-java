package com.github.fanzezhen.demo.lowcode.maginapi;

import org.springframework.stereotype.Component;
import org.ssssssss.magicapi.modules.db.BoundSql;
import org.ssssssss.magicapi.modules.db.dialect.KingbaseSQLDialect;

/**
 * 神通
 *
 * @author fanzezhen
 * @createTime 2024/1/8 16:02
 * @since 1.0
 */
@Component
public class OsrDialect extends KingbaseSQLDialect {
    /**
     * 匹配规则
     */
    @Override
    public boolean match(String jdbcUrl) {
        return jdbcUrl.contains(":oscar:");
    }

    /**
     * 拼接分页sql
     */
    @Override
    public String getPageSql(String sql, BoundSql boundSql, long offset, long limit) {
        boundSql.addParameter(offset);
        boundSql.addParameter(limit);
        return sql + "\n limit ?,?";
    }

}
