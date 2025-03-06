package com.github.fanzezhen.webmagic;

import com.github.fanzezhen.webmagic.qidian.QiPageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import us.codecraft.webmagic.Spider;

/**
 * 起点
 *
 * @author fanzezhen
 * @createTime 2023/12/29 17:01
 * @since 2.4.0
 */
@Slf4j
@Disabled
class QiTest {
    @Test
    @Disabled("以控制台交互的方式基于es生成java类")
    void test() {
        Spider mySpider = Spider.create(new QiPageProcessor()); // 创建爬虫实例并运行。注意：此URL需要替换为真实的URL。
        mySpider.addUrl("https://www.qidian.com/chapter/1016572786/498215647/");
        mySpider.run();
        Assertions.assertEquals(2, 1 + 1);
    }
}
