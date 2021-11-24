package com.easyads.core.nati;

import com.easyads.itf.EABaseADListener;

public interface EANativeExpressListener extends EABaseADListener {
    void onAdRenderFailed();

    void onAdRenderSuccess();
}
