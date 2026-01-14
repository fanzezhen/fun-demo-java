package com.github.fanzezhen.demo.fun.data.elasticsearch7.enums;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 *
 */
@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum RegCapRangeEnum {

    /**
     * 0-10w
     */
    A("1", null, "1000"),

    /**
     * 10-100w
     */
    B("2", "1000", "10000"),

    /**
     * 100w-1000w
     */
    C("3", "10000", "100000"),

    /**
     * 1000w-5000w
     */
    D("4", "100000", "500000"),

    /**
     * 5000w以上
     */
    E("5", "500000", null);

    /**
     * 描述
     */
    private final String desc;
    /**
     * 起始值
     */
    private final String from;
    /**
     * 终止值
     */
    private final String to;

    /**
     * 根据名称匹配
     */
    public static Optional<RegCapRangeEnum> from(String name) {
        return Arrays.stream(RegCapRangeEnum.values()).filter(e -> e.name().equalsIgnoreCase(name))
            .findAny();
    }
}
