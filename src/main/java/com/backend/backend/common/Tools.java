package com.backend.backend.common;

import org.springframework.util.DigestUtils;

import java.util.*;

/**
 * @Author: goodtimp
 * @Date: 2019/10/9 16:26
 * @description : 某些工具类
 */
public class Tools {
    /**
     * Description:  获取GMT8时间
     *
     * @return 将当前时间转换为GMT8时区后的Date
     */
    public static Date getGMT8Time() {
        Date gmt8;
        try {
            Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("GMT+8"), Locale.CHINESE);
            Calendar day = Calendar.getInstance();
            day.set(Calendar.YEAR, cal.get(Calendar.YEAR));
            day.set(Calendar.MONTH, cal.get(Calendar.MONTH));
            day.set(Calendar.DATE, cal.get(Calendar.DATE));
            day.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
            day.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
            day.set(Calendar.SECOND, cal.get(Calendar.SECOND));
            gmt8 = day.getTime();
        } catch (Exception e) {
            System.out.println("获取GMT8时间  getGMT8Time()  error  !");
            e.printStackTrace();
            gmt8 = null;
        }
        return gmt8;
    }

    /**
     * 产生随机字符串
     *
     * @param length 长度
     * @return
     */
    public static String getRandomString(int length) {
        String str = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(62);
            sb.append(str.charAt(number));
        }
        return sb.toString();
    }


    /**
     * 字符串加盐后进行Md5加密 指定length长度
     *
     * @param str
     * @param length
     * @return
     */
    public static String dataAddSaltAndMD5(String str, int length) {
        String saltStr = str + getRandomString(length);
        return DigestUtils.md5DigestAsHex(saltStr.getBytes());
    }

    /*=====================    author ipso     =====================*/

    /**
     * 生成随机数字
     * 用来生成手机验证码
     * @author ipso
     * @param num 生成数字的数量
     * @return 随机数字字符串
     */
    public static String createRandomNum(int num){
        String randomNumStr = "";
        for(int i = 0; i < num;i ++){
            int randomNum = (int)Math.floor(Math.random() * 10);
            randomNumStr += randomNum;
        }
        return randomNumStr;
    }
}
