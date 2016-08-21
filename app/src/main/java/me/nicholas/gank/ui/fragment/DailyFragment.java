package me.nicholas.gank.ui.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import me.nicholas.gank.adapter.DailyAdapter;
import me.nicholas.gank.bean.Gank;
import me.nicholas.gank.bean.GankLitepal;
import me.nicholas.gank.bean.GankWithDate;
import me.nicholas.gank.bean.UpdateDate;
import me.nicholas.gank.contract.GankContract;
import me.nicholas.gank.presenter.GankPresenter;
import me.nicholas.gank.ui.activity.MainActivity;
import me.nicholas.gank.ui.activity.WebActivity;
import me.nicholas.gank.utils.ToastUtils;

/**
 * Created by Nicholas on 2016/7/8.
 */
public class DailyFragment extends Fragment implements GankContract.View, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "DailyFragment";
    public static final String ACTION_SCROLL_TO_TOP = "me.nicholas.gank.ACTION.DAILY.SCROLL.TO.TOP";
    public static final String MEIZHI_URL = "url";
    public static final String MEIZHI_DATE = "date";

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    private DailyAdapter adapter;
    private GankPresenter presenter;
    private LocalBroadcastManager broadcastManager;

    private List<Gank> ganks;
    List<String> dates;
    private int flag = 0;

    public static DailyFragment newInstance() {
        return new DailyFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_daily, container, false);
        ButterKnife.bind(this, v);
        initViews();
        return v;
    }

    private void initViews() {
        ganks = new ArrayList<>();
        presenter = new GankPresenter(this);
        adapter = new DailyAdapter(ganks);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setColorSchemeResources(
                R.color.googleColorRed,
                R.color.googleColorGreen,
                R.color.googleColorYellow,
                R.color.googleColorBlue);
        swipeRefreshLayout.setOnRefreshListener(this);

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
            Intent intent = new Intent(getActivity(), WebActivity.class);
            intent.putExtra(Config.GANK_TITLE, title);
            intent.putExtra(Config.GANK_URL, url);
            startActivity(intent);
        });
    }

    private void loadMore() {

        if (dates == null) {
            ToastUtils.Short(R.string.REFRESH);
            return;
        }

        flag++;
        presenter.getMore(dates.get(flag));
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GankWithDate dbGanks = getDataFromLitepal();

        if (dbGanks != null) {
            refresh(dbGanks);
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


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        presenter.unSubscription();
    }

    @Override
    public void onSucceed(GankWithDate values) {
        saveGanks(values);
        refresh(values);
    }

    private void saveGanks(GankWithDate values) {

        DataSupport.deleteAll(GankLitepal.class);

        GankLitepal dbGank = new GankLitepal();
        List<Gank> ganks = values.getGanks();

        DataSupport.saveAll(ganks);

        dbGank.setDate(values.getDate());
        dbGank.setUrl(values.getUrl());
        dbGank.getGankList().addAll(ganks);

        if (dbGank.save()) {
            Log.e(TAG, "Save Succeed!");
        } else {
            Log.e(TAG, "Save Failure!");
        }

        SharedPreferences sp = App.getContext().getSharedPreferences(Config.SP_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Config.GANK_REFRESH_DATE, values.getDate());
        editor.apply();

    }

    private void refresh(GankWithDate values) {

        Intent intent = new Intent(MainActivity.ACTION_LOAD_MEIZHI);
        intent.putExtra(MEIZHI_URL, values.getUrl());
        intent.putExtra(MEIZHI_DATE, values.getDate());
        getActivity().sendBroadcast(intent);

        if (ganks.size() != 0) {
            ganks.clear();
        }

        ganks.addAll(values.getGanks());
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onFailure(String err) {
        Log.e(TAG, "Failure :" + err);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onCompleted() {
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onRefresh() {
        presenter.getDate();
    }

    @Override
    public void onMoreSucceed(GankWithDate data) {

        if (data.getGanks().size() == 0) {
            ToastUtils.Short(R.string.NO_MORE);
            return;
        }

        ganks.addAll(data.getGanks());
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onDateSucceed(UpdateDate updateDate) {

        dates = updateDate.getResults();

        SharedPreferences sp = App.getContext().getSharedPreferences(Config.SP_NAME,
                Context.MODE_PRIVATE);
        String date = sp.getString(Config.GANK_REFRESH_DATE, "");
        String gankDate = updateDate.getResults().get(0);

        if (!gankDate.equals(date)) {
            presenter.getGank(gankDate);
        } else {
            swipeRefreshLayout.setRefreshing(false);
        }
    }

    private GankWithDate getDataFromLitepal() {

        GankLitepal data = DataSupport.findFirst(GankLitepal.class);

        if (data != null) {

            GankWithDate gank = new GankWithDate();
            gank.setDate(data.getDate());
            gank.setUrl(data.getUrl());
            gank.setGanks(data.getGankList());

            return gank;
        }

        return null;
    }

    @Override
    public void onDateFailure(String err) {
        Log.e(TAG, "DateFailure :" + err);
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onDateComplete() {
    }
}
