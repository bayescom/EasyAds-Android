package com.easyads.supplier.baidu;

import android.app.Activity;
import android.view.View;

import com.easyads.core.nati.EANativeExpressSetting;
import com.easyads.custom.EANativeExpressCustomAdapter;
import com.easyads.model.EasyAdError;
import com.easyads.utils.EALog;
import com.baidu.mobads.sdk.api.BaiduNativeManager;
import com.baidu.mobads.sdk.api.FeedNativeView;
import com.baidu.mobads.sdk.api.NativeResponse;
import com.baidu.mobads.sdk.api.RequestParameters;
import com.baidu.mobads.sdk.api.StyleParams;
import com.baidu.mobads.sdk.api.XAdNativeResponse;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

/**
 * 模板信息流 对应了百度的智能优选信息流广告位
 */
public class BDNativeExpressAdapter extends EANativeExpressCustomAdapter implements BaiduNativeManager.FeedAdListener, NativeResponse.AdInteractionListener {
    private EANativeExpressSetting setting;
    private RequestParameters parameters;
    NativeResponse nativeResponse = null;
    FeedNativeView feedNativeView;

    public BDNativeExpressAdapter(SoftReference<Activity> activity, EANativeExpressSetting baseSetting) {
        super(activity, baseSetting);
        setting = baseSetting;
        parameters = EasyBDManager.getInstance().nativeExpressParameters;
    }

    @Override
    protected void doLoadAD() {

        if (sdkSupplier != null) {
            BDUtil.initBDAccount(this);

            /**
             * Step 1. 创建BaiduNative对象，参数分别为： 上下文context，广告位ID
             * 注意：请将adPlaceId替换为自己的广告位ID
             * 注意：信息流广告对象，与广告位id一一对应，同一个对象可以多次发起请求
             */
            BaiduNativeManager mBaiduNativeManager = new BaiduNativeManager(getActivity(), sdkSupplier.adspotId);

            mBaiduNativeManager.loadFeedAd(parameters, this);
        }

    }


    @Override
    protected void doShowAD() {
        try {
            StyleParams styleParams;
            styleParams = EasyBDManager.getInstance().nativeExpressSmartStyle;

            if (nativeResponse != null) {
                feedNativeView.setAdData((XAdNativeResponse) nativeResponse);
                if (styleParams != null) {
                    feedNativeView.changeViewLayoutParams(styleParams);
                }
                feedNativeView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nativeResponse.unionLogoClick();
                    }
                });
                if (nativeResponse != null) {
                    EALog.max("getImageUrl = " + nativeResponse.getImageUrl());
                    XAdNativeResponse xad = (XAdNativeResponse) nativeResponse;
                    xad.setAdDislikeListener(new NativeResponse.AdDislikeListener() {
                        @Override
                        public void onDislikeClick() {
                            try {
                                onADClose();
                                // 点击了负反馈渠道的回调
                                removeADView();

                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                    });

                    /**
                     * registerViewForInteraction()与BaiduNativeManager配套使用
                     * 警告：调用该函数来发送展现，勿漏！
                     */

                    List<View> clickViews = new ArrayList<>();
                    List<View> creativeViews = new ArrayList<>();
                    nativeResponse.registerViewForInteraction(setting.getAdContainer(),clickViews, creativeViews,this);

                    nativeResponse.setAdPrivacyListener(new NativeResponse.AdPrivacyListener() {
                        @Override
                        public void onADPermissionShow() {
                            EALog.high(TAG + "onADPermissionShow");

                        }

                        @Override
                        public void onADPermissionClose() {
                            EALog.high(TAG + "onADPermissionClose");
                        }

                        @Override
                        public void onADPrivacyClick() {
                            EALog.high(TAG + "onADPrivacyClick");
                        }
                    });

                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doDestroy() {
    }

    @Override
    public void onNativeLoad(List<NativeResponse> list) {
        EALog.high(TAG + "onNativeLoad");
        try {
            if (list == null || list.size() == 0) {
                handleFailed(EasyAdError.ERROR_DATA_NULL, "");
            } else {
                nativeResponse = list.get(0);

                feedNativeView = new FeedNativeView(getActivity());
                addADView(feedNativeView);
                setNEView(feedNativeView);
                handleSucceed();
            }
        } catch (Throwable e) {
            e.printStackTrace();
            handleFailed(EasyAdError.parseErr(EasyAdError.ERROR_EXCEPTION_LOAD));
        }
    }

    @Override
    public void onNativeFail(int i, String s) {
        handleFailed(i + "", s);
    }

    @Override
    public void onNoAd(int i, String s) {
        handleFailed(i + "", s);
    }


    @Override
    public void onVideoDownloadSuccess() {
        EALog.high(TAG + "onVideoDownloadSuccess");

    }

    @Override
    public void onVideoDownloadFailed() {
        EALog.e(TAG + "onVideoDownloadFailed");

    }

    @Override
    public void onLpClosed() {
        EALog.high(TAG + "onLpClosed");

    }


    public void onADClose() {
        EALog.high(TAG + "onADClose");
        if (null != setting) {
            setting.adapterDidClosed(sdkSupplier);
        }
    }


    @Override
    public void onAdClick() {
        handleClick();

        String title = "";
        if (nativeResponse != null) {
            title = nativeResponse.getTitle();
        }
        EALog.high(TAG + "onAdClick: title = " + title);
    }

    @Override
    public void onADExposed() {
        handleExposure();

        String title = "";
        if (nativeResponse != null) {
            title = nativeResponse.getTitle();
        }
        EALog.high(TAG + "onADExposed: title = " + title);
    }

    @Override
    public void onADExposureFailed(int i) {
        EALog.high(TAG + "onADExposureFailed , inf = " + i);

        handleFailed(i, "onADExposureFailed");

    }

    @Override
    public void onADStatusChanged() {
        EALog.high(TAG + "onADStatusChanged:" + getBtnText(nativeResponse));

    }

    @Override
    public void onAdUnionClick() {
        EALog.high(TAG + "onADUnionClick");

        handleClick();
    }

    // 下载状态及下载的进度
    private String getBtnText(NativeResponse nrAd) {
        if (nrAd == null) {
            return "";
        }
        try {
            if (nrAd.isNeedDownloadApp()) {
                int status = nrAd.getDownloadStatus();
                if (status >= 0 && status <= 100) {
                    return "下载中：" + status + "%";
                } else if (status == 101) {
                    return "点击安装";
                } else if (status == 102) {
                    return "继续下载";
                } else if (status == 103) {
                    return "点击启动";
                } else if (status == 104) {
                    return "重新下载";
                } else {
                    return "点击下载";
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return "查看详情";
    }
}
