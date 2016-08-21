package me.nicholas.gank.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.nicholas.gank.App;
import me.nicholas.gank.Config;
import me.nicholas.gank.R;
import me.nicholas.gank.adapter.AndroidAdapter;
import me.nicholas.gank.api.ApiFather;
import me.nicholas.gank.bean.Common;
import me.nicholas.gank.bean.UpdateDate;
import me.nicholas.gank.contract.CommonContract;
import me.nicholas.gank.presenter.CommonPresenter;
import me.nicholas.gank.ui.activity.WebActivity;
import me.nicholas.gank.utils.ToastUtils;
import me.nicholas.gank.widget.DividerItemDecoration;

/**
 * Created by Nicholas on 2016/7/8.
 */
public class AndroidFragment extends Fragment
        implements CommonContract.View, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = AndroidFragment.class.getName();
    public static final String ACTION_SCROLL_TO_TOP = "me.nicholas.gank.ACTION.ANDROID.SCROLL.TO.TOP";

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    private AndroidAdapter adapter;
    private CommonPresenter presenter;

    private List<Common> datas;
    private String gankDate;

    private int page = 1;
    private LocalBroadcastManager broadcastManager;

    public static AndroidFragment newInstance() {
        return new AndroidFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_android, container, false);
        ButterKnife.bind(this, v);
        presenter = new CommonPresenter(this);
        datas = new ArrayList<>();
        initViews();

        return v;
    }

    private void initViews() {

        swipeRefreshLayout.setColorSchemeResources(
                R.color.googleColorRed,
                R.color.googleColorGreen,
                R.color.googleColorYellow,
                R.color.googleColorBlue);

        swipeRefreshLayout.setOnRefreshListener(this);

        adapter = new AndroidAdapter(datas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL_LIST));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            boolean isSlidingToLast = false;

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {

                    int lastVisibleItem = layoutManager.findLastCompletelyVisibleItemPosition();
                    int totalItemCount = layoutManager.getItemCount();

                    if (lastVisibleItem == (totalItemCount - 1) && !swipeRefreshLayout.isRefreshing() && !isSlidingToLast) {
                        swipeRefreshLayout.setRefreshing(true);
                        loadMore();
                    }
                }
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dx > 0) {
                    isSlidingToLast = true;
                } else {
                    isSlidingToLast = false;
                }
            }
        });


        adapter.setItemClickListener((view, title, url, position) -> {
            Intent intent=new Intent(getActivity(), WebActivity.class);
            intent.putExtra(Config.GANK_TITLE,title);
            intent.putExtra(Config.GANK_URL,url);
            startActivity(intent);
        });
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<Common> androids = getDataFromLitepal();

        if (androids != null) {
            refresh(androids);
        }

        swipeRefreshLayout.post(() -> {
            swipeRefreshLayout.setRefreshing(true);
            onRefresh();
        });

        broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_SCROLL_TO_TOP);
        broadcastManager.registerReceiver(receiver, intentFilter);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            String action = intent.getAction();
            if (action.equals(ACTION_SCROLL_TO_TOP)) {
                recyclerView.scrollToPosition(0);
            }
        }
    };

    private void loadMore() {
        page++;
        presenter.getCommon(ApiFather.TYPE_ANDROID, CommonContract.MORE, page);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        broadcastManager.unregisterReceiver(receiver);
        presenter.unSubscription();
    }

    @Override
    public void onRefresh() {
        presenter.getDate();
    }

    @Override
    public void onMore(List<Common> mores) {

        if (mores.size() == 0) {
            ToastUtils.Short(R.string.NO_MORE);
            return;
        }

        datas.addAll(mores);
        adapter.notifyItemInserted(adapter.getItemCount());
    }

    @Override
    public void onDateSucceed(UpdateDate updateDate) {

        SharedPreferences sp = App.getContext().getSharedPreferences(Config.SP_NAME,
                Context.MODE_PRIVATE);
        String date = sp.getString(Config.ANDROID_REFRESH_DATE, "");
        gankDate = updateDate.getResults().get(0);

        if (!gankDate.equals(date)) {
            presenter.getCommon(ApiFather.TYPE_ANDROID, CommonContract.REFRESH, 1);
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    @Override
    public void onDateFailure(String err) {
        Log.e(TAG, "DateFailure :" + err);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onDateComplete() {
    }

    @Override
    public void onSucceed(List<Common> values) {
        saveAndroid(values);
        refresh(values);
    }

    private void saveAndroid(List<Common> values) {

        DataSupport.deleteAll(Common.class, " type = ?", Config.COMMON_TYPE_ANDROID);

        DataSupport.saveAll(values);

        SharedPreferences sp = App.getContext().getSharedPreferences(Config.SP_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Config.ANDROID_REFRESH_DATE, gankDate);
        editor.apply();

    }

    private void refresh(List<Common> commons) {
        if (datas.size() != 0) {
            datas.clear();
        }
        datas.addAll(commons);
        adapter.notifyDataSetChanged();
    }

    private List<Common> getDataFromLitepal() {

        List<Common> androids = DataSupport
                .where(" type = ?", Config.COMMON_TYPE_ANDROID)
                .find(Common.class);

        if (androids.size() != 0) {
            return androids;
        }

        return null;
    }

    @Override
    public void onFailure(String err) {
        Log.e(TAG, err);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onCompleted() {
        swipeRefreshLayout.setRefreshing(false);
    }
}
