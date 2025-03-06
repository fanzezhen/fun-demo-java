package com.github.fanzezhen.demo.cas.controller;

import com.github.fanzezhen.fun.framework.core.constant.SecurityConstant;
import com.github.fanzezhen.fun.framework.core.model.bean.ImageCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.social.connect.web.HttpSessionSessionStrategy;
import org.springframework.social.connect.web.SessionStrategy;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.ServletWebRequest;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

/**
 * @author zezhen.fan
 */
@Controller("captcha-api")
@RequestMapping("/captcha")
public class CaptchaController {
    private final SessionStrategy sessionStrategy = new HttpSessionSessionStrategy();

    @GetMapping("/image-code")
    public void createCode(HttpServletRequest request, HttpServletResponse response) throws IOException {
        ImageCode imageCode = createImageCode();
        sessionStrategy.setAttribute(new ServletWebRequest(request), SecurityConstant.SESSION_KEY_CAPTCHA, imageCode);
        ImageIO.write(imageCode.getImage(), "jpeg", response.getOutputStream());
    }

    private ImageCode createImageCode() {
        // 验证码图片宽度
        int width = 100;
        // 验证码图片长度
        int height = 36;
        // 验证码位数
        int length = 4;
        // 验证码有效时间 60s
        int expireIn = 60;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);

        Graphics graphics = image.getGraphics();

        Random random = new Random();

        graphics.setColor(getRandColor(200, 500));
        graphics.fillRect(0, 0, width, height);
        graphics.setFont(new Font("Times New Roman", Font.ITALIC, 20));
        graphics.setColor(getRandColor(160, 200));
        int range = 155;
        for (int i = 0; i < range; i++) {
            int x = random.nextInt(width);
            int y = random.nextInt(height);
            int xl = random.nextInt(12);
            int yl = random.nextInt(12);
            graphics.drawLine(x, y, x + xl, y + yl);
        }
        StringBuilder sRand = new StringBuilder();
        for (int i = 0; i < length; i++) {
            String rand = String.valueOf(random.nextInt(10));
            sRand.append(rand);
            graphics.setColor(new Color(20 + random.nextInt(110), 20 + random.nextInt(110), 20 + random.nextInt(110)));
            graphics.drawString(rand, 13 * i + 6, 16);
        }

        graphics.dispose();

        return new ImageCode(image, sRand.toString(), expireIn);
    }

    private Color getRandColor(int fc, int bc) {
        int range = 255;
        Random random = new Random();
        if (fc > range) {
            fc = range;
        }

        if (bc > range) {
            bc = range;
        }
        int r = fc + random.nextInt(bc - fc);
        int g = fc + random.nextInt(bc - fc);
        int b = fc + random.nextInt(bc - fc);
        return new Color(r, g, b);
    }
}
