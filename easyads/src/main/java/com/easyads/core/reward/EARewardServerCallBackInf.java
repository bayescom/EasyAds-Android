package com.easyads.core.reward;

import java.util.Map;

public class EARewardServerCallBackInf {
    public boolean bdRewardVerify;
    public Map<String, Object> ylhRewardMap;

    public CsjRewardInf csjInf;

      public static class CsjRewardInf {
        public boolean rewardVerify;
        public int rewardAmount;
        public String rewardName;
        public int errorCode;
        public String errMsg;
    }
}
