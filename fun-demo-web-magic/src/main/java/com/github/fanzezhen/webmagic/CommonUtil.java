package com.github.fanzezhen.webmagic;

import us.codecraft.webmagic.Site;

/**
 * 工具类
 *
 * @author fanzezhen
 * @createTime 2023/12/29 17:12
 * @since 3.0
 */
public class CommonUtil {
    private CommonUtil() {
    }

    public static void setDefaultSite(Site site, String domain) {
        site.setSleepTime(5 * 1000)
                //添加cookie之前一定要先设置主机地址，否则cookie信息不生效
                .setDomain(domain)
                //添加请求头，有些网站会根据请求头判断该请求是由浏览器发起还是由爬虫发起的
                .addHeader("Referer", domain)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:52.0) Gecko/20100101 Firefox/52.0")
                .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .addHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
                .addHeader("Accept-Encoding", "gzip, deflate")
                .addHeader("Connection", "keep-alive")
                .addHeader("Upgrade-Insecure-Requests", "1")
                .addHeader("Cache-Control", "max-age=0")
        ;
    }
}
