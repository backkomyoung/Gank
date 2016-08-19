package me.nicholas.gank.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.nicholas.gank.R;
import me.nicholas.gank.bean.Common;
import me.nicholas.gank.widget.RatioImageview;


/**
 * Created by Nicholas on 2016/6/23.
 */
public class MeizhiAdapter extends RecyclerView.Adapter<MeizhiAdapter.ViewHolder> {

    private Context context;
    private LayoutInflater inflater;
    private List<Common> meizhis;

    private MeizhiClickListener listener;

    public MeizhiAdapter(Context context,List<Common> meizhis) {
        this.context = context;
        this.meizhis=meizhis;
        inflater = LayoutInflater.from(context);
    }

    public interface MeizhiClickListener {
        void onClick(View v, int position);
    }

    public void setMeizhiClickListener(MeizhiClickListener listener) {
        this.listener = listener;
    }

//    public void onRefresh(List<Common> meizhis) {
//        if (meizhis == null)
//            return;
//        this.meizhis = meizhis;
//    }
//
//    public void onLoadMore(List<Common> meizhis) {
//        if (meizhis == null)
//            return;
//        for (Common meizhi : meizhis) {
//            this.meizhis.add(meizhi);
//        }
//    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_meizhi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (meizhis == null) {
            return;
        }

        Common common = meizhis.get(position);

        holder.mTitle.setText(common.getDesc());
        // holder.mDate.setText(common.getPublishedAt());

        Glide.with(context)
                .load(common.getUrl())
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.meizhiImg);

    }

    @Override
    public int getItemCount() {
        return meizhis == null ? 0 : meizhis.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.meizhi_img)
        RatioImageview meizhiImg;
        @Bind(R.id.meizhi_title)
        TextView mTitle;
//        @Bind(R.id.meizhi_date)
//        TextView mDate;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            meizhiImg.setOriginalSize(50, 60);
        }

        @OnClick(R.id.meizhi_img)
        public void onMeizhiClick(View view) {
            if (listener != null) {
                listener.onClick(view, getAdapterPosition());
            }
        }

    }
}
