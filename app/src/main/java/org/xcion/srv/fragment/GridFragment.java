package org.xcion.srv.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.xcion.lib.SwipeRecyclerView;
import com.xcion.lib.divider.HorizontalDividerItemDecoration;
import com.xcion.lib.divider.VerticalDividerItemDecoration;


import org.xcion.srv.R;
import org.xcion.srv.adapter.RecyclerAdapter;
import org.xcion.srv.bean.NewsBean;

import java.util.ArrayList;
import java.util.List;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * @Author: Kern Hu
 * @E-mail:
 * @CreateDate: 2020/9/22 14:11
 * @UpdateUser: Kern Hu
 * @UpdateDate: 2020/9/22 14:11
 * @Version: 1.0
 * @Description:
 * @UpdateRemark:
 */
public class GridFragment extends Fragment implements SwipeRecyclerView.OnRefreshListener, SwipeRecyclerView.OnLoadMoreListener {

    @BindView(R.id.load_refresh_recycler_view)
    SwipeRecyclerView mSwipeRecyclerView;

    Unbinder mUnbinder;
    private RecyclerAdapter mRecyclerAdapter;

    private int position;
    private String title;
    private int page = 1;

    public static GridFragment getInstance(int position, String title) {
        GridFragment fragment = new GridFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("position", position);
        bundle.putString("title", title);
        fragment.setArguments(bundle);
        return fragment;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Bundle bundle = getArguments();
        position = bundle.getInt("position");
        title = bundle.getString("title");

        View view = inflater.inflate(R.layout.fragment_linear, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        initView(view);

        return view;
    }

    private void initView(View view) {

        mSwipeRecyclerView.setColorSchemeResources(android.R.color.holo_orange_dark, android.R.color.holo_purple, android.R.color.holo_green_dark);
        GridLayoutManager mGridLayoutManager = new GridLayoutManager(getContext(), 2, GridLayoutManager.VERTICAL, false);
        mSwipeRecyclerView.getRecyclerView().setLayoutManager(mGridLayoutManager);
        mSwipeRecyclerView.getRecyclerView().setItemAnimator(new DefaultItemAnimator());
        mSwipeRecyclerView.getRecyclerView().addItemDecoration(new HorizontalDividerItemDecoration
                .Builder(getContext())
                .colorResId(android.R.color.darker_gray)
                .sizeResId(R.dimen.divider_line_height)
                .margin(10, 10)
                .showLastDivider()
                .build());
        mSwipeRecyclerView.getRecyclerView().addItemDecoration(new VerticalDividerItemDecoration
                .Builder(getContext())
                .colorResId(android.R.color.darker_gray)
                .sizeResId(R.dimen.divider_line_height)
                .margin(10, 10)
                .showLastDivider()
                .build());

        mRecyclerAdapter = new RecyclerAdapter(getContext(), null);
        mSwipeRecyclerView.setAdapter(mRecyclerAdapter);

        mSwipeRecyclerView.setOnRefreshListener(this);
        mSwipeRecyclerView.setOnLoadMoreListener(this, 10);

        //自动刷新
        mSwipeRecyclerView.setAutoRefresh();
    }


    private List<NewsBean> getList(int page) {
        int count = page * 20 - 20;
        int max = page * 20;
        List<NewsBean> list = new ArrayList<>();
        for (int i = count; i < max; i++) {
            list.add(new NewsBean(NewsBean.TYPE_GRID, "grid item >>>>>" + i));
        }
        return list;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mUnbinder != null) {
            mUnbinder.unbind();
        }
    }

    @Override
    public void onRefresh() {
        page = 1;
        mSwipeRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecyclerAdapter.setUpdate(getList(page), page == 1);
                mSwipeRecyclerView.setRefreshing(false, true);
            }
        }, 1000);
    }


    @Override
    public void onLoadMore(int overallItemsCount, int lastVisibleItemPosition) {
        page++;
        mSwipeRecyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                mRecyclerAdapter.setUpdate(getList(page), page == 1);
                mSwipeRecyclerView.setLoadingMore(false, true);
            }
        }, 500);
    }
}
