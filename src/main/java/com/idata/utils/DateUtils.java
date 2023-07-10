package com.idata.utils;

import cn.hutool.core.date.DateUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.sql.Date;

/**
 * @description: some desc
 * @author: xiehaotian
 * @date: 2023/5/29 9:47
 */
public class DateUtils {

    /**
     * 字符串时间转化类型(年月日)
     * @param s
     * @return
     */
    public static java.util.Date strToTs(String s) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        java.util.Date date =sdf.parse(s);
        return date;
    }

    /**
     * 字符串时间转化类型(年月日时分)
     * @param s
     * @return
     */
    public static java.util.Date strToTsSF(String s) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmm");
        java.util.Date date = null;
        try {
            date = sdf.parse(s);
        } catch (ParseException e) {
            System.out.println("sb======================="+s);
            throw new RuntimeException(e);
        }
        return date;
    }

    /**
     * 字符串时间转化类型(年月日时分秒)
     * @param s
     * @return
     */
    public static java.util.Date strToTsSFM(String s) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        java.util.Date date = null;
        try {
            date = sdf.parse(s);
        } catch (ParseException e) {
            System.out.println("sb======================="+s);
            throw new RuntimeException(e);
        }
        return date;
    }

    public static void main(String[] args) {
        String s = "201809201534";
        if (s.length() == 12){
            String s1 = "00";
            s=s+ s1;
        }
        Date date = DateUtil.parse(s).toSqlDate();
        System.out.println(date);
    }
}
