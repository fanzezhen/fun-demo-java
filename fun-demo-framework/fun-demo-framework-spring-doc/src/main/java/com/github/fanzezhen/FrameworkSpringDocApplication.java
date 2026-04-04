package com.github.fanzezhen;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author fanzezhen
 */
@Tag(name = "测试接口", description = "用于测试的接口")
@RestController
@SpringBootApplication
public class FrameworkSpringDocApplication {

    public static void main(String[] args) {
        SpringApplication.run(FrameworkSpringDocApplication.class, args);
    }

    /**
     *
     */
    @Operation(summary = "Hello 接口", description = "返回 hello 字符串")
    @RequestMapping("/hello")
    public Object hello() {
        return "hello";
    }

}
