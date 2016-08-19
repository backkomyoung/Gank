package me.nicholas.gank.base;

/**
 * Created by Nicholas on 2016/7/8.
 */
public interface BaseView<T> {
    void onSucceed(T values);
    void onFailure(String err);
    void onCompleted();
}
