package com.github.fanzezhen.demo.cas;

import cn.hutool.extra.spring.SpringUtil;
import com.github.fanzezhen.demo.cas.model.SysUserDto;
import com.zaxxer.hikari.HikariDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.authentication.AuthenticationException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

/**
 * @author zezhen.fan
 */
@Slf4j
public class DataService {
    public SysUserDto getByUsernameNotNull(String username) {
        log.info("getByUsernameNotNull {}" , username);
        DataSource dataSource = getDataSourceFromSpring();
        // 创建JDBC模板
        JdbcTemplate jdbcTemplate = new JdbcTemplate();
        jdbcTemplate.setDataSource(dataSource);
        String sql = "SELECT * FROM auth_user WHERE username = ?";
        SysUserDto sysUserDto = jdbcTemplate.queryForObject(sql, new Object[]{username}, new BeanPropertyRowMapper<>(SysUserDto.class));
        if (sysUserDto == null) {
            throw new AuthenticationException("用户名不存在：" + username);
        }
        return sysUserDto;
    }

    private DataSource getDataSourceFromSpring() {
        return SpringUtil.getBean(HikariDataSource.class);
    }

    private DataSource getDataSource() {
        // JDBC模板依赖于连接池来获得数据的连接，所以必须先要构造连接池
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/demo?serverTimezone=UTC&characterEncoding=utf-8&useUnicode=true&useSSL=false");
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        return dataSource;
    }

    private static class SingletonHolder {
        public static DataService INSTANCE = new DataService();
    }

    public static DataService getInstance() {
        return DataService.SingletonHolder.INSTANCE;
    }
}
