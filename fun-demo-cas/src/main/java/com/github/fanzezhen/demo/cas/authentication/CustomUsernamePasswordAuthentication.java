package com.github.fanzezhen.demo.cas.authentication;


import com.github.fanzezhen.demo.cas.DataService;
import com.github.fanzezhen.demo.cas.model.SysUserDto;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.authentication.AuthenticationException;
import org.apereo.cas.authentication.AuthenticationHandlerExecutionResult;
import org.apereo.cas.authentication.MessageDescriptor;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;
import org.apereo.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.services.ServicesManager;

import javax.security.auth.login.FailedLoginException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


/**
 * @author anumbrella
 */
@Slf4j
public class CustomUsernamePasswordAuthentication extends AbstractUsernamePasswordAuthenticationHandler {
    public CustomUsernamePasswordAuthentication(String name, PrincipalFactory principalFactory, Integer order) {
        super(name, principalFactory, order);
    }
    @Override
    protected AuthenticationHandlerExecutionResult authenticateUsernamePasswordInternal(UsernamePasswordCredential usernamePasswordCredential, String s) throws Throwable {
        String username = usernamePasswordCredential.getUsername();
        String password = Arrays.toString(usernamePasswordCredential.getPassword());
        log.info("username : " + username);
        log.info("password : " + password);
        // JDBC模板依赖于连接池来获得数据的连接，所以必须先要构造连接池
        SysUserDto sysUserDto = DataService.getInstance().getByUsernameNotNull(username);
        if (sysUserDto == null) {
            throw new AuthenticationException("用户名不存在：" + username);
        }
        log.info("database username : " + sysUserDto.getUsername());
        log.info("database password : " + sysUserDto.getPassword());
        if (!sysUserDto.getPassword().equals(password)) {
            throw new FailedLoginException("Sorry, password not correct!");
        } else {
            // 可自定义返回给客户端的多个属性信息
            HashMap<String, List<Object>> returnInfo = HashMap.newHashMap(1);
            returnInfo.put("unitName", Lists.newArrayList(sysUserDto.getUnitName()));
            final List<MessageDescriptor> list = new ArrayList<>();
            return createHandlerResult(usernamePasswordCredential,
                    this.principalFactory.createPrincipal(username, returnInfo), list);
        }
    }
}
