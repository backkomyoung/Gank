package me.nicholas.gank.presenter;

import java.util.List;

import me.nicholas.gank.base.RxSubManager;
import me.nicholas.gank.bean.Common;
import me.nicholas.gank.bean.UpdateDate;
import me.nicholas.gank.contract.CommonContract;
import me.nicholas.gank.model.CommonModel;
import rx.Observer;
import rx.Subscription;

/**
 * Created by Nicholas on 2016/7/8.
 */
public class CommonPresenter extends RxSubManager implements CommonContract.Presenter {

    private CommonContract.Model model;
    private CommonContract.View view;

    public CommonPresenter(CommonContract.View view) {
        this.view = view;
        model=new CommonModel();
    }

    @Override
    public void getCommon(String type, final int load, int page) {

        Subscription s = model.getCommon(type, page)
                .subscribe(new Observer<List<Common>>() {
                    @Override
                    public void onCompleted() {
                        view.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.onFailure(e.getMessage());
                    }

                    @Override
                    public void onNext(List<Common> commons) {
                        switch (load) {
                            case CommonContract.MORE:
                                view.onMore(commons);
                                break;
                            case CommonContract.REFRESH:
                                view.onSucceed(commons);
                                break;
                        }
                    }
                });
        addSubscription(s);
    }

    @Override
    public void getDate() {
        Subscription s = model.getDate()
                .subscribe(new Observer<UpdateDate>() {
                    @Override
                    public void onCompleted() {
                        view.onDateComplete();
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.onDateFailure(e.getMessage());
                    }

                    @Override
                    public void onNext(UpdateDate updateDate) {
                        view.onDateSucceed(updateDate);
                    }
                });
        addSubscription(s);
    }

    @Override
    public void unSubscription() {
        view=null;
        unSub();
    }
}
