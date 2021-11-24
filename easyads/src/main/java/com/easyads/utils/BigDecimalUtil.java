package com.easyads.utils;

import java.math.BigDecimal;

public class BigDecimalUtil {
    /**
     * 提供精确加法计算的add方法
     *
     * @param value1 被加数
     * @param value2 加数
     * @return 两个参数的和
     */
    public static double add(double value1, double value2) {
        BigDecimal b1 = new BigDecimal(value1);
        BigDecimal b2 = new BigDecimal(value2);
        return b1.add(b2).doubleValue();
    }

    /**
     * 提供精确减法运算的sub方法
     *
     * @param value1 被减数
     * @param value2 减数
     * @return 两个参数的差
     */
    public static double sub(double value1, double value2) {
        BigDecimal b1 = new BigDecimal(value1);
        BigDecimal b2 = new BigDecimal(value2);
        return b1.subtract(b2).doubleValue();
    }

    /**
     * 提供精确乘法运算的mul方法
     *
     * @param value1 被乘数
     * @param value2 乘数
     * @return 两个参数的积，结果保留两位小数
     */
    public static double mul(double value1, double value2) {
        BigDecimal b1 = new BigDecimal(value1);
        BigDecimal b2 = new BigDecimal(value2);
        return b1.multiply(b2).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }


    public static double div(double value1, double value2) {
        return div(value1, value2, 0);
    }

    /**
     * 提供精确的除法运算方法div
     *
     * == value1（分子）除以value2（分母）
     *
     * @param value1 被除数
     * @param value2 除数
     * @param scale  精确范围
     * @return 两个参数的商
     */
    public static double div(double value1, double value2, int scale) {
        //如果精确范围小于0，抛出异常信息
        if (scale < 0) {
            scale = 0;
        }
        BigDecimal b1 = new BigDecimal(value1);
        BigDecimal b2 = new BigDecimal(value2);
        return b1.divide(b2, scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}