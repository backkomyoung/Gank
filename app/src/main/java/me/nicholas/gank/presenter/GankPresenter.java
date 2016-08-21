package me.nicholas.gank.presenter;

import me.nicholas.gank.base.RxSubManager;
import me.nicholas.gank.bean.GankWithDate;
import me.nicholas.gank.bean.UpdateDate;
import me.nicholas.gank.contract.GankContract;
import me.nicholas.gank.model.GankModel;
import rx.Observer;
import rx.Subscription;

/**
 * Created by Nicholas on 2016/7/8.
 */
public class GankPresenter extends RxSubManager implements GankContract.Presenter {

    private GankContract.Model model;
    private GankContract.View view;

    public GankPresenter(GankContract.View view) {
        this.view = view;
        model=new GankModel();
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
    public void getGank(String date) {

        Subscription s = model.getGank(date)
                .subscribe(new Observer<GankWithDate>() {
                    @Override
                    public void onCompleted() {
                        view.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.onFailure(e.getMessage());
                    }

                    @Override
                    public void onNext(GankWithDate gankWithDate) {
                        view.onSucceed(gankWithDate);
                    }
                });

        addSubscription(s);
    }

    @Override
    public void getMore(String date) {

        Subscription s = model.getGank(date)
                .subscribe(new Observer<GankWithDate>() {
                    @Override
                    public void onCompleted() {
                        view.onCompleted();
                    }

                    @Override
                    public void onError(Throwable e) {
                        view.onFailure(e.getMessage());
                    }

                    @Override
                    public void onNext(GankWithDate gankWithDate) {
                        view.onMoreSucceed(gankWithDate);
                    }
                });

        addSubscription(s);

    }

    @Override
    public void unSubscription() {
        view = null;
        unSub();
    }
}
