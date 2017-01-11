package com.example.apple.imdemo.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.apple.imdemo.IMApplication;

/**
 * Created by apple on 17/1/7.
 *
 * 保存
 *
 * 获取数据
 */

public class SpUtils {

    public static final String IS_NEW_INVITE = "is_new_invite";//新的邀请标记
    private static SpUtils instance = new SpUtils();

    private static SharedPreferences msp;

    public SpUtils() {

    }

    //单例
    public static SpUtils getInstance(){

        if (msp==null) {
            msp = IMApplication.getGlobalApplication().getSharedPreferences("im", Context.MODE_PRIVATE);
        }
        return instance;
    }

    //保存
    public void save(String key,Object value){
        if (value instanceof String){
            msp.edit().putString(key, (String) value).commit();
        }else if (value instanceof Boolean){
            msp.edit().putBoolean(key, (Boolean) value).commit();
        }else if (value instanceof Integer){
            msp.edit().putInt(key, (Integer) value).commit();
        }
    }

    //获取数据

    public String getString(String key,String defValue){
        return msp.getString(key,defValue);
    }

    //获取布尔数据
    public boolean getBoolean(String key,boolean defValue){
        return msp.getBoolean(key,defValue);
    }

    //获取整型数据
    public int getInt(String key,int defValue){
        return msp.getInt(key,defValue);
    }
}
