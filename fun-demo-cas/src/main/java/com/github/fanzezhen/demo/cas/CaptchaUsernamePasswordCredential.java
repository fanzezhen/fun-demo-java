package com.github.fanzezhen.demo.cas;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.authentication.credential.UsernamePasswordCredential;

import javax.validation.constraints.Size;

/**
 * @author zezhen.fan
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public class CaptchaUsernamePasswordCredential extends UsernamePasswordCredential {
    private static final long serialVersionUID = -4166149641561667276L;
    @Size(min = 6, max = 6, message = "required.captcha")
    private String captcha;
}
