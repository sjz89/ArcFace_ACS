package com.daylight.arcface_acs.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.daylight.arcface_acs.Values;


/**
 * SharedPreferences工具类
 * Created by Daylight on 2018/1/14.
 */

public class SharedPreferencesUtil {
    public static void setAccount(Context context,String account){
        SharedPreferences.Editor spe=context.getSharedPreferences(Values.USER,Context.MODE_PRIVATE).edit();
        spe.putString("account",account);
        spe.apply();
    }
    public static String getAccount(Context context){
        SharedPreferences sp=context.getSharedPreferences(Values.USER,Context.MODE_PRIVATE);
        return sp.getString("account","");
    }
    public static void setPassword(Context context,String password){
        SharedPreferences.Editor spe=context.getSharedPreferences(Values.USER,Context.MODE_PRIVATE).edit();
        spe.putString("password",password);
        spe.apply();
    }
    public static String getPassword(Context context){
        SharedPreferences sp=context.getSharedPreferences(Values.USER,Context.MODE_PRIVATE);
        return sp.getString("password","");
    }
    public static void resetCount(Context context){
        SharedPreferences.Editor spe=context.getSharedPreferences(Values.NUMBER,Context.MODE_PRIVATE).edit();
        spe.putInt("count",5);
        spe.apply();
    }
    public static void SubCount(Context context){
        SharedPreferences.Editor spe=context.getSharedPreferences(Values.NUMBER,Context.MODE_PRIVATE).edit();
        SharedPreferences sp=context.getSharedPreferences(Values.NUMBER,Context.MODE_PRIVATE);
        int count=sp.getInt("count",5);
        count--;
        spe.putInt("count",count);
        spe.apply();
    }
    public static int getCount(Context context){
        SharedPreferences sp=context.getSharedPreferences(Values.NUMBER,Context.MODE_PRIVATE);
        return sp.getInt("count",-1);
    }
    public static void setPin(Context context,String pin){
        SharedPreferences.Editor spe=context.getSharedPreferences(Values.USER,Context.MODE_PRIVATE).edit();
        spe.putString("pin",pin);
        spe.apply();
    }
    public static String getPin(Context context){
        SharedPreferences sp=context.getSharedPreferences(Values.USER,Context.MODE_PRIVATE);
        return sp.getString("pin","");
    }
}
