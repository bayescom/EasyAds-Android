# 开屏广告

### 集成最佳实践建议：

- 理论上开屏广告应该在**用户阅读并同意隐私政策授权之后**展示。
- 展示区域最好**大于整体画面 75%且高度不得低于400dp**，全屏效果更佳；
- 开屏请求、展示时机：**使用单独的页面展示开屏**，并且加载广告时尽量不要进行其他的网络请求或耗时操作，这类操作可以在广告曝光或加载失败回调事件以后再进行。
- 开屏**冷启动和热启动**区分设置策略json。

### 代码示例

开屏页面调用

```
        //初始化广告处理封装类
        EasyADController ad = new EasyADController(this);
        ad.loadSplash(jsonName, adContainer, logo, true, new EasyADController.SplashCallBack() {
            @Override
            public void jumpMain() {
                goToMainActivity();
            }
        });

```

EasyADController中相关处理代码：

```
    /**
     * 加载开屏广告
     *
     * @param adContainer    广告承载布局，不可为空
     * @param logoContainer  底部logo布局，可以为空
     * @param singleActivity 是否为单独activity中展示开屏广告
     * @param callBack       跳转回调，在回调中进行跳转主页或其他操作
     */
    public void loadSplash(String jsonFileName, final ViewGroup adContainer, final ViewGroup logoContainer, boolean singleActivity, final SplashCallBack callBack) {
        //必须：设置开屏核心回调事件的监听器。
        EASplashListener listener = new EASplashListener() {

            @Override
            public void onAdClose() {
                if (callBack != null)
                    callBack.jumpMain();

                logAndToast(mActivity, "广告关闭");
            }

            @Override
            public void onAdSucceed() {
                logAndToast(mActivity, "广告加载成功");
                if (EasyAdsConstant.SDK_TAG_CSJ.equals(baseAD.getSupplierInf().tag)) {
                    if (logoContainer != null)
                        logoContainer.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onAdExposure() {
                //设置开屏父布局背景色为白色
                if (adContainer != null)
                    adContainer.setBackgroundColor(Color.WHITE);
                //logo展示建议：广告展示的时候再展示logo，其他时刻都是展示的全屏的background图片
                if (logoContainer != null)
                    logoContainer.setVisibility(View.VISIBLE);


                logAndToast(mActivity, "广告展示成功");
            }

            @Override
            public void onAdFailed(EasyAdError error) {
                logAndToast(mActivity, "广告加载失败 code=" + error.code + " msg=" + error.msg);
            }

            @Override
            public void onAdClicked() {
                logAndToast(mActivity, "广告点击");
            }


        };
        EasyAdSplash easySplash = new EasyAdSplash(mActivity, adContainer, listener);
        baseAD = easySplash;
        //注意：如果开屏页是fragment或者dialog实现，这里需要置为false。默认为true，代表开屏和首页为两个不同的activity
        easySplash.setShowInSingleActivity(singleActivity);
        //必须：设置策略信息
        easySplash.setData(FileUtil.getJson(mActivity, jsonFileName));
        //必须：请求并展示广告
        easySplash.loadAndShow();
        logAndToast(mActivity, "广告请求中");
    }

    /**
     * 开屏跳转回调
     */
    public interface SplashCallBack {
        void jumpMain();
    }

```

### 主要API

* EasyAdSplash

|方法名 | 方法介绍
|:------------- |:---------------|  
|EasyAdSplash(Activity activity, ViewGroup adContainer, EASplashListener listener) | 构造方法，需要传入广告载体布局adContainer|
|setCsjShowAsExpress(boolean isExpress) |设置穿山甲为模板广告类型请求广告（需在穿山甲后台申请），默认为非模板类型
|setShowInSingleActivity(boolean single) |开屏是否在单独的activity中，默认true。
|isADSkip | 当回调了onAdClose事件时，可以获取此值来区分是否为用户点击了跳过
|addCustomSupplier(String sdkTag, EABaseSupplierAdapter adapter) | 通用方法，添加自定义SDK的适配器
|getSupplierInf() | 通用方法，获取当前正在执行的渠道信息，可以在Listener的任意事件回调中获取，方便统计执行情况。
|getReqId() |通用方法，获取当前策略执行的唯一id，方便统计
|setData(String strategyJson)|通用方法，设置策略执行数据，为固定格式的json字符串
|loadOnly() |通用方法，仅拉取广告
|show() | 通用方法，展示广告，和loadOnly()方法搭配使用
|loadAndShow() | 通用方法，拉取并展示广告
|destroy()|通用方法， 销毁广告


* EASplashListener


|方法名 | 方法介绍
|:------------- |:---------------|  
|onAdSucceed() |通用回调方法，成功加载到广告
|onAdExposure() |通用回调方法，广告曝光
|onAdClicked() |通用回调方法，广告点击
|onAdClose() |通用回调方法，广告关闭
|onAdFailed(EasyAdError easyAdError) |通用回调方法，广告失败，easyAdError包含了具体的失败原因，easyAdError可能为null
 