package com.iuh.stream.datalocal;

import android.content.Context;

public class DataLocalManager {
    private static DataLocalManager instance;
    private MySharedPreferences mySharedPreferences;

    public static void init(Context context){
        instance = new DataLocalManager();
        instance.mySharedPreferences = new MySharedPreferences(context);
    }

    private  static DataLocalManager getInstance(){
        if(instance == null){
            instance = new DataLocalManager();
        }
        return instance;
    }

    public static void putStringValue(String key, String value){
        DataLocalManager.getInstance().mySharedPreferences.putStringValue(key, value);
    }
    public static String getStringValue(String key){
        return DataLocalManager.getInstance().mySharedPreferences.getStringValue(key);
    }

}
