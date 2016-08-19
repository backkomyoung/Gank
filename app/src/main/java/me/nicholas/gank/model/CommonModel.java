package me.nicholas.gank.model;

import java.util.List;

import me.nicholas.gank.api.ApiEngine;
import me.nicholas.gank.api.ApiFather;
import me.nicholas.gank.bean.Common;
import me.nicholas.gank.bean.Data;
import me.nicholas.gank.bean.UpdateDate;
import me.nicholas.gank.contract.CommonContract;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Nicholas on 2016/7/8.
 */
public class CommonModel implements CommonContract.Model {


    @Override
    public Observable<UpdateDate> getDate() {
        ApiFather api = ApiEngine.getInstance().api;
        return api.getDate()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<List<Common>> getCommon(String type, int page) {

        ApiFather api= ApiEngine.getInstance().api;

        return api.getCommon(type,ApiFather.COUNT,page)
                .map(new Func1<Data<Common>, List<Common>>() {
                    @Override
                    public List<Common> call(Data<Common> commonData) {
                        return commonData.getResults();
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

}
