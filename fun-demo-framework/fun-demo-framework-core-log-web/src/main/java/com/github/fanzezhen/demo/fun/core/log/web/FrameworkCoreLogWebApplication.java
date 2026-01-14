package com.github.fanzezhen.demo.fun.core.log.web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author fanzezhen
 */
@RestController
@SpringBootApplication
public class FrameworkCoreLogWebApplication {

    public static void main(String[] args) {
        SpringApplication.run(FrameworkCoreLogWebApplication.class, args);
    }

    /**
     *
     */
    @RequestMapping("/hello")
    public Object hello() {
        return "hello";
    }

}
