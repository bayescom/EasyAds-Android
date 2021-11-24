package com.easyads.model;

import com.easyads.EasyAdsConstant;
import com.easyads.utils.BigDecimalUtil;
import com.easyads.utils.EALog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 所有策略信息model，包含所有的广告渠道信息和加载策略
 */
public class StrategyModel implements Serializable {
    public HashMap<Integer, SdkSupplier> supplierMap = new HashMap<>();
    public ArrayList<SdkRule> rules = new ArrayList<>();
    public double sumPercent = 0;//概率值总和


    /**
     * 将约定格式的json结构转换为当前model，解析成可执行的策略信息，用于后续策略执行、调度
     *
     * @param json 一定要完全参考demo示例中的json格式
     * @return SDK可执行的策略信息
     */
    public static StrategyModel covert(String json) {
        StrategyModel strategyModel = new StrategyModel();
        try {
            JSONObject result = new JSONObject(json);

            JSONArray suppliers = result.getJSONArray("suppliers");
            JSONArray setting = result.getJSONArray("rules");

            strategyModel.rules = new ArrayList<>();
            strategyModel.supplierMap = new HashMap<>();

            for (int i = 0; i < suppliers.length(); i++) {
                SdkSupplier supplier = new SdkSupplier();
                JSONObject impObject = suppliers.getJSONObject(i);
                supplier.tag = impObject.optString("tag");
                supplier.adspotId = impObject.optString("adspotid");
                supplier.appId = impObject.optString("appid");
                supplier.index = impObject.optInt("index");
                strategyModel.supplierMap.put(supplier.index, supplier);
            }

            for (int i = 0; i < setting.length(); i++) {
                SdkRule rule = new SdkRule();
                JSONObject impObject = setting.getJSONObject(i);
                rule.tag = impObject.optString("tag");
                rule.percent = impObject.optDouble("percent", EasyAdsConstant.DEFAULT_PERCENT);
                //累加百分值
                strategyModel.sumPercent = BigDecimalUtil.add(strategyModel.sumPercent, rule.percent);

                JSONArray sortList = impObject.optJSONArray("sort");
                if (sortList != null) {
                    rule.sort = new ArrayList<>();
                    //存放排好序后的广告组
                    rule.sortedSuppliers = new ArrayList<>();

                    //依次查找sort中元素
                    for (int j = 0; j < sortList.length(); j++) {
                        int index = sortList.optInt(j);
                        rule.sort.add(index);

                        //从已有的渠道map中找到对应index的渠道，依次添加至排序列表中
                        SdkSupplier sourceSupplier = strategyModel.supplierMap.get(index);
                        if (sourceSupplier != null) {
                            //需要新建对象，不可直接使用sourceSupplier内容，否则如果相同的index在不同的rule组时，会出现引用源错乱问题。
                            SdkSupplier supplier = new SdkSupplier();
                            //赋值优先级，按照for循环执行顺序，起始值1
                            supplier.priority = j + 1;
                            supplier.ruleTag = rule.tag;

                            //需要依次把对应值重新赋值
                            supplier.tag = sourceSupplier.tag;
                            supplier.adspotId = sourceSupplier.adspotId;
                            supplier.appId = sourceSupplier.appId;
                            supplier.index = sourceSupplier.index;
                            supplier.versionTag = sourceSupplier.versionTag;
                            EALog.max("[StrategyModel_covert] 已完成解析的渠道：" + supplier.toString());
                            rule.sortedSuppliers.add(supplier);
                        }
                    }
                }
                strategyModel.rules.add(rule);
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
        return strategyModel;
    }
}
