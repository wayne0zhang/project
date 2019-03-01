package com.paxing.test.kaoqin.utils;

/**
 * 二进制实现加减乘除
 * <p>
 * ref:https://www.cnblogs.com/paxing/p/10452264.html
 *
 * @author wtzhang (zhangwentao001@lianjia.com)
 * @summary
 * @since 2019/2/28
 */
public class BinaryCalculator {
    /**
     * 加法：递归写法
     *
     * @param a
     * @param b
     * @return
     */
    public static int add(int a, int b) {
        if (b == 0) {
            return a;
        }
        // 不考虑进位
        int sum = a ^ b;
        int carry = (a & b) << 1;
        return add(sum, carry);
    }

    /**
     * 加法：迭代写法
     *
     * @param a
     * @param b
     * @return
     */
    public static int addCircle(int a, int b) {
        int sum = a ^ b;
        int carry = (a & b) << 1;
        while (carry != 0) {
            int x = sum;
            int y = carry;
            sum = x ^ y;
            carry = (x & y) << 1;
        }
        return sum;
    }

    /**
     * 减法：加一个负数
     *
     * @param a
     * @param b
     * @return
     */
    public static int sub(int a, int b) {
        int reverseB = add(~b, 1);
        return add(a, reverseB);
    }

    /**
     * 加法：
     * 被乘数低位为1，加乘数
     * 为0，不加
     *
     * @param a
     * @param b
     * @return
     */
    public static int multiply(int a, int b) {
        // 取绝对值
        int multiplicand = a < 0 ? add(~a, 1) : a;
        int multiplier = b < 0 ? add(~b, 1) : b;

        int result = 0;
        while (multiplier > 0) {
            if ((multiplier & 0x1) > 0) {
                result = add(result, multiplicand);
            }
            multiplier = multiplier >> 1;
            multiplicand = multiplicand << 1;
        }
        if ((a ^ b) < 0) {
            result = add(~result, 1);
        }
        return result;
    }


    /**
     * 除法：不是很懂哦
     *
     * @param a
     * @param b
     * @return
     */
    public static int divide(int a, int b) {
        // 先取被除数和除数的绝对值
        int dividend = a > 0 ? a : add(~a, 1);
        int divisor = b > 0 ? a : add(~b, 1);
        int quotient = 0;// 商
        int remainder = 0;// 余数
        for (int i = 31; i >= 0; i--) {
            //比较dividend是否大于divisor的(1<<i)次方，不要将dividend与(divisor<<i)比较，而是用(dividend>>i)与divisor比较，
            //效果一样，但是可以避免因(divisor<<i)操作可能导致的溢出，如果溢出则会可能dividend本身小于divisor，但是溢出导致dividend大于divisor
            if ((dividend >> i) >= divisor) {
                quotient = add(quotient, 1 << i);
                dividend = sub(dividend, divisor << i);
            }
        }
        // 确定商的符号
        if ((a ^ b) < 0) {
            // 如果除数和被除数异号，则商为负数
            quotient = add(~quotient, 1);
        }
        // 确定余数符号
        remainder = b > 0 ? dividend : add(~dividend, 1);
        return quotient;// 返回商
    }
}
