# EasyAds

| 方法名| 	方法介绍|  
|:------------- |:---------------|  
|getVersion() | 获取EasyAds SDK的版本号信息
|setDebug(boolean isDebug, EALogLevel logLevel) | 设置debug模式，用来调试是查看日志信息，日志tag标记为：`EasyAds-log` 。EALogLevel代表了日志等级，常用的Level如下：<br/>**EALogLevel.DEFAULT**:默认等级，等于SIMPLE等级，打印SDK核心方法日志信息<br/>**EALogLevel.SIMPLE**:基础等级，打印SDK核心方法日志信息 <br/>**EALogLevel.HIGH**:高级模式，可打印一些辅助判断的执行信息，方便排查问题 <br/>**EALogLevel.MAX**: 最大等级，可打印全部信息，比较详细的看到SDK执行信息，用来定位错误信息
|setSplashPlusAutoClose(int time)|   设置开屏v+ 小窗口自动关闭时间，单位毫秒，不设置使用默认各个渠道默认展示逻辑
