package me.nicholas.gank.contract;

import me.nicholas.gank.base.BaseModel;
import me.nicholas.gank.base.BasePresenter;
import me.nicholas.gank.base.BaseView;
import me.nicholas.gank.bean.GankWithDate;
import me.nicholas.gank.bean.UpdateDate;
import rx.Observable;

/**
 * Created by Nicholas on 2016/7/8.
 */
public interface GankContract {

    interface  Model extends BaseModel{
        Observable<UpdateDate> getDate();
        Observable<GankWithDate> getGank(String date);
    }

    interface View extends BaseView<GankWithDate>{
        void onMoreSucceed(GankWithDate data);
        void onDateSucceed(UpdateDate updateDate);
        void onDateFailure(String err);
        void onDateComplete();
    }

    interface Presenter extends BasePresenter{
        void getDate();
        void getGank(String date);
        void getMore(String date);
    }
}
