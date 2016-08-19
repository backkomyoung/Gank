package me.nicholas.gank.base;

import rx.Subscription;
import rx.subscriptions.CompositeSubscription;

/**
 * Created by Nicholas on 2016/7/8.
 */
public abstract class RxSubManager {

    private CompositeSubscription cs;

    /**
     * 添加到队列
     * @param s Subscription
     */
    protected void addSubscription(Subscription s){
        if (cs==null){
            cs=new CompositeSubscription();
        }
        cs.add(s);
    }

    /**
     * 解除订阅（Subscription）
     */
    protected void unSub(){
        if (cs!=null){
            cs.unsubscribe();
        }
    }

    /**
     * 子类必须实现的方法
     */
    public abstract void unSubscription();


}
