package com.github.fanzezhen.demo.lowcode.maginapi;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 配置自动启动浏览器
 *
 * @author zezhen.fan
 */
@Slf4j
@Component
public class FzzCommandRunner implements CommandLineRunner {

    @Value("${spring.cloud.client.ipAddress:localhost}")
    private String ip;

    @Value("${server.port:8080}")
    private String port;

    @Value("${server.servlet.context-path:}")
    private String contextPath;
    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) {
        try {
            log.info("\n----------------------------------------------------------\n\t" +
                    "Application  is running! Access URLs:\n\t" +
                    "Local访问网址: \t\thttp://localhost:" + port + contextPath + "\n\t" +
                    "Local访问网址swagger: \t\thttp://localhost:" + port + contextPath + "/swagger-ui.html" + "\n\t" +
                    "External访问网址: \thttp://" + ip + ":" + port + contextPath + "\n\t" +
                    "External访问网址swagger: \thttp://" + ip + ":" + port + contextPath + "/swagger-ui.html" + "\n\t" +
                    "----------------------------------------------------------");
//            Runtime.getRuntime().exec("cmd /c start http://" + ip + ":" + port + contextPath + "/swagger-ui.html");
            Runtime.getRuntime().exec("cmd /c start http://" + ip + ":" + port + contextPath + "/magic/web/index.html");
        } catch (Exception ex) {
            log.warn("", ex);
        }
    }
}
