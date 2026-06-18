package com.github.fanzezhen.demo.fun.security.sa.token;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.github.fanzezhen.fun.framework.core.model.common.IUser;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
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
