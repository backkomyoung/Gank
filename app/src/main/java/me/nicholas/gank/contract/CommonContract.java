package me.nicholas.gank.contract;

import java.util.List;

import me.nicholas.gank.base.BaseModel;
import me.nicholas.gank.base.BasePresenter;
import me.nicholas.gank.base.BaseView;
import me.nicholas.gank.bean.Common;
import me.nicholas.gank.bean.UpdateDate;
import rx.Observable;

/**
 * Created by Nicholas on 2016/7/8.
 */
public interface CommonContract {

    int REFRESH=0;
    int MORE=1;

    interface Model extends BaseModel{
        Observable<UpdateDate> getDate();
        Observable<List<Common>> getCommon(String type,int page);
    }

    interface View extends BaseView<List<Common>>{
        void onMore(List<Common> mores);
        void onDateSucceed(UpdateDate updateDate);
        void onDateFailure(String err);
        void onDateComplete();
    }

    interface Presenter extends BasePresenter{
        void getCommon(String type,int load,int page);
        void getDate();
    }
}
