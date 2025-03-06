package com.github.fanzezhen.demo.cas.action;

import cn.hutool.core.util.StrUtil;
import com.github.fanzezhen.demo.cas.CaptchaUsernamePasswordCredential;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.web.support.WebUtils;
import org.springframework.binding.message.MessageBuilder;
import org.springframework.binding.message.MessageContext;
import org.springframework.webflow.action.AbstractAction;
import org.springframework.webflow.execution.Event;
import org.springframework.webflow.execution.RequestContext;

/**
 * @author anumbrella
 */
@Slf4j
public class ValidateLoginAction extends AbstractAction {

    /**
     * 是否开启验证码
     *
     * @return boolean
     */
    private boolean isEnable() {
        return true;
    }


    @Override
    protected Event doExecute(RequestContext context) {
        CaptchaUsernamePasswordCredential credential = (CaptchaUsernamePasswordCredential) WebUtils.getCredential(context);
        // 系统信息不为空才检测校验码
        if (credential != null) {
            String captcha = credential.getCaptcha();
            if (StrUtil.isEmpty(captcha)) {
                return getError(context, "验证码不能为空！");
            }
        }
        return null;
    }

    /**
     * 跳转到错误页
     *
     * @param requestContext RequestContext
     * @return Event
     */
    private Event getError(final RequestContext requestContext, String code) {
        final MessageContext messageContext = requestContext.getMessageContext();
        messageContext.addMessage(new MessageBuilder().error().code(code).build());
        return getEventFactorySupport().event(this, code);
    }


}
