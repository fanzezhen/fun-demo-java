package com.github.fanzezhen.demo.warm.flow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


/**
 * @author fanzezhen
 */
@SpringBootApplication(scanBasePackages = {"com.github.fanzezhen.demo.warm.flow", "org.dromara.warm"})
public class WarmFlowApplication {

    public static void main(String[] args) {
        SpringApplication.run(WarmFlowApplication.class, args);
    }

}
