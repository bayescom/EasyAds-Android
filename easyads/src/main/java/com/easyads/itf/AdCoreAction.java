package com.easyads.itf;

/**
 * 广告加载核心调用方法，对外聚合调用和内部adapter调用，方法命名一致
 */
public interface AdCoreAction {
    void loadOnly();

    void show();

    void loadAndShow();

    void destroy();
}
