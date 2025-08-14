package com.github.fanzezhen.demo.spring.security;


import com.github.fanzezhen.fun.framework.security.base.FunSecurityFacade;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PermissionFacadeImpl implements FunSecurityFacade {
    /**
     * 获取需要管控的接口列表
     *
     * @param serviceCode 应用标识
     */
    @Override
    public List<String> needManageUriList(String serviceCode) {
        return List.of();
    }

    /**
     * 获取用户有权限的接口列表
     *
     * @param serviceCode 应用标识
     * @param username    用户名
     */
    @Override
    public List<String> holdUriList(String serviceCode, String username) {
        return List.of();
    }

    /**
     * 获取用户有权限的接口列表
     *
     * @param serviceCode 应用标识
     * @param username    用户名
     */
    @Override
    public List<String> holdRoleList(String serviceCode, String username) {
        return List.of();
    }
}
