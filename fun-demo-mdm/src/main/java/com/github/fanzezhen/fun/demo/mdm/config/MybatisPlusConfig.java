package com.github.fanzezhen.fun.demo.mdm.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置
 * 配置分页插件，确保分页查询时执行 count 查询
 *
 * @author Claude
 * @since 4.0.6
 */
@Configuration
public class MybatisPlusConfig {

    @Bean
    public InnerInterceptor paginationInnerInterceptor() {
        // 添加分页插件（H2 数据库）
        return new PaginationInnerInterceptor(DbType.H2);
    }
}
