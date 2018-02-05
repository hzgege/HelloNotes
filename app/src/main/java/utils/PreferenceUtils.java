package utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;


public class PreferenceUtils {
    private static PreferenceUtils instance;
    private SharedPreferences SP;

    private PreferenceUtils(Context mContext) {
        SP = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public static PreferenceUtils getInstance(Context context) {
        if (instance == null) {
            synchronized (PreferenceUtils.class) {
                if (instance == null)
                    instance = new PreferenceUtils(context);
            }
        }
        return instance;
    }

    public SharedPreferences.Editor getEditor() {
        return SP.edit();
    }

    public void putInt(String key, int value) {
        getEditor().putInt(key, value).commit();
    }

    public int getInt(String key, int defValue) {
        return SP.getInt(key, defValue);
    }

    public void putBoolean(String key, boolean value) {
        getEditor().putBoolean(key, value).commit();
    }

    public boolean getBoolean(String key, boolean defValue) {
        return SP.getBoolean(key, defValue);
    }

}
