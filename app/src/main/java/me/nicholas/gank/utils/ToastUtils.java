package me.nicholas.gank.utils;

import android.widget.Toast;

import me.nicholas.gank.App;


/**
 * Created by zoom on 2016/4/24.
 */
public class ToastUtils {

    private ToastUtils() {
        throw new UnsupportedOperationException("Do not need instantiate!");
    }

    public static void Short(String msg) {
        Toast.makeText(App.getContext(), msg, Toast.LENGTH_SHORT).show();
    }

    public static void Short(int id) {
        Toast.makeText(App.getContext(), id, Toast.LENGTH_SHORT).show();
    }

    public static void Long(String msg) {
        Toast.makeText(App.getContext(), msg, Toast.LENGTH_LONG).show();
    }

    public static void Long(int id) {
        Toast.makeText(App.getContext(), id, Toast.LENGTH_LONG).show();
    }
}
