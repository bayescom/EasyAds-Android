package com.easyads.core.full;

import com.easyads.itf.EABaseADListener;

public interface EAFullScreenVideoListener extends EABaseADListener {
    void onVideoComplete();

    void onVideoSkipped();

    void onVideoCached();
}
