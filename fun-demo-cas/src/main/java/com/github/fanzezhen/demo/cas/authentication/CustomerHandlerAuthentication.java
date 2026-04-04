package com.github.fanzezhen.demo.cas.authentication;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.github.fanzezhen.demo.cas.DataService;
import com.github.fanzezhen.demo.cas.CaptchaUsernamePasswordCredential;
import com.github.fanzezhen.demo.cas.exection.CheckCodeErrorException;
import com.github.fanzezhen.demo.cas.SecurityConstant;
import com.github.fanzezhen.demo.cas.model.SysUserDto;
import com.github.fanzezhen.fun.framework.core.model.ImageCode;
import com.github.fanzezhen.fun.framework.core.model.exception.ServiceException;
import com.github.fanzezhen.fun.framework.core.model.exception.enums.ExceptionCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.authentication.*;
import org.apereo.cas.authentication.handler.support.AbstractPreAndPostProcessingAuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.authentication.principal.Service;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.security.auth.login.FailedLoginException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author anumbrella
 */
@Slf4j
public class CustomerHandlerAuthentication extends AbstractPreAndPostProcessingAuthenticationHandler {

    public CustomerHandlerAuthentication(String name,  PrincipalFactory principalFactory, Integer order) {
        super(name, principalFactory, order);
    }

    @Override
    protected AuthenticationHandlerExecutionResult doAuthentication(Credential credential, Service service) throws Throwable {
        CaptchaUsernamePasswordCredential customCredential = (CaptchaUsernamePasswordCredential) credential;
        String username = customCredential.getUsername();
        String password = Arrays.toString(customCredential.getPassword());
        String captcha = customCredential.getCaptcha();
        log.info("username : " + username);
        log.info("password : " + password);
        if (CharSequenceUtil.isEmpty(username) || CharSequenceUtil.isEmpty(password) || CharSequenceUtil.isEmpty(captcha)) {
            throw new AuthenticationException("用户名、密码、验证码 不能为空");
        }
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (!(requestAttributes instanceof ServletRequestAttributes)) {
            throw new ServiceException(ExceptionCodeEnum.SERVICE_ERROR);
        }
        ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
        jakarta.servlet.http.HttpSession httpSession = attributes.getRequest().getSession();
        if (httpSession == null) {
            throw new ServiceException(ExceptionCodeEnum.SERVICE_ERROR);
        }
        Object captchaFromSession = httpSession.getAttribute(SecurityConstant.SESSION_KEY_CAPTCHA);
        if (!(captchaFromSession instanceof ImageCode)) {
            throw new CheckCodeErrorException();
        }
        ImageCode imageCode = (ImageCode) captchaFromSession;
        if (!captcha.equalsIgnoreCase(imageCode.getCode())) {
            throw new CheckCodeErrorException();
        }
        SysUserDto sysUserDto = DataService.getInstance().getByUsernameNotNull(username);
        if (sysUserDto == null) {
            throw new AuthenticationException("用户名不存在：" + username);
        }
        log.info("database username : " + sysUserDto.getUsername());
        log.info("database password : " + sysUserDto.getPassword());
        if (!BCrypt.checkpw(password, sysUserDto.getPassword())) {
            throw new FailedLoginException("Sorry, password not correct!");
        } else {
            final List<MessageDescriptor> list = new ArrayList<>();
            return createHandlerResult(customCredential,
                    this.principalFactory.createPrincipal(username, Collections.emptyMap()), list);
        }
    }

    @Override
    public boolean supports(Credential credential) {
        //判断传递过来的Credential 是否是自己能处理的类型
        return credential instanceof CaptchaUsernamePasswordCredential;
    }

}
