package com.daylight.arcface_acs.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * 日期格式转化工具
 * Created by Daylight on 2018/1/29.
 */

public class DateUtil {
    public static String dateToStr(Date date) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return formatter.format(date);
    }
    public static String longToString(Long time){
        Date date=new Date(time);
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日 HH:mm:ss", Locale.getDefault());
        return format.format(date);
    }
    public static String autoTransFormat(Long time){
        Date date=new Date(time);
        Date now=new Date();
        SimpleDateFormat format1=new SimpleDateFormat("HH:mm",Locale.getDefault());
        SimpleDateFormat format2=new SimpleDateFormat("昨天",Locale.getDefault());
        SimpleDateFormat format3=new SimpleDateFormat("MM月dd日",Locale.getDefault());
        if (getDay(now)-getDay(date)==1){
            return format2.format(date);
        }else if (getDay(now)-getDay(date)>1
                || getMon(now) != getMon(date)
                || getYear(now) != getYear(date))
            return format3.format(date);
        return format1.format(date);
    }
    private static int getYear(Date date){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.YEAR);
    }
    private static int getMon(Date date){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.MONTH);
    }
    private static int getDay(Date date){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_MONTH);
    }
    public static int getWeek(Date date){
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK);
    }
}
