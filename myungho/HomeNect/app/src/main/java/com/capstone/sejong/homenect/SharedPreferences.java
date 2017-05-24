package com.capstone.sejong.homenect;

import android.app.Activity;
import android.content.Context;

/**
 * Created by 12aud on 2017-04-27.
 */

public class SharedPreferences {
    Context mContext;

    public SharedPreferences(Context c) {
        mContext = c;
    }

    /**
     *
     * @param key key data
     * @param value 저장할 값
     * @param prefName file 이름
     */
    public void putValue(String key, String value, String prefName) {
        android.content.SharedPreferences pref = mContext.getSharedPreferences(prefName,
                Activity.MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = pref.edit();

        editor.putString(key, value); // value 저장
        editor.commit();
    }

    public void putValue(String key, boolean value, String prefName) {
        android.content.SharedPreferences pref = mContext.getSharedPreferences(prefName,
                Activity.MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = pref.edit();

        editor.putBoolean(key, value); // value 저장
        editor.commit();
    }

    /**
     *
     * @param key key data
     * @param dftValue key의 value가 Null이면 dftValue반환
     * @param prefName file 이름
     * @return
     */
    public String getValue(String key, String dftValue, String prefName) {
        android.content.SharedPreferences pref = mContext.getSharedPreferences(prefName,
                Activity.MODE_PRIVATE);
        try {
            return pref.getString(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }
    }

    public boolean getValue(String key, boolean dftValue, String prefName) {
        android.content.SharedPreferences pref = mContext.getSharedPreferences(prefName,
                Activity.MODE_PRIVATE);
        try {
            return pref.getBoolean(key, dftValue);
        } catch (Exception e) {
            return dftValue;
        }
    }

    public void removeAllPreferences(String prefName){
        android.content.SharedPreferences pref = mContext.getSharedPreferences(prefName,
                Activity.MODE_PRIVATE);
        android.content.SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }
}
