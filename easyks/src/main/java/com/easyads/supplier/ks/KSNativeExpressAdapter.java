package com.easyads.supplier.ks;

import android.app.Activity;
import android.support.annotation.Nullable;
import android.view.View;

import com.easyads.core.nati.EANativeExpressSetting;
import com.easyads.custom.EANativeExpressCustomAdapter;
import com.easyads.model.EasyAdError;
import com.easyads.utils.EALog;
import com.kwad.sdk.api.KsAdSDK;
import com.kwad.sdk.api.KsFeedAd;
import com.kwad.sdk.api.KsLoadManager;
import com.kwad.sdk.api.KsScene;

import java.lang.ref.SoftReference;
import java.util.List;

public class KSNativeExpressAdapter extends EANativeExpressCustomAdapter {
    public EANativeExpressSetting setting;
    KsFeedAd ad;

    public KSNativeExpressAdapter(SoftReference<Activity> activity, EANativeExpressSetting baseSetting) {
        super(activity, baseSetting);
        setting = baseSetting;
    }

    @Override
    protected void doLoadAD() {
        KSUtil.initAD(this, new KSUtil.InitListener() {
            @Override
            public void success() {
                //只有在成功初始化以后才能调用load方法，否则穿山甲会抛错导致无法进行广告展示
                startLoad();
            }

            @Override
            public void fail(String code, String msg) {
                handleFailed(code, msg);
            }
        });

    }

    private void startLoad() {
            int num = 1;
            KsScene scene = new KsScene.Builder(KSUtil.getADID(sdkSupplier)).adNum(num).build(); // 此为测试posId，请联系快手平台申请正式posId
            KsAdSDK.getLoadManager().loadConfigFeedAd(scene, new KsLoadManager.FeedAdListener() {
                @Override
                public void onError(int code, String msg) {
                    EALog.high(TAG + " onError ，" + code + msg);

                    handleFailed(code, msg);

                }

                @Override
                public void onFeedAdLoad(@Nullable List<KsFeedAd> list) {
                    EALog.high(TAG + " onFeedAdLoad");

                    try {
                        if (list == null || list.size() == 0 || list.get(0) == null) {
                            handleFailed(EasyAdError.ERROR_DATA_NULL, "");
                        } else {
                            ad = list.get(0);
                            setNEView(ad.getFeedView(getActivity()));
                            ad.setAdInteractionListener(new KsFeedAd.AdInteractionListener() {
                                @Override
                                public void onAdClicked() {

                                    EALog.high(TAG + " onAdClicked ");
                                    handleClick();
                                }

                                @Override
                                public void onAdShow() {
                                    EALog.high(TAG + " onAdShow ");

                                    handleExposure();
                                }

                                @Override
                                public void onDislikeClicked() {
                                    EALog.high(TAG + " onDislikeClicked ");

                                    if (setting != null) {
                                        setting.adapterDidClosed(sdkSupplier);
                                    }
                                    removeADView();
                                }

                                @Override
                                public void onDownloadTipsDialogShow() {
                                    EALog.high(TAG + " onDownloadTipsDialogShow ");

                                }

                                @Override
                                public void onDownloadTipsDialogDismiss() {
                                    EALog.high(TAG + " onDownloadTipsDialogDismiss ");

                                }
                            });

                            handleSucceed();
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                        handleFailed(EasyAdError.ERROR_EXCEPTION_LOAD, "");
                    }
                }
            });


        }


    @Override
    public void doDestroy() {

    }

    @Override
    protected void doShowAD() {
        View adv = ad.getFeedView(getActivity());
        addADView(adv);
    }
}
