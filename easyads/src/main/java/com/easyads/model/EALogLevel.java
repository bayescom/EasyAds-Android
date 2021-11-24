package com.easyads.model;

public enum EALogLevel {
    /**
     * 可以自定义级别
     */
    CUSTOM(-1),
    /**
     * 不打印任何log
     */
    NONE(0),
    /**
     * 默认等级，等于SIMPLE等级，打印SDK核心方法日志信息
     */
    DEFAULT(1),
    /**
     * 基础等级，打印SDK核心方法日志信息，方便普通的接入者查看接入效果
     */
    SIMPLE(2),
    /**
     * 高级模式，可打印一些辅助判断的执行信息，方便排查问题
     */
    HIGH(3),
    /**
     * 最大等级，可打印全部信息，比较详细的看到SDK执行信息，用来定位错误信息
     */
    MAX(10),
    ;


    EALogLevel(int ni) {
        level = ni;
    }

    public int level; //日志级别
}
