package com.github.fanzezhen.demo.lowcode.maginapi;

import com.github.fanzezhen.fun.framework.all.EnableFunAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * @author fanzezhen
 * @createTime 2023/12/11 9:00
 * @since 1.0-SNAPSHOT
 */
@EnableFunAutoConfiguration
@SpringBootApplication
public class MagicApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(MagicApiApplication.class, args);
    }

}
