package com.github.fanzezhen.demo.fun.security.sa.token;

import com.github.fanzezhen.fun.framework.security.base.ILoginResult;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LoginResult implements ILoginResult<Long, User> {
    private User user;
    private String token;

}
