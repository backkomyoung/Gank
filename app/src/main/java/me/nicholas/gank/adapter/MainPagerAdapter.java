package me.nicholas.gank.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import java.util.List;

import me.nicholas.gank.App;
import me.nicholas.gank.R;

/**
 * Created by Nicholas on 2016/7/8.
 */
public class MainPagerAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragments;
    private Context context;

    String[] titles = new String[]{
            App.getContext().getResources().getString(R.string.tab_title_daily),
            App.getContext().getResources().getString(R.string.tab_title_Android),
            App.getContext().getResources().getString(R.string.tab_title_Meizhi)
    };

    public MainPagerAdapter(FragmentManager fm,Context context) {
        super(fm);
        this.context=context;
    }

    public View getTabView(int position){
        View view= LayoutInflater.from(context).inflate(R.layout.view_tab,null);
        TextView tv = (TextView) view.findViewById(R.id.tab_title);
        tv.setText(titles[position]);
        return view;
    }

    public void addFragments(List<Fragment> fragments){
        if (fragments==null)
            return;
        this.fragments=fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments==null?0:fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return titles[position];
    }

}
