package com.github.fanzezhen.demo.fun.security.sa.token;

import com.github.fanzezhen.fun.framework.security.base.ILoginParameter;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class LoginParameter implements ILoginParameter {
    String username;
    /**
     * 登录标识，如 username、ticket 等
     */
    @Override
    public String getCode() {
        return username;
    }
}
