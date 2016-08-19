package me.nicholas.gank.ui.fragment;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.tbruyelle.rxpermissions.RxPermissions;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import me.nicholas.gank.App;
import me.nicholas.gank.R;
import me.nicholas.gank.adapter.MeizhiViewPagerAdapter;
import me.nicholas.gank.task.SaveWallpaperTask;
import me.nicholas.gank.utils.ToastUtils;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;


/**
 * Created by Nicholas on 2016/7/9.
 */
public class MeizhiDialogFragment extends DialogFragment {

    private static final String TAG = MeizhiDialogFragment.class.getName();

    public static final String URLS = "urls";
    public static final String POSITION = "position";

    @Bind(R.id.view_pager)
    ViewPager viewPager;
    @Bind(R.id.page_percent)
    TextView pagePercent;

    private ArrayList<String> urls;
    private int position;
    private AlertDialog dialog;

    public static MeizhiDialogFragment newInstance(ArrayList<String> urls, int position) {
        MeizhiDialogFragment fragment = new MeizhiDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(URLS, urls);
        bundle.putInt(POSITION, position);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations = R.style.MeizhiDialogAnimation;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        if (getArguments() != null) {
            urls = getArguments().getStringArrayList(URLS);
            position = getArguments().getInt(POSITION);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dialog_meizhi, container);
        ButterKnife.bind(this, view);
        initViews();
        return view;
    }

    private void initViews() {

        MeizhiViewPagerAdapter adapter = new MeizhiViewPagerAdapter(getActivity(), urls);
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(position);
        pagePercent.setText((position + 1) + "/" + urls.size());

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                pagePercent.setText((position + 1) + "/" + urls.size());
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        adapter.setClickListener(new MeizhiViewPagerAdapter.ClickListener() {
            @Override
            public void onClick(View view, int pos, float v, float v1) {
                dismiss();
            }
        });

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        dialog = builder.create();
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_meizhi_context_menu, null);
        dialog.setView(view);

        RelativeLayout saveView = (RelativeLayout) view.findViewById(R.id.dialog_meizhi_save);
        RelativeLayout wallpaperView = (RelativeLayout) view.findViewById(R.id.dialog_meizhi_wallpaper);
        RelativeLayout shareView = (RelativeLayout) view.findViewById(R.id.dialog_meizhi_share);

        adapter.setLongClickListener(new MeizhiViewPagerAdapter.LongClickListener() {
            @Override
            public void onLongClick(View v, int pos) {
                dialog.show();
            }
        });

        saveView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    doSave();
                    dialog.dismiss();
                } else {
                    new SaveWallpaperTask().execute(urls.get(viewPager.getCurrentItem()), SaveWallpaperTask.SAVE);
                    dialog.dismiss();
                }
            }
        });

        wallpaperView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SaveWallpaperTask().execute(urls.get(viewPager.getCurrentItem()), SaveWallpaperTask.WALLPAPER);
                dialog.dismiss();
            }
        });

        shareView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                getShareBitmap(urls.get(viewPager.getCurrentItem()))
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
                                Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getActivity().getContentResolver(), bitmap, null, null));
                                Intent intent = new Intent();
                                intent.setAction(Intent.ACTION_SEND);
                                intent.putExtra(Intent.EXTRA_STREAM, uri);
                                intent.setType("image/*");
                                startActivity(Intent.createChooser(intent, App.getContext().getResources().getString(R.string.action_share)));
                            }
                        });
                dialog.dismiss();
            }
        });

    }

    private void doSave() {
        RxPermissions.getInstance(App.getContext())
                .request(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean aBoolean) {
                        if (aBoolean) {
                            new SaveWallpaperTask().execute(urls.get(viewPager.getCurrentItem())
                                    , SaveWallpaperTask.SAVE);
                        } else {
                            ToastUtils.Short(R.string.save_no_permission);
                        }
                    }
                });
    }

    private Observable<Bitmap> getShareBitmap(final String url) {

        return Observable.create(new Observable.OnSubscribe<Bitmap>() {
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
                }

                subscriber.onNext(bitmap);
                subscriber.onCompleted();

            }
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

}
