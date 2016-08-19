package me.nicholas.gank.ui.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import br.com.mauker.materialsearchview.MaterialSearchView;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import me.nicholas.gank.R;
import me.nicholas.gank.adapter.MainPagerAdapter;
import me.nicholas.gank.ui.fragment.AndroidFragment;
import me.nicholas.gank.ui.fragment.DailyFragment;
import me.nicholas.gank.ui.fragment.MeizhiFragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        AppBarLayout.OnOffsetChangedListener, View.OnClickListener {

    private static final String TAG = "MainActivity";
    public static final String MEIZHI_URL = "meizhi_url";
    public static final String ACTION_LOAD_MEIZHI = "me.nicholas.gank.REFRESH";

    @Bind(R.id.img_view)
    ImageView imgView;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tab_layout)
    TabLayout tabLayout;
    @Bind(R.id.view_pager)
    ViewPager viewPager;
    @Bind(R.id.nav_view)
    NavigationView navView;
    @Bind(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @Bind(R.id.search_view)
    MaterialSearchView searchView;
    @Bind(R.id.app_bar_layout)
    AppBarLayout appBarLayout;

    private long exitTime = 0;
    private long doubleClickTime = 0;
    private String url;

    private int offset = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        initViews();
    }

    private void initViews() {

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navView.setNavigationItemSelectedListener(this);

        View headView=navView.getHeaderView(0);

        CircleImageView avatarImg= (CircleImageView) headView.findViewById(R.id.img_avatar);
        TextView tvNick= (TextView) headView.findViewById(R.id.tv_nick);
        TextView tvPhone= (TextView) headView.findViewById(R.id.tv_phone);

        tvNick.setText("零下");
        tvPhone.setText("15812345678");

        avatarImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        FragmentManager manager = getSupportFragmentManager();
        MainPagerAdapter adapter = new MainPagerAdapter(manager, this);

        DailyFragment dailyFragment = DailyFragment.newInstance();
        AndroidFragment androidFragment = AndroidFragment.newInstance();
        MeizhiFragment meizhiFragment = MeizhiFragment.newInstance();

        List<Fragment> fragments = new ArrayList<>();
        fragments.add(dailyFragment);
        fragments.add(androidFragment);
        fragments.add(meizhiFragment);

        adapter.addFragments(fragments);

        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(2);
        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < tabLayout.getTabCount(); i++) {
            TabLayout.Tab tab = tabLayout.getTabAt(i);
            if (tab != null) {
                tab.setCustomView(adapter.getTabView(i));
                if (tab.getCustomView() != null) {
                    View tabView = (View) tab.getCustomView().getParent();
                    tabView.setTag(i);
                    tabView.setOnClickListener(this);
                }
            }
        }

        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.e(TAG, "CCCCCC");
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

    }

    @OnClick(R.id.img_view)
    public void onMeizhiClick(View view) {
        if (url == null) {
            return;
        }
        Intent intent = new Intent(MainActivity.this, MeizhiActivity.class);
        intent.putExtra(MEIZHI_URL, url);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        appBarLayout.addOnOffsetChangedListener(this);
        IntentFilter loadFilter = new IntentFilter();
        loadFilter.addAction(ACTION_LOAD_MEIZHI);
        registerReceiver(receiver, loadFilter);
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            switch (action) {
                case ACTION_LOAD_MEIZHI:

                    url = intent.getStringExtra(DailyFragment.MEIZHI_URL);
                    Picasso.with(MainActivity.this)
                            .load(intent.getStringExtra(DailyFragment.MEIZHI_URL))
                            .into(imgView);
                    toolbar.setTitle(intent.getStringExtra(DailyFragment.MEIZHI_DATE));

                    break;
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        appBarLayout.removeOnOffsetChangedListener(this);
        unregisterReceiver(receiver);
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {

            if (searchView.isOpen()) {
                searchView.closeSearch();
            } else {
                exit();
            }

        }
    }

    private void exit() {

        long currentTime = System.currentTimeMillis();

        if (currentTime - exitTime > 2000) {
            Toast.makeText(this, R.string.EXIT, Toast.LENGTH_SHORT).show();
            exitTime = currentTime;
            return;
        }

        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_search) {
            if (offset != 0) {
                appBarLayout.setExpanded(true);
            }
            searchView.openSearch();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.nav_collect) {

        } else if (id == R.id.nav_settings) {

        } else if (id == R.id.nav_share) {

        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
        offset = verticalOffset;
    }

    @Override
    public void onClick(View view) {
        int position = (int) view.getTag();
        switch (position) {
            case 0:

                break;
            case 1:
                if (isDoubleClick()) {
                    Intent intent = new Intent(AndroidFragment.ACTION_SCROLL_TO_TOP);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                }
                break;
            case 2:
                if (isDoubleClick()) {
                    Intent intent = new Intent(MeizhiFragment.ACTION_SCROLL_TO_TOP);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
                }
                break;
        }
    }

    private boolean isDoubleClick() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - doubleClickTime > 800) {
            doubleClickTime = currentTime;
            return false;
        }
        return true;
    }
}
