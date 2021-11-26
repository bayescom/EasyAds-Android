# EasyCsjManager

穿山甲个性化配置API方法介绍

| 参数名| 参数类型|	基本介绍|  
|:------------- |:---------------|:---------------|  
|csj_splashButtonType | int | 用于控制开屏广告的点击区域，[具体设置值及含义可参考此处](https://www.pangle.cn/support/doc/611f0f0c1b039f004611e4da)
|csj_downloadType | int | 用于控制下载APP前是否弹出二次确认弹窗(适用所有广告类型)。[具体设置值及含义可参考此处](https://www.pangle.cn/support/doc/611f0f0c1b039f004611e4da)
| csj_supportMultiProcess |  boolean | 是否支持多进程，默认true支持
|  csj_askPermission | boolean |    是否允许穿山甲SDK，在必要时进行权限询问，默认false不允许
|csj_directDownloadNetworkType | int[] | 下载类广告允许直接下载的网络状态，如果未设置下载状态集合，默认4g和wifi下可以下载。
