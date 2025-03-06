package com.github.fanzezhen.base.eurekaserver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * 配置自动启动浏览器
 *
 * @author zezhen.fan
 */
@Slf4j
@Component
public class DevCommandRunner implements CommandLineRunner {

    @Value("${spring.cloud.client.ipAddress:localhost}")
    private String ip;
    @Value("${server.port:5600}")
    private String port;
    @Value("${server.servlet.context-path:}")
    private String contextPath;
    @Value("${spring.profiles.active:}")
    private String springProfileActive;

    @Override
    public void run(String... args) {
        String localSpringProfileActive = "dev";
        if (!localSpringProfileActive.equals(springProfileActive)) {
            return;
        }
        try {
            log.info("\n----------------------------------------------------------\n\t" +
                    "Application  is running! Access URLs:\n\t" +
                    "Local访问网址: \t\thttp://localhost:" + port + contextPath + "\n\t" +
                    "Local访问网址swagger: \t\thttp://localhost:" + port + contextPath + "/swagger-ui.html" + "\n\t" +
                    "External访问网址: \thttp://" + ip + ":" + port + contextPath + "\n\t" +
                    "External访问网址swagger: \thttp://" + ip + ":" + port + contextPath + "/swagger-ui.html" + "\n\t" +
                    "----------------------------------------------------------");
            Runtime.getRuntime().exec("cmd /c start http://" + ip + ":" + port + contextPath);
        } catch (Throwable ex) {
            log.warn("", ex);
        }
    }
}
