package com.github.fanzezhen.demo.cas.authentication;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import cn.stylefeng.roses.kernel.model.exception.ServiceException;
import cn.stylefeng.roses.kernel.model.exception.enums.CoreExceptionEnum;
import com.github.fanzezhen.demo.cas.DataService;
import com.github.fanzezhen.demo.cas.CaptchaUsernamePasswordCredential;
import com.github.fanzezhen.demo.cas.exection.CheckCodeErrorException;
import com.github.fanzezhen.fun.framework.core.constant.SecurityConstant;
import com.github.fanzezhen.fun.framework.core.model.bean.ImageCode;
import com.github.fanzezhen.fun.framework.mp.model.SysUserDto;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.authentication.*;
import org.apereo.cas.authentication.handler.support.AbstractPreAndPostProcessingAuthenticationHandler;
import org.apereo.cas.authentication.principal.PrincipalFactory;
import org.apereo.cas.authentication.principal.Service;
import org.apereo.cas.services.ServicesManager;
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

    public CustomerHandlerAuthentication(String name, ServicesManager servicesManager, PrincipalFactory principalFactory, Integer order) {
        super(name, servicesManager, principalFactory, order);
    }

    @Override
    protected AuthenticationHandlerExecutionResult doAuthentication(Credential credential, Service service) throws Throwable {
        CaptchaUsernamePasswordCredential customCredential = (CaptchaUsernamePasswordCredential) credential;
        String username = customCredential.getUsername();
        String password = Arrays.toString(customCredential.getPassword());
        String captcha = customCredential.getCaptcha();
        log.info("username : " + username);
        log.info("password : " + password);
        if (StrUtil.isEmpty(username) || CharSequenceUtil.isEmpty(password) || StrUtil.isEmpty(captcha)) {
            throw new AuthenticationException("用户名、密码、验证码 不能为空");
        }
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (!(requestAttributes instanceof ServletRequestAttributes)) {
            throw new ServiceException(CoreExceptionEnum.SERVICE_ERROR);
        }
        ServletRequestAttributes attributes = (ServletRequestAttributes) requestAttributes;
        jakarta.servlet.http.HttpSession httpSession = attributes.getRequest().getSession();
        if (httpSession == null) {
            throw new ServiceException(CoreExceptionEnum.SERVICE_ERROR);
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
