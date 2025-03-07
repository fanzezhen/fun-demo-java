package com.github.fanzezhen.webmagic.qidian;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.CharSequenceUtil;
import com.github.fanzezhen.webmagic.CommonUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.processor.PageProcessor;

import java.util.List;

/**
 * 起点
 *
 * @author fanzezhen
 * @createTime 2023/12/29 14:48
 * @since 3.0
 */
public class QiPageProcessor implements PageProcessor {
    Logger log = LoggerFactory.getLogger(QiPageProcessor.class);

    public static final String URL_DOMAIN = "https://www.qidian.com";
    public static final String URL_PATTERN = "https://www.qidian.com/chapter/1016572786/498215647/";

    public QiPageProcessor() {
        CommonUtil.setDefaultSite(getSite(), URL_DOMAIN);
        for (String cookie : ResourceUtil.readUtf8Str("cookies/qi-cookie.txt").split("[\r\n,]")) {
            if (CharSequenceUtil.isBlank(cookie)){
                continue;
            }
            String[] strings = cookie.split(CharSequenceUtil.SPACE);
            if (strings.length==2) {
                getSite().addCookie(strings[0], strings[1]);
            }
        }
    }

    @Override
    public void process(Page page) {
        // 在这里编写解析和处理页面的代码，例如提取链接等。
        List<String> names = page.getHtml().xpath("//*[@id=\"reader-content\"]/div[2]/div/div[2]/div/h1/text()").all();
        for (String name : names) {
            page.addTargetRequest(name); // 解析出来的链接，将放到待抓取队列中。
        }
        page.putField("names", names); // 将解析出来的数据放入结果中。
    }
}
