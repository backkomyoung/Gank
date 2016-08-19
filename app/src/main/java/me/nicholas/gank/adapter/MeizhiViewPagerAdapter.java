package me.nicholas.gank.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.util.List;

import me.nicholas.gank.R;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Nicholas on 2016/7/9.
 */
public class MeizhiViewPagerAdapter extends PagerAdapter {

    private List<String> urls;
    private Context context;
    private LayoutInflater inflater;
    private ClickListener listener;
    private LongClickListener longListener;

    public MeizhiViewPagerAdapter(Context context, List<String> urls) {
        this.context = context;
        this.urls = urls;
        inflater = LayoutInflater.from(context);
    }

    public interface ClickListener {
        void onClick(View view, int pos, float v, float v1);
    }

    public interface LongClickListener {
        void onLongClick(View v, int pos);
    }

    public void setClickListener(ClickListener listener) {
        this.listener = listener;
    }

    public void setLongClickListener(LongClickListener longListener) {
        this.longListener = longListener;
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        View view = inflater.inflate(R.layout.item_viewpager_meizhi, container, false);
        PhotoView photoView = (PhotoView) view.findViewById(R.id.photo_view);

        //单击事件
        photoView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View view, float v, float v1) {
                if (listener != null) {
                    listener.onClick(view, position, v, v1);
                }
            }

            @Override
            public void onOutsidePhotoTap() {
                //空白处点击事件
            }
        });

        photoView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (longListener != null) {
                    longListener.onLongClick(view, position);
                }
                return true;
            }
        });

        Picasso.with(context)
                .load(urls.get(position))
                .into(photoView);

        container.addView(photoView, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        return photoView;
    }

    @Override
    public int getCount() {
        return urls == null ? 0 : urls.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
