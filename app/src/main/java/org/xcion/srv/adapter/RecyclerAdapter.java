package org.xcion.srv.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.xcion.srv.R;
import org.xcion.srv.bean.NewsBean;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Kern Hu
 * @E-mail:
 * @CreateDate: 2020/9/22 18:08
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/9/22 18:08
 * @Version: 1.0
 * @Description:
 * @UpdateRemark:
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context mContext;
    private List<NewsBean> mData;

    public RecyclerAdapter(Context mContext, List<NewsBean> data) {
        this.mContext = mContext;
        this.mData = new ArrayList<>();
        if (data != null && !data.isEmpty()) {
            mData.addAll(data);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).getType();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = null;
        switch (viewType) {
            case NewsBean.TYPE_LINEAR:
                view = ViewGroup.inflate(mContext, R.layout.item_linear, null);
                return new LinearViewHolder(view);
            case NewsBean.TYPE_GRID:
                view = ViewGroup.inflate(mContext, R.layout.item_grid, null);
                return new GridViewHolder(view);
            case NewsBean.TYPE_STAGGERED_GRID1:
                view = ViewGroup.inflate(mContext, R.layout.item_staggered_grid1, null);
                return new StaggeredGrid1ViewHolder(view);
            case NewsBean.TYPE_STAGGERED_GRID2:
                view = ViewGroup.inflate(mContext, R.layout.item_staggered_grid2, null);
                return new StaggeredGrid2ViewHolder(view);
            case NewsBean.TYPE_STAGGERED_GRID3:
                view = ViewGroup.inflate(mContext, R.layout.item_staggered_grid3, null);
                return new StaggeredGrid3ViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof LinearViewHolder) {
            LinearViewHolder lvh = (LinearViewHolder) holder;
            lvh.mLinearTitle.setText(mData.get(position).getTitle());
        } else if (holder instanceof GridViewHolder) {
            GridViewHolder gvh = (GridViewHolder) holder;
            gvh.mGridTitle.setText(mData.get(position).getTitle());
        } else if (holder instanceof StaggeredGrid1ViewHolder) {
            StaggeredGrid1ViewHolder svh1 = (StaggeredGrid1ViewHolder) holder;
            svh1.mStaggered1Title.setText(mData.get(position).getTitle());
        } else if (holder instanceof StaggeredGrid2ViewHolder) {
            StaggeredGrid2ViewHolder svh2 = (StaggeredGrid2ViewHolder) holder;
            svh2.mStaggered2Title.setText(mData.get(position).getTitle());
        } else if (holder instanceof StaggeredGrid3ViewHolder) {
            StaggeredGrid3ViewHolder svh3 = (StaggeredGrid3ViewHolder) holder;
            svh3.mStaggered3Title.setText(mData.get(position).getTitle());
        }

    }

    @Override
    public int getItemCount() {
        return mData.size();
    }


    public void setUpdate(List<NewsBean> data, boolean isRefresh) {
        if (data != null) {
            if (isRefresh) {
                mData.clear();
            }
            mData.addAll(data);
            notifyDataSetChanged();
        }
    }


    class LinearViewHolder extends RecyclerView.ViewHolder {

        private TextView mLinearTitle;

        public LinearViewHolder(@NonNull View itemView) {
            super(itemView);
            mLinearTitle = itemView.findViewById(R.id.linear_title);
        }
    }

    class GridViewHolder extends RecyclerView.ViewHolder {

        private TextView mGridTitle;

        public GridViewHolder(@NonNull View itemView) {
            super(itemView);
            mGridTitle = itemView.findViewById(R.id.grid_title);
        }
    }

    class StaggeredGrid1ViewHolder extends RecyclerView.ViewHolder {

        private TextView mStaggered1Title;

        public StaggeredGrid1ViewHolder(@NonNull View itemView) {
            super(itemView);
            mStaggered1Title = itemView.findViewById(R.id.staggered_grid1_title);
        }
    }

    class StaggeredGrid2ViewHolder extends RecyclerView.ViewHolder {

        private TextView mStaggered2Title;

        public StaggeredGrid2ViewHolder(@NonNull View itemView) {
            super(itemView);
            mStaggered2Title = itemView.findViewById(R.id.staggered_grid2_title);
        }
    }

    class StaggeredGrid3ViewHolder extends RecyclerView.ViewHolder {

        private TextView mStaggered3Title;

        public StaggeredGrid3ViewHolder(@NonNull View itemView) {
            super(itemView);
            mStaggered3Title = itemView.findViewById(R.id.staggered_grid3_title);
        }
    }
}
