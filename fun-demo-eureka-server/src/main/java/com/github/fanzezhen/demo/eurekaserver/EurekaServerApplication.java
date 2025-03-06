package com.github.fanzezhen.demo.eurekaserver;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.Environment;

import java.lang.management.ManagementFactory;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author zezhen.fan
 */
@EnableEurekaServer
@SpringBootApplication
public class EurekaServerApplication {
    private static final Log log = LogFactory.get();

    public static void main(String[] args) {
        ConfigurableApplicationContext application = SpringApplication.run(EurekaServerApplication.class, args);
        Environment env = application.getEnvironment();
        String ip = CharSequenceUtil.EMPTY;
        try {
            ip = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            log.warn("", e);
        }
        String port = env.getProperty("server.port");
        String path = env.getProperty("server.servlet.context-path");
        if (CharSequenceUtil.isEmpty(path)) {
            path = "";
        }
        log.info("\n----------------------------------------------------------\n\t" +
                "Application  is running! Access URLs:\n\t" +
                "Local访问网址: \t\thttp://localhost:" + port + path + "\n\t" +
                "External访问网址: \thttp://" + ip + ":" + port + path + "\n\t" +
                "----------------------------------------------------------");
        String jvmName = ManagementFactory.getRuntimeMXBean().getName();
        log.debug("当前项目进程号：" + jvmName.split("@")[0]);
    }

}
