package com.github.fanzezhen.demo.cas.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 验证码 Session 存储对象
 * <p>
 * 只存储验证码文本和过期时间，不存储图片对象（BufferedImage 不可序列化）
 *
 * @author zezhen.fan
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CaptchaSession implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 验证码文本
     */
    private String code;

    /**
     * 过期时间
     */
    private LocalDateTime expireTime;

    /**
     * 检查验证码是否过期
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expireTime);
    }
}
