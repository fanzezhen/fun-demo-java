package com.github.fanzezhen.demo.fun.security.sa.token;

import com.github.fanzezhen.fun.framework.core.model.IUser;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class User implements IUser<Long> {
    private String username;

    /**
     * 登录标识，建议返回id或username
     */
    @Override
    public Long getLoginCode() {
        return 1L;
    }
}
