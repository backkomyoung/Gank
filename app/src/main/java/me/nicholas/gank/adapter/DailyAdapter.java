package me.nicholas.gank.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.nicholas.gank.R;
import me.nicholas.gank.bean.Gank;


/**
 * Created by Nicholas on 2016/6/22.
 */
public class DailyAdapter extends RecyclerView.Adapter<DailyAdapter.ViewHolder> {

    private List<Gank> ganks;

    private onItemClickListener listener;

    public interface onItemClickListener {
        void onItemClick(View view, String title, String url, int position);
    }

    public void setItemClickListener(onItemClickListener listener) {
        this.listener = listener;
    }

    public void setDatas(List<Gank> ganks) {
        if (ganks == null)
            return;
        this.ganks = ganks;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_gank, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        if (ganks != null) {

            Gank gank = ganks.get(position);

            if (position == 0) {
                showCategory(holder);
            } else {
                boolean compareCategory = ganks.get(position - 1)
                        .getType().equals(gank.getType());
                if (!compareCategory) {
                    showCategory(holder);
                } else {
                    hideCategory(holder);
                }
            }

            holder.mCategory.setText(gank.getType());
            String title = gank.getDesc() + "(" + gank.getWho() + ")";
            holder.mTitle.setText(title);

        }

    }

    @Override
    public int getItemCount() {
        return ganks == null ? 0 : ganks.size();
    }

    private void showCategory(ViewHolder viewHolder) {
        if (!isVisibility(viewHolder.mCategory)) {
            viewHolder.mCategory.setVisibility(View.VISIBLE);
        }
    }

    private void hideCategory(ViewHolder viewHolder) {
        if (isVisibility(viewHolder.mCategory)) {
            viewHolder.mCategory.setVisibility(View.GONE);
        }
    }

    private boolean isVisibility(View view) {
        return view.getVisibility() == View.VISIBLE;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.gank_category)
        TextView mCategory;
        @Bind(R.id.gank_title)
        TextView mTitle;


        public ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        @OnClick(R.id.gank_ll)
        public void onItemClick(View view) {
            if (listener != null) {
                Gank gank = ganks.get(getLayoutPosition());
                listener.onItemClick(view, gank.getDesc(), gank.getUrl(), getAdapterPosition());
            }

        }
    }
}
