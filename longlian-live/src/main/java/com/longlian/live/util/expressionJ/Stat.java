package com.longlian.live.util.expressionJ;

import java.util.Arrays;

/**
 * Created by Administrator on 2017/2/23.
 */
public class Stat implements Function {

    // 求和
    public double sum(double[] number) {
        double result = 0;
        for (double num : number) {
            result += num;
        }
        return result;
    }

    // 求平均值
    public double average(double[] number) {
        double result = sum(number);
        result = result / number.length;
        return result;
    }

    // 求最小值
    public double min(double[] number) {
        double result = number[0];
        for (double num : number) {
            if (num < result) {
                result = num;
            }
        }
        return result;
    }

    // 求最大值
    public double max(double[] number) {
        double result = number[0];
        for (double num : number) {
            if (num > result) {
                result = num;
            }
        }
        return result;
    }

    // 返回数值列表的长度
    public int count(double[] number) {
        return number.length;
    }

    // 返回大于scalar的数值列表
    public double[] gt(double[] number, double scalar) {
        double[] result = new double[number.length];
        int count = 0;
        for (int i = 0; i < number.length; i++) {
            double num = number[i];
            if (num > scalar) {
                result[i] = num;
                count += 1;
            }
        }
        return Arrays.copyOf(result, count);
    }

    // 返回小于scalar的数值列表
    public double[] lt(double[] number, double scalar) {
        double[] result = new double[number.length];
        int count = 0;
        for (int i = 0; i < number.length; i++) {
            double num = number[i];
            if (num < scalar) {
                result[i] = num;
                count += 1;
            }
        }
        return Arrays.copyOf(result, count);
    }

    // 返回等于scalar的数值列表
    public double[] eq(double[] number, double scalar) {
        double[] result = new double[number.length];
        int count = 0;
        for (int i = 0; i < number.length; i++) {
            double num = number[i];
            if (num == scalar) {
                result[i] = num;
                count += 1;
            }
        }
        return Arrays.copyOf(result, count);
    }

}
