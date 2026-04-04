package com.github.fanzezhen.demo.fun.security.sa.token;

import cn.dev33.satoken.stp.SaTokenInfo;
import com.github.fanzezhen.fun.framework.security.sa.token.ILoginHandle;
import org.springframework.stereotype.Service;

/**
 * 原生登录 请求处理器
 */
@Service
public class NativeLoginHandle implements ILoginHandle<Long, User, LoginParameter, LoginResult> {


    /**
     * 是否支持指定的登录方式
     */
    @Override
    public boolean isSupport(Object mode) {
        return true;
    }

    /**
     * 验证登录用户
     */
    @Override
    public User verify(LoginParameter parameter) {
        return new User().setUsername(parameter.getCode());
    }

    /**
     * 制作登录结果
     */
    @Override
    public LoginResult makeLoginResult(User user, SaTokenInfo tokenInfo) {
        return new LoginResult().setUser(user).setToken(tokenInfo.getTokenValue());
    }
}
