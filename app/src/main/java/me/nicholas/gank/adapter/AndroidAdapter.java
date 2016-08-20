package me.nicholas.gank.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import circletextimage.viviant.com.circletextimagelib.view.CircleTextImage;
import me.nicholas.gank.R;
import me.nicholas.gank.bean.Common;
import me.nicholas.gank.utils.DateUtils;

/**
 * Created by Nicholas on 2016/8/15.
 */
public class AndroidAdapter extends RecyclerView.Adapter<AndroidAdapter.ViewHolder> {


    private List<Common> lists;

    public AndroidAdapter(List<Common> lists) {
        this.lists=lists;
    }

    private onItemClickListener listener;

    public interface onItemClickListener {
        void onItemClick(View view, String title, String url, int position);
    }

    public void setItemClickListener(onItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_android, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (lists == null) {
            return;
        }

        Common data=lists.get(position);

        if (position==0){
            showMonth(holder);
        }else {

            String lastMonth=DateUtils.getGankDate2Show(lists.get(position - 1).getPublishedAt(),DateUtils.MONTH);
            String nextMonth=DateUtils.getGankDate2Show(data.getPublishedAt(),DateUtils.MONTH);

            if (lastMonth != null) {
                boolean compareCategory = lastMonth.equals(nextMonth);

                if (compareCategory){
                    hideMonth(holder);
                }else {
                    showMonth(holder);
                }
            }
        }

        holder.tvMonth.setText(DateUtils.getGankDate2Show(data.getPublishedAt(),DateUtils.MONTH)+"月");
        holder.imgTv.setText4CircleImage(data.getDesc());
        holder.tvTitle.setText(data.getDesc());
        holder.tvDate.setText(DateUtils.getGankDate2Show(data.getPublishedAt(),DateUtils.ALL));

    }

    private void showMonth(ViewHolder viewHolder) {
        if (!isVisibility(viewHolder.tvMonth)) {
            viewHolder.tvMonth.setVisibility(View.VISIBLE);
        }
    }

    private void hideMonth(ViewHolder viewHolder) {
        if (isVisibility(viewHolder.tvMonth)) {
            viewHolder.tvMonth.setVisibility(View.GONE);
        }
    }

    private boolean isVisibility(View view) {
        return view.getVisibility() == View.VISIBLE;
    }

    @Override
    public int getItemCount() {
        return lists == null ? 0 : lists.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.circleTextImage)
        CircleTextImage imgTv;
        @Bind(R.id.title)
        TextView tvTitle;
        @Bind(R.id.date)
        TextView tvDate;
        @Bind(R.id.item_rl)
        RelativeLayout itemRl;
        @Bind(R.id.date_month)
        TextView tvMonth;

        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }

        @OnClick(R.id.item_rl)
        public void onItemClick(View v){
            if (listener==null){
                return;
            }
            Common data=lists.get(getLayoutPosition());
            listener.onItemClick(v,data.getDesc(),data.getUrl(),getAdapterPosition());
        }
    }
}
