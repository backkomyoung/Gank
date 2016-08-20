package me.nicholas.gank.ui.activity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import org.litepal.crud.DataSupport;

import java.lang.reflect.Field;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.nicholas.gank.BuildConfig;
import me.nicholas.gank.Config;
import me.nicholas.gank.R;
import me.nicholas.gank.bean.Favorite;
import me.nicholas.gank.utils.DateUtils;
import me.nicholas.gank.utils.ToastUtils;

public class WebActivity extends AppCompatActivity {

    public static final String COPY_LABEL = "copy_label";

    private static final int FLAG_SHOW = 0;
    private static final int FLAG_HIDE = 1;

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.web_progressBar)
    ProgressBar progressBar;
    @Bind(R.id.web_view)
    WebView webView;
    @Bind(R.id.app_bar_layout)
    AppBarLayout appBarLayout;


    private ClipboardManager cbm;
    private WebSettings settings;
    private String title;
    private String url;

    private int touchSlop;
    private float firstY;
    private float currentY;

    private boolean gankExist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        Intent intent = getIntent();

        if (intent != null) {
            title = intent.getStringExtra(Config.GANK_TITLE);
            url = intent.getStringExtra(Config.GANK_URL);
        }

        initViews();
    }


    private void initViews() {

        touchSlop = ViewConfiguration.get(this).getScaledTouchSlop();

        settings = webView.getSettings();
        settings.setDomStorageEnabled(true);
        settings.setCacheMode(WebSettings.LOAD_DEFAULT);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);

        if (Build.VERSION.SDK_INT > 19) {
            settings.setLoadsImagesAutomatically(true);
        } else {
            settings.setLoadsImagesAutomatically(false);
        }

        webView.requestFocus();
        webView.setWebViewClient(new GankWebViewClient());
        webView.setWebChromeClient(new GankWebChromeClient());
        webView.loadUrl(url);

    }


    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onResume() {
        settings.setJavaScriptEnabled(true);
        super.onResume();
    }

    @Override
    protected void onStop() {
        settings.setJavaScriptEnabled(false);
        super.onStop();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        List<Favorite> favorites = DataSupport.where("title = ?", title).find(Favorite.class);

        MenuItem item = menu.findItem(R.id.menu_favorite);

        if (favorites.size() != 0) {
            item.setIcon(R.drawable.ic_favorite_white_24dp);
            gankExist = true;
        } else {
            item.setIcon(R.drawable.ic_favorite_border_white_24dp);
            gankExist = false;
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_web, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.menu_favorite:

                if (gankExist) {
                    item.setIcon(R.drawable.ic_favorite_border_white_24dp);
                    DataSupport.deleteAll(Favorite.class, "title = ?", title);
                    ToastUtils.Short(R.string.web_copy_favorite_delete);
                    gankExist = false;
                } else {
                    item.setIcon(R.drawable.ic_favorite_white_24dp);

                    Favorite favorite = new Favorite();
                    favorite.setDate(DateUtils.getNowLongTimes());
                    favorite.setTitle(title);
                    favorite.setUrl(url);

                    if (favorite.save()) {
                        ToastUtils.Short(R.string.web_copy_favorite_succeed);
                    } else {
                        ToastUtils.Short(R.string.web_copy_favorite_failure);
                    }
                    gankExist = true;
                }
                break;
            case R.id.menu_copy:
                copy2Clipboard();
                break;
            case R.id.menu_reload:
                webView.reload();
                break;
            case R.id.menu_open:
                openInBrowser();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private void openInBrowser() {
        Uri uri = Uri.parse(webView.getUrl());
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            ToastUtils.Short(R.string.web_open_failure);
        }
    }

    private void copy2Clipboard() {
        if (cbm == null) {
            cbm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        }
        ClipData data = ClipData.newPlainText(COPY_LABEL, webView.getUrl());
        cbm.setPrimaryClip(data);

        ToastUtils.Short(R.string.web_copy_succeed);
    }

    private class GankWebViewClient extends WebViewClient {

        @Override
        public void onPageFinished(WebView view, String url) {
            toolbar.setTitle(view.getTitle());
            if (!webView.getSettings().getLoadsImagesAutomatically()) {
                webView.getSettings().setLoadsImagesAutomatically(true);
            }
            super.onPageFinished(view, url);
        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            handler.proceed();
            super.onReceivedSslError(view, handler, error);
        }

        @Override
        public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
            super.onReceivedError(view, request, error);
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }
    }

    private class GankWebChromeClient extends WebChromeClient {

        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (newProgress == 100) {
                progressBar.setVisibility(View.GONE);
            } else {
                if (View.GONE == progressBar.getVisibility()) {
                    progressBar.setVisibility(View.VISIBLE);
                }
                progressBar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }
    }

    @Override
    protected void onDestroy() {

        if (webView != null) {
            webView.setVisibility(View.GONE);
            webView.removeAllViews();
            webView.destroy();
            releaseAllWebViewCallback();
        }

        super.onDestroy();
    }

    /**
     * 防止内存泄露
     */
    public void releaseAllWebViewCallback() {
        if (Build.VERSION.SDK_INT < 16) {
            try {
                Field field = WebView.class.getDeclaredField("mWebViewCore");
                field = field.getType().getDeclaredField("mBrowserFrame");
                field = field.getType().getDeclaredField("sConfigCallback");
                field.setAccessible(true);
                field.set(null, null);
            } catch (NoSuchFieldException e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            } catch (IllegalAccessException e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                Field sConfigCallback = Class.forName("android.webkit.BrowserFrame").getDeclaredField("sConfigCallback");
                if (sConfigCallback != null) {
                    sConfigCallback.setAccessible(true);
                    sConfigCallback.set(null, null);
                }
            } catch (NoSuchFieldException e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            } catch (ClassNotFoundException e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            } catch (IllegalAccessException e) {
                if (BuildConfig.DEBUG) {
                    e.printStackTrace();
                }
            }
        }
    }

}
