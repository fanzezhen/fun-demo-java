package com.github.fanzezhen.demo.warm.flow;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson2.JSONObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Base64;
import java.util.Random;

/**
 * 验证码 Controller
 * 用于 RuoYi-Vue3 前端登录
 */
@RestController
public class SysCaptchaController {

    private final String CODE_KEY = "captcha_code";
    private Random random = new Random();

    @GetMapping("/captchaImage")
    public JSONObject captchaImage() throws IOException {
            int width = 120;
            int height = 40;
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            Graphics2D g = image.createGraphics();

            // 生成随机验证码
            String code = generateCode(4);

            // 画背景
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, width, height);

            // 画文字
            g.setFont(new Font("Arial", Font.BOLD, 28));
            g.setColor(new Color(30, 144, 255));
            g.drawString(code, 20, 30);

            // 干扰线
            for (int i = 0; i < 5; i++) {
                g.setColor(new Color(100, 100, 100));
                g.drawLine(
                    random.nextInt(width),
                    random.nextInt(height),
                    random.nextInt(width),
                    random.nextInt(height)
                );
            }

            // 转 Base64
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ImageIO.write(image, "png", os);
            String base64 = Base64.getEncoder().encodeToString(os.toByteArray());

            // 返回前端需要的结构（RuoYi 格式）
        JSONObject map = new JSONObject();
            map.put("img", base64);
            map.put("captchaEnabled", true);
            map.put("uuid", IdUtil.objectId());

           return map;
    }

    // 生成随机字母/数字
    private String generateCode(int len) {
        String chars = "ABCDEFGHJKLMNPQRSTWXY23456789";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < len; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
