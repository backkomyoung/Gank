package me.nicholas.gank.api;

import me.nicholas.gank.bean.Common;
import me.nicholas.gank.bean.Data;
import me.nicholas.gank.bean.GankDaily;
import me.nicholas.gank.bean.UpdateDate;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by Nicholas on 2016/7/8.
 */
public interface ApiFather {

    String BASE_URL="http://gank.io/api/";
    String TYPE_MEIZHI="福利";
    String TYPE_ANDROID="Android";
    String TYPE_IOS="iOS";
    int COUNT = 20;

    @GET("day/{year}/{month}/{day}")
    Observable<GankDaily> getGankDaily(@Path("year") String year,
                                       @Path("month") String month,
                                       @Path("day") String day);
    @GET("day/history")
    Observable<UpdateDate> getDate();

    @GET("data/{type}/{count}/{page}")
    Observable<Data<Common>> getCommon(@Path("type") String type,
                                       @Path("count") int count,
                                       @Path("page") int page);
}
