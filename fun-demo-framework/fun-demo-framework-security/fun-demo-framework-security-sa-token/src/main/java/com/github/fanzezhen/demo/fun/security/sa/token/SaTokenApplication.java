package com.github.fanzezhen.demo.fun.security.sa.token;

import com.github.fanzezhen.fun.framework.security.sa.token.ILoginHandle;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author fanzezhen
 */
@RestController
@SpringBootApplication
public class SaTokenApplication {

    public static void main(String[] args) {
        SpringApplication.run(SaTokenApplication.class, args);
    }

    /**
     *
     */
    @RequestMapping("/hello")
    public Object hello() {
        return "hello";
    }
    /**
     *
     */
    @RequestMapping("/login")
    public Object login(@RequestParam(name = "username") String username, @RequestParam(name = "mode") String mode) {
        ILoginHandle<?, ?, LoginParameter, ?> loginHandle = ILoginHandle.getLoginHandle(mode);
        return loginHandle.doLogin(new LoginParameter().setUsername(username));
    }

}
