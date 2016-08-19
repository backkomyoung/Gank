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
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
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
import me.nicholas.gank.adapter.MeizhiAdapter;
import me.nicholas.gank.api.ApiFather;
import me.nicholas.gank.bean.Common;
import me.nicholas.gank.bean.UpdateDate;
import me.nicholas.gank.contract.CommonContract;
import me.nicholas.gank.presenter.CommonPresenter;

/**
 * Created by Nicholas on 2016/7/8.
 */
public class MeizhiFragment extends Fragment implements CommonContract.View, SwipeRefreshLayout.OnRefreshListener {

    private static final String TAG = "MeizhiFragment";
    public static final String ACTION_SCROLL_TO_TOP = "me.nicholas.gank.ACTION.MEIZHI.SCROLL.TO.TOP";

    @Bind(R.id.recyclerView)
    RecyclerView recyclerView;
    @Bind(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;

    private CommonPresenter presenter;
    private MeizhiAdapter adapter;

    private List<Common> meizhis;
    private String gankDate;

    private ArrayList<String> urls;
    private int page = 1;
    private LocalBroadcastManager broadcastManager;

    public static MeizhiFragment newInstance() {
        return new MeizhiFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_meizhi, container, false);
        ButterKnife.bind(this, v);
        presenter = new CommonPresenter(this);
        urls = new ArrayList<>();
        meizhis = new ArrayList<>();
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

        adapter = new MeizhiAdapter(getActivity(), meizhis);

        final StaggeredGridLayoutManager layoutManager
                = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int[] lastVisiblePositions = new int[2];
                lastVisiblePositions = layoutManager.findLastCompletelyVisibleItemPositions(lastVisiblePositions);
                int midItem = lastVisiblePositions[1];
                boolean isBottom = midItem > adapter.getItemCount() - 3;
                if (isBottom && !swipeRefreshLayout.isRefreshing() && dy > 0) {
                    swipeRefreshLayout.setRefreshing(true);
                    loadMore();
                }
            }
        });

        adapter.setMeizhiClickListener(new MeizhiAdapter.MeizhiClickListener() {
            @Override
            public void onClick(View v, int position) {
                MeizhiDialogFragment dialog = MeizhiDialogFragment.newInstance(urls, position);
                dialog.show(getChildFragmentManager(), "MeizhiDialogFragment");
            }
        });

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        List<Common> dbMeizhi = getDataFromLitepal();

        if (dbMeizhi != null) {
            refresh(dbMeizhi);
        }

        swipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(true);
                onRefresh();
            }
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
        presenter.getCommon(ApiFather.TYPE_MEIZHI, CommonContract.MORE, page);
    }

    @Override
    public void onSucceed(List<Common> values) {
        saveMeizhi(values);
        refresh(values);
    }

    private void saveMeizhi(List<Common> values) {

        DataSupport.deleteAll(Common.class, " type= ? ", Config.COMMON_TYPE_MEIZHI);

        DataSupport.saveAll(values);

        SharedPreferences sp = App.getContext().getSharedPreferences(Config.SP_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Config.MEIZHI_REFRESH_DATE, gankDate);
        editor.apply();

    }

    private List<Common> getDataFromLitepal() {

        List<Common> meizhis = DataSupport
                .where(" type = ?", Config.COMMON_TYPE_MEIZHI)
                .find(Common.class);

        if (meizhis.size() != 0) {
            return meizhis;
        }

        return null;
    }

    private void refresh(List<Common> values) {

        if (meizhis.size() != 0) {
            meizhis.clear();
        }

        meizhis.addAll(values);
        adapter.notifyDataSetChanged();

        if (urls.size() != 0) {
            urls.clear();
        }

        addUrls(values);
    }

    private void addUrls(List<Common> values) {
        for (Common common : values) {
            urls.add(common.getUrl());
        }
    }

    @Override
    public void onMore(List<Common> mores) {
        if (mores.size() != 0) {
            meizhis.addAll(mores);
            adapter.notifyItemInserted(adapter.getItemCount());
            addUrls(mores);
        }
    }

    @Override
    public void onDateSucceed(UpdateDate updateDate) {

        SharedPreferences sp = App.getContext().getSharedPreferences(Config.SP_NAME,
                Context.MODE_PRIVATE);
        String date = sp.getString(Config.MEIZHI_REFRESH_DATE, "");
        gankDate = updateDate.getResults().get(0);

        if (!gankDate.equals(date)) {
            presenter.getCommon(ApiFather.TYPE_MEIZHI, CommonContract.REFRESH, 1);
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
    public void onFailure(String err) {
        Log.e(TAG, "onFailure:" + err);
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
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        broadcastManager.unregisterReceiver(receiver);
        presenter.unSubscription();
    }
}
