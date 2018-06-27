package com.ucoin.ucoinnew.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.util.TypedValue;

import com.ucoin.ucoinnew.application.App;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Util {

    public static String getProperty(String key, Context context) throws IOException {
        Properties properties = new Properties();
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("config.properties");
        properties.load(inputStream);
        return properties.getProperty(key);
    }

    public static boolean checkUserToken() {
        String token = Util.getSP("userToken");
        if (token == null || token.equals("")) {
            return false;
        }
        return true;
    }

    public static boolean setSP(String key, String value) {
        SharedPreferences sharedPreferences = App.getInstance().getSharedPreferences("config", App.getInstance().MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        return editor.commit();
    }

    public static String getSP(String key) {
        SharedPreferences sharedPreferences = App.getInstance().getSharedPreferences("config", App.getInstance().MODE_PRIVATE);
        return sharedPreferences.getString(key, null);
    }


    public static int dip2px(Context context, float dipValue) {
        Resources r = context.getResources();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dipValue, r.getDisplayMetrics());
    }
}
