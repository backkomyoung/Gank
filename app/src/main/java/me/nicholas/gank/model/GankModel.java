package me.nicholas.gank.model;


import java.util.ArrayList;
import java.util.List;

import me.nicholas.gank.api.ApiEngine;
import me.nicholas.gank.api.ApiFather;
import me.nicholas.gank.bean.Gank;
import me.nicholas.gank.bean.GankDaily;
import me.nicholas.gank.bean.GankList;
import me.nicholas.gank.bean.GankWithDate;
import me.nicholas.gank.bean.UpdateDate;
import me.nicholas.gank.contract.GankContract;
import me.nicholas.gank.utils.DateUtils;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by Nicholas on 2016/7/8.
 */
public class GankModel implements GankContract.Model {

    @Override
    public Observable<UpdateDate> getDate() {
        ApiFather api = ApiEngine.getInstance().api;
        return api.getDate()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<GankWithDate> getGank(final String date) {

        ApiFather api = ApiEngine.getInstance().api;

        String year = DateUtils.getGankDate(date, DateUtils.YEAR);
        String month = DateUtils.getGankDate(date, DateUtils.MONTH);
        String day = DateUtils.getGankDate(date, DateUtils.DAY);

        return api.getGankDaily(year, month, day).flatMap(new Func1<GankDaily, Observable<GankWithDate>>() {
            @Override
            public Observable<GankWithDate> call(GankDaily gankDaily) {
                return Observable.just(mergeGankDate(gankDaily, date));
            }
        }).subscribeOn(Schedulers.io())
          .observeOn(AndroidSchedulers.mainThread());
    }

    private GankWithDate mergeGankDate(GankDaily gankDaily, String date) {

        GankWithDate gankWithDate = new GankWithDate();

        List<Gank> ganks = new ArrayList<>();

        GankList lists = gankDaily.getResults();

        if (lists.get休息视频() != null)
            ganks.addAll(lists.get休息视频());

        if (lists.getAndroid() != null)
            ganks.addAll(lists.getAndroid());

        if (lists.getiOS() != null)
            ganks.addAll(lists.getiOS());

        if (lists.get前端() != null)
            ganks.addAll(lists.get前端());

        if (lists.getApp() != null)
            ganks.addAll(lists.getApp());

        if (lists.get拓展资源() != null)
            ganks.addAll(lists.get拓展资源());

        if (lists.get瞎推荐() != null)
            ganks.addAll(lists.get瞎推荐());

        if (lists.get福利() != null) {
            gankWithDate.setUrl(lists.get福利().get(0).getUrl());
        }

        gankWithDate.setGanks(ganks);
        gankWithDate.setDate(date);

        return gankWithDate;
    }

}
