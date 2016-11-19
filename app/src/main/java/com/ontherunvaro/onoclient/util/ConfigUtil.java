package com.ontherunvaro.onoclient.util;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by varo on 19/11/16.
 */

public class ConfigUtil {


    private static String TAG = "ConfigUtil";

    public enum ConfigKey {
        USERNAME("user.name"), PASSWORD("user.password");

        String key;

        ConfigKey(String key) {
            this.key = key;
        }
    }

    private static final String PROP_FILE = "config.properties";
    private static Properties properties = new Properties();

    public static void load(Context ctx) {
        try {
            properties.load(ctx.getResources().getAssets().open(PROP_FILE));
            Log.d(TAG, "load: loaded file " + PROP_FILE);
        } catch (IOException e) {
            Log.e(TAG, "load: cannot load properties", e);
        }
    }

    public static String getProp(ConfigKey key) {
        return properties.getProperty(key.key);
    }

}
