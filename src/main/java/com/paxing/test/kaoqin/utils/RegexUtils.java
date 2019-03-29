package com.paxing.test.kaoqin.utils;

import java.util.regex.Pattern;

/**
 * ClassName: RegexUtils <br/>
 * Function: <br/>
 * date: 2014年08月11日 13:57 <br/>
 *
 * @author fengyue
 * @since JDK 1.7
 */
public final class RegexUtils {

    /**
     * 验证Email
     *
     * @param email email地址，格式：zhangsan@sina.com，zhangsan@xxx.com.cn，xxx代表邮件服务商
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkEmail(String email) {
        String regex = "\\w+@\\w+\\.[a-z]+(\\.[a-z]+)?";
        return Pattern.matches(regex, email);
    }


    /**
     * 验证系统号
     * @param empId
     * @return
     */
    public static boolean checkEmpId(String empId) {
        String regex = "^([1-9]{8}|[1-9]{10})$";
        return Pattern.matches(regex, empId);
    }

    /**
     * 验证身份证号码
     *
     * @param idCard 居民身份证号码18位，最后一位可能是数字或字母
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkIdCard(String idCard) {
        String regex = "^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$";
        return Pattern.matches(regex, idCard);
    }

    /**
     * 验证银行卡号
     * @param AccountNo
     * @return
     */
    public static boolean checkAccoutNo(String AccountNo) {
        String regex = "^([1-9]{1})(\\d{14}|\\d{15}|\\d{18})$";
        return Pattern.matches(regex, AccountNo);
    }

    /**
     * 验证手机号码（支持国际格式，+86135xxxx...（中国内地），+00852137xxxx...（中国香港））
     *
     * @param mobilePhone 移动、联通、电信运营商的号码段
     *                    <p>移动的号段：134(0-8)、135、136、137、138、139、147（预计用于TD上网卡）
     *                    、150、151、152、157（TD专用）、158、159、187（未启用）、188（TD专用）</p>
     *                    <p>联通的号段：130、131、132、155、156（世界风专用）、185、186（3g）</p>
     *                    <p>电信的号段：133、153、170、180（未启用）、189</p>
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkMobilePhone(String mobilePhone) {
//        String regex = "(\\+\\d+)?1[34578]\\d{9}$";
//        String regex = "(\\+*\\d+)?([1][3-8]\\d{9}$|([6|9|5])\\d{7}$)";
        String regex = "(\\+?86)?[1][3-9]\\d{9}$|(\\+?(00)?852)?([6|9|5])\\d{7}$";


        return Pattern.matches(regex, mobilePhone);
    }

    /**
     * 验证固定电话号码
     *
     * @param telephone 电话号码，格式：国家（地区）电话代码 + 区号（城市代码） + 电话号码，如：+8602085588447
     *                  <p><b>国家（地区） 代码 ：</b>标识电话号码的国家（地区）的标准国家（地区）代码。它包含从 0 到 9 的一位或多位数字，
     *                  数字之后是空格分隔的国家（地区）代码。</p>
     *                  <p><b>区号（城市代码）：</b>这可能包含一个或多个从 0 到 9 的数字，地区或城市代码放在圆括号——
     *                  对不使用地区或城市代码的国家（地区），则省略该组件。</p>
     *                  <p><b>电话号码：</b>这包含从 0 到 9 的一个或多个数字 </p>
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkTelephone(String telephone) {
        String regex = "((\\+?\\d+)?(\\d{3,4}\\-?)?\\d{7,8}$)|(^400\\d{7}-\\d{4}$)";
        return Pattern.matches(regex, telephone);
    }
    
    /**
     * 验证整数（正整数和负整数）
     *
     * @param digit 一位或多位0-9之间的整数
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkDigit(String digit) {
        String regex = "\\-?[1-9]\\d+";
        return Pattern.matches(regex, digit);
    }

    /**
     * 验证整数和浮点数（正负整数和正负浮点数）*
     *
     * @param decimals 一位或多位0-9之间的浮点数，如：1.23，233.30
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkDecimals(String decimals) {
        String regex = "\\-?[1-9]\\d+(\\.\\d+)?";
        return Pattern.matches(regex, decimals);
    }

    /**
     * 验证空白字符
     *
     * @param blankSpace 空白字符，包括：空格、\t、\n、\r、\f、\x0B
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkBlankSpace(String blankSpace) {
        String regex = "\\s+";
        return Pattern.matches(regex, blankSpace);
    }

    /**
     * 验证中文
     *
     * @param chinese 中文字符
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkChinese(String chinese) {
        String regex = "^[\u4E00-\u9FA5]+$";
        return Pattern.matches(regex, chinese);
    }

    /**
     * 验证日期（年月日）
     *
     * @param birthday 日期，格式：1992-09-03，或1992.09.03
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkBirthday(String birthday) {
        String regex = "[1-9]{4}([-./])\\d{1,2}\\1\\d{1,2}";
        return Pattern.matches(regex, birthday);
    }

    /**
     * 验证URL地址
     *
     * @param url 格式：http://blog.csdn.net:80/xyang81/article/details/7705960? 或 http://www.csdn.net:80
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkURL(String url) {
        String regex = "(https?://(w{3}\\.)?)?\\w+\\.\\w+(\\.[a-zA-Z]+)*(:\\d{1,5})?(/\\w*)*(\\??(.+=.*)?(&.+=.*)?)?";
        return Pattern.matches(regex, url);
    }

    /**
     * 匹配中国邮政编码
     *
     * @param postcode 邮政编码
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkPostcode(String postcode) {
        String regex = "[1-9]\\d{5}";
        return Pattern.matches(regex, postcode);
    }

    /**
     * 匹配IP地址(简单匹配，格式，如：192.168.1.1，127.0.0.1，没有匹配IP段的大小)
     *
     * @param ipAddress IPv4标准地址
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkIpAddress(String ipAddress) {
        String regex = "[1-9](\\d{1,2})?\\.(0|([1-9](\\d{1,2})?))\\.(0|([1-9](\\d{1,2})?))\\.(0|([1-9](\\d{1,2})?))";
        return Pattern.matches(regex, ipAddress);
    }

    /**
     * 根据传入的参数进行正则校验
     * @param name
     * @param regex
     * @return 验证成功返回true，验证失败返回false
     */
    public static boolean checkByRegex(String name, String regex) {
        return Pattern.matches(regex, name);
    }

}
