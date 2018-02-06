package com.daylight.arcface_acs.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.provider.Settings;

import java.security.NoSuchAlgorithmException;

import static com.daylight.arcface_acs.utils.EncryptUtil.toMD5;

/**
 * 设备信息工具类
 * Created by Daylight on 2018/1/19.
 */

public class DeviceInfoUtil {
    @SuppressLint("HardwareIds")
    public static String getDeviceId(Context context){
        String androidID= Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String id=androidID+ Build.SERIAL;
        try {
            return toMD5(id);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return id;
        }
    }
    public static String getDeviceBrand(){
        return Build.BRAND;
    }
    public static String getDeviceModel(){
        return Build.MODEL;
    }
}
