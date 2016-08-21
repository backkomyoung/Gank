package me.nicholas.gank;

import android.content.Context;

import org.litepal.LitePalApplication;

/**
 * Created by Nicholas on 2016/7/8.
 */
public class App extends LitePalApplication {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context=getApplicationContext();
    }

    public static Context getContext() {
        return context;
    }
}
