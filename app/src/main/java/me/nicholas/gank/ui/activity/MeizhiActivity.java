package me.nicholas.gank.ui.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.RelativeLayout;

import com.squareup.picasso.Picasso;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.nicholas.gank.App;
import me.nicholas.gank.R;
import me.nicholas.gank.task.SaveWallpaperTask;
import me.nicholas.gank.utils.ToastUtils;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class MeizhiActivity extends AppCompatActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.photo_view)
    PhotoView photoView;
    @Bind(R.id.appbar_layout)
    AppBarLayout appbarLayout;
    @Bind(R.id.relativeLayout)
    RelativeLayout relativeLayout;

    private String url;
    private boolean mShow = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meizhi);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);

        toolbar.setNavigationOnClickListener(view -> finish());

        Intent intent = getIntent();

        if (intent != null) {
            url = intent.getStringExtra(MainActivity.MEIZHI_URL);
        }

        Picasso.with(this)
                .load(url)
                .into(photoView);

        photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float v, float v1) {
                toolbarAnim();
            }

            @Override
            public void onOutsidePhotoTap() {
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_meizhi, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_save:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    saveMeizhi();
                } else {
                    new SaveWallpaperTask().execute(url, SaveWallpaperTask.SAVE);
                }
                break;
            case R.id.action_share:
                shareMeizhi();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveMeizhi() {
        RxPermissions.getInstance(this)
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(aBoolean -> {
                    if (aBoolean) {
                        new SaveWallpaperTask().execute(url
                                , SaveWallpaperTask.SAVE);
                    } else {
                        Snackbar.make(relativeLayout, R.string.save_no_permission
                                , Snackbar.LENGTH_LONG).show();
                    }
                });
    }

    private void shareMeizhi() {

        Observable.create(new Observable.OnSubscribe<Bitmap>() {
            @Override
            public void call(Subscriber<? super Bitmap> subscriber) {
                Bitmap bitmap = null;
                try {
                    bitmap = Picasso.with(App.getContext()).load(url).get();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (bitmap == null) {
                    subscriber.onError(new Exception("获取图片失败!"));
                } else {
                    subscriber.onNext(bitmap);
                    subscriber.onCompleted();
                }
            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Bitmap>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.Short(e.getMessage());
                    }

                    @Override
                    public void onNext(Bitmap bitmap) {
                        Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, null, null));
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_SEND);
                        intent.putExtra(Intent.EXTRA_STREAM, uri);
                        intent.setType("image/*");
                        startActivity(Intent.createChooser(intent, App.getContext().getResources().getString(R.string.action_share)));
                    }
                });
    }

    private void toolbarAnim() {
        appbarLayout.animate()
                .translationY(mShow ? 0 : -appbarLayout.getHeight())
                .setInterpolator(new DecelerateInterpolator(2))
                .start();
        mShow = !mShow;
    }
}
