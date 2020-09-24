package com.xcion.lib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

/**
 * Author: Kern
 * E-mail: sky580@126.com
 * DateTime: 2020/9/25  00:05
 * Intro:
 */
public class SwipeRecyclerView extends SwipeRefreshLayout {

    private FrameLayout mParentContainer;
    private RecyclerView mRecyclerView;
    private View mLoadMoreView;
    private View mLoadingView;
    private View mEmptyView;
    private View mErrorView;

    private RecyclerView.Adapter mAdapter;
    private OnRefreshListener mOnRefreshListener;
    private OnLoadMoreListener mLoadMoreListener;

    private boolean mAutoCompleteRefreshing;
    private boolean mAutoCompleteLoadingMore;
    private boolean mClipToPadding;
    private boolean mScrollBarEnabled;
    private int mPadding;
    private int mPaddingTop;
    private int mPaddingBottom;
    private int mPaddingLeft;
    private int mPaddingRight;

    private int mLoadingViewId;
    private int mLoadMoreViewId;
    private int mEmptyViewId;
    private int mErrorViewId;

    protected LayoutManagerType mLayoutManagerType;
    private int[] mLastScrollPositions;
    private int LOAD_MORE_MAX_ITEM_COUNT = 10;
    private boolean isLoadingMore = false;

    private enum State {
        LOADING,
        SUCCESS,
        EMPTY,
        ERROR
    }

    private enum LayoutManagerType {
        LINEAR,
        GRID,
        STAGGERED_GRID
    }

    public SwipeRecyclerView(@NonNull Context context) {
        this(context, null);
    }

    public SwipeRecyclerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SwipeRecyclerView);
        try {
            mClipToPadding = a.getBoolean(R.styleable.SwipeRecyclerView_srv_clipToPadding, false);
            mAutoCompleteRefreshing = a.getBoolean(R.styleable.SwipeRecyclerView_srv_autoCompleteRefreshing, false);
            mAutoCompleteLoadingMore = a.getBoolean(R.styleable.SwipeRecyclerView_srv_autoCompleteLoadingMore, false);
            mScrollBarEnabled = a.getBoolean(R.styleable.SwipeRecyclerView_srv_scrollbar_enable, false);

            mPadding = (int) a.getDimension(R.styleable.SwipeRecyclerView_srv_padding, 0.0f);
            mPaddingTop = (int) a.getDimension(R.styleable.SwipeRecyclerView_srv_paddingTop, 0.0f);
            mPaddingBottom = (int) a.getDimension(R.styleable.SwipeRecyclerView_srv_paddingBottom, 0.0f);
            mPaddingLeft = (int) a.getDimension(R.styleable.SwipeRecyclerView_srv_paddingLeft, 0.0f);
            mPaddingRight = (int) a.getDimension(R.styleable.SwipeRecyclerView_srv_paddingRight, 0.0f);

            mLoadingViewId = a.getResourceId(R.styleable.SwipeRecyclerView_srv_loading_layout, R.layout.srv_layout_loading_view);
            mLoadMoreViewId = a.getResourceId(R.styleable.SwipeRecyclerView_srv_loadmore_layout, R.layout.srv_layout_loadmore_view);
            mEmptyViewId = a.getResourceId(R.styleable.SwipeRecyclerView_srv_empty_layout, R.layout.srv_layout_empty_view);
            mErrorViewId = a.getResourceId(R.styleable.SwipeRecyclerView_srv_error_layout, R.layout.srv_layout_error_view);
        } finally {
            a.recycle();
        }

        initParentView();
        initChildView();
    }

    private void initParentView() {

        ViewGroup.LayoutParams mParentContainerParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mParentContainer = new FrameLayout(getContext());
        this.addView(mParentContainer, mParentContainerParams);

        this.setColorSchemeResources(android.R.color.holo_blue_light, android.R.color.holo_red_light, android.R.color.holo_orange_light);
    }

    private void initChildView() {

        initRecyclerView();

        //load more view
        FrameLayout.LayoutParams mLoadMoreViewParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        mLoadMoreView = ViewGroup.inflate(getContext(), mLoadMoreViewId, null);
        mLoadMoreViewParams.gravity = Gravity.BOTTOM;
        mParentContainer.addView(mLoadMoreView, mLoadMoreViewParams);

        //loading view
        FrameLayout.LayoutParams mLoadingViewParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mLoadingView = ViewGroup.inflate(getContext(), mLoadingViewId, null);
        mParentContainer.addView(mLoadingView, mLoadingViewParams);

        //empty view
        FrameLayout.LayoutParams mEmptyViewParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mEmptyViewParams.gravity = Gravity.CENTER;
        mEmptyView = ViewGroup.inflate(getContext(), mEmptyViewId, null);

        //error view
        FrameLayout.LayoutParams mErrorViewParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mErrorViewParams.gravity = Gravity.CENTER;
        mErrorView = ViewGroup.inflate(getContext(), mErrorViewId, null);

        changeStateSetupUI(State.LOADING);
    }

    private void initRecyclerView() {
        //recycler view
        FrameLayout.LayoutParams mRecyclerParams = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        mRecyclerView = new RecyclerView(getContext());
        mRecyclerView.setClipToPadding(mClipToPadding);
        mRecyclerView.setVerticalScrollBarEnabled(mScrollBarEnabled);
        mRecyclerView.setHorizontalScrollBarEnabled(mScrollBarEnabled);
        mRecyclerView.setOverScrollMode(View.OVER_SCROLL_ALWAYS);
        mRecyclerView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        try {
            @SuppressLint("PrivateApi")
            Method method = View.class.getDeclaredMethod("initializeScrollbars", TypedArray.class);
            method.setAccessible(true);
            method.invoke(mRecyclerView, (Object) null);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

        mRecyclerView.setPadding(mPaddingLeft == 0 ? mPadding : mPaddingLeft,
                mPaddingTop == 0 ? mPadding : mPaddingTop,
                mPaddingRight == 0 ? mPadding : mPaddingRight,
                mPaddingBottom == 0 ? mPadding : mPaddingBottom);
        mParentContainer.addView(mRecyclerView, mRecyclerParams);
        mRecyclerView.addOnScrollListener(mOnScrollListener);

    }


    /**********************************************************************************************/
    /**********************************************************************************************/
    /**********************************************************************************************/
    RecyclerView.AdapterDataObserver mAdapterDataObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            super.onChanged();
            setAdapterChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            super.onItemRangeChanged(positionStart, itemCount);
            setAdapterChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount, @Nullable Object payload) {
            super.onItemRangeChanged(positionStart, itemCount, payload);
            setAdapterChanged();
        }

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            super.onItemRangeInserted(positionStart, itemCount);
            setAdapterChanged();
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            super.onItemRangeRemoved(positionStart, itemCount);
            setAdapterChanged();
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            super.onItemRangeMoved(fromPosition, toPosition, itemCount);
            setAdapterChanged();
        }

        @Override
        public void onStateRestorationPolicyChanged() {
            super.onStateRestorationPolicyChanged();
            setAdapterChanged();
        }
    };

    /**
     *
     */
    RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            int lastVisibleItemPosition = getLastVisibleItemPosition(layoutManager);
            int visibleItemCount = layoutManager.getChildCount();
            int totalItemCount = layoutManager.getItemCount();
            if (((totalItemCount - lastVisibleItemPosition) <= LOAD_MORE_MAX_ITEM_COUNT ||
                    (totalItemCount - lastVisibleItemPosition) == 0 && totalItemCount > visibleItemCount)
                    && !isLoadingMore) {
                isLoadingMore = true;
                if (mLoadMoreListener != null) {
                    setLoadMoreState(true);
                    mLoadMoreListener.onLoadMore(recyclerView.getAdapter().getItemCount(), lastVisibleItemPosition);
                }
            }
        }

        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    };

    /**
     *
     */
    private void setAdapterChanged() {
        if (mAutoCompleteLoadingMore && isLoadingMore) {
            setLoadingMore(false);
        }
        if (mAutoCompleteRefreshing && isRefreshing()) {
            setRefreshing(false);
        }

        if (mAdapter.getItemCount() == 0) {
            changeStateSetupUI(State.EMPTY);
        } else {
            changeStateSetupUI(State.SUCCESS);
        }
        isLoadingMore = false;
    }

    /**
     * @param layoutManager
     * @return
     */
    private int getLastVisibleItemPosition(RecyclerView.LayoutManager layoutManager) {
        int lastVisibleItemPosition = -1;
        if (mLayoutManagerType == null) {
            if (layoutManager instanceof GridLayoutManager) {
                mLayoutManagerType = LayoutManagerType.GRID;
            } else if (layoutManager instanceof LinearLayoutManager) {
                mLayoutManagerType = LayoutManagerType.LINEAR;
            } else if (layoutManager instanceof StaggeredGridLayoutManager) {
                mLayoutManagerType = LayoutManagerType.STAGGERED_GRID;
            } else {
                throw new RuntimeException("Can't use" + layoutManager.getClass().getName() + ",just support LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager.");
            }
        }

        switch (mLayoutManagerType) {
            case LINEAR:
                lastVisibleItemPosition = ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition();
                break;
            case GRID:
                lastVisibleItemPosition = ((GridLayoutManager) layoutManager).findLastVisibleItemPosition();
                break;
            case STAGGERED_GRID:
                lastVisibleItemPosition = caseStaggeredGrid(layoutManager);
                break;
        }
        return lastVisibleItemPosition;
    }

    /**
     * @param layoutManager
     * @return
     */
    private int caseStaggeredGrid(RecyclerView.LayoutManager layoutManager) {
        StaggeredGridLayoutManager staggeredGridLayoutManager = (StaggeredGridLayoutManager) layoutManager;
        if (mLastScrollPositions == null)
            mLastScrollPositions = new int[staggeredGridLayoutManager.getSpanCount()];
        staggeredGridLayoutManager.findLastVisibleItemPositions(mLastScrollPositions);
        return findMax(mLastScrollPositions);
    }

    /**
     * @param lastPositions
     * @return
     */
    private int findMax(int[] lastPositions) {
        int max = Integer.MIN_VALUE;
        for (int value : lastPositions) {
            if (value > max)
                max = value;
        }
        return max;
    }

    /**********************************************************************************************/
    /**********************************************************************************************/
    /**********************************************************************************************/
    /**
     * @param state
     */
    protected void changeStateSetupUI(State state) {

        switch (state) {

            case LOADING:

                if (mLoadingView != null) {
                    if (mParentContainer.indexOfChild(mLoadingView) == -1) {
                        mParentContainer.addView(mLoadingView);
                    }
                    mLoadingView.setVisibility(View.VISIBLE);
                }
                if (mRecyclerView != null) {
                    mRecyclerView.setVisibility(View.GONE);
                }
                if (mLoadMoreView != null) {
                    mLoadMoreView.setVisibility(View.GONE);
                }

                break;
            case SUCCESS:

                if (mRecyclerView != null) {
                    if (mParentContainer.indexOfChild(mRecyclerView) == -1) {
                        mParentContainer.addView(mRecyclerView);
                    }
                    mRecyclerView.setVisibility(View.VISIBLE);
                }
                if (mLoadMoreView != null) {
                    if (mParentContainer.indexOfChild(mLoadMoreView) == -1) {
                        mParentContainer.addView(mLoadMoreView);
                    }
                    mLoadMoreView.setVisibility(View.GONE);
                }
                if (mLoadingView != null && mParentContainer.indexOfChild(mLoadingView) != -1) {
                    mParentContainer.removeView(mLoadingView);
                }

                if (mEmptyView != null && mParentContainer.indexOfChild(mEmptyView) != -1) {
                    mParentContainer.removeView(mEmptyView);
                }
                if (mErrorView != null && mParentContainer.indexOfChild(mErrorView) != -1) {
                    mParentContainer.removeView(mErrorView);
                }

                break;
            case EMPTY:

                if (mEmptyView != null) {
                    if (mParentContainer.indexOfChild(mEmptyView) == -1) {
                        mParentContainer.addView(mEmptyView);
                    }
                    mEmptyView.setVisibility(View.VISIBLE);
                }
                if (mRecyclerView != null) {
                    if (mParentContainer.indexOfChild(mRecyclerView) != -1)
                        mParentContainer.removeView(mRecyclerView);
                    mRecyclerView.setVisibility(View.GONE);
                }
                if (mLoadMoreView != null) {
                    if (mParentContainer.indexOfChild(mLoadMoreView) != -1)
                        mParentContainer.removeView(mLoadMoreView);
                    mLoadMoreView.setVisibility(View.GONE);
                }
                if (mErrorView != null) {
                    if (mParentContainer.indexOfChild(mErrorView) != -1)
                        mParentContainer.removeView(mErrorView);
                    mErrorView.setVisibility(View.GONE);
                }
                if (mLoadingView != null) {
                    if (mParentContainer.indexOfChild(mLoadingView) != -1)
                        mParentContainer.removeView(mLoadingView);
                    mLoadingView.setVisibility(View.GONE);
                }

                break;
            case ERROR:

                if (mErrorView != null) {
                    if (mParentContainer.indexOfChild(mErrorView) == -1) {
                        mParentContainer.addView(mErrorView);
                    }
                    mErrorView.setVisibility(View.VISIBLE);
                }

                if (mRecyclerView != null) {
                    if (mParentContainer.indexOfChild(mRecyclerView) != -1)
                        mParentContainer.removeView(mRecyclerView);
                    mRecyclerView.setVisibility(View.GONE);
                }
                if (mLoadMoreView != null) {
                    if (mParentContainer.indexOfChild(mLoadMoreView) != -1)
                        mParentContainer.removeView(mLoadMoreView);
                    mLoadMoreView.setVisibility(View.GONE);
                }
                if (mEmptyView != null) {
                    if (mParentContainer.indexOfChild(mEmptyView) != -1)
                        mParentContainer.removeView(mEmptyView);
                    mEmptyView.setVisibility(View.GONE);
                }
                if (mLoadingView != null) {
                    if (mParentContainer.indexOfChild(mLoadingView) != -1)
                        mParentContainer.removeView(mLoadingView);
                    mLoadingView.setVisibility(View.GONE);
                }

                break;
        }
    }

    /**
     * @param isLoadingMore if loading more or not
     */
    private void setLoadMoreState(boolean isLoadingMore) {
        mLoadMoreView.setVisibility(isLoadingMore ? View.VISIBLE : View.GONE);
    }


    /**********************************************************************************************/
    public SwipeRefreshLayout getSwipeRefreshLayout() {
        return this;
    }

    public RecyclerView getRecyclerView() {
        return mRecyclerView;
    }

    public View getLoadingView() {
        return mLoadingView;
    }

    public View getEmptyView() {
        return mEmptyView;
    }

    public View getLoadMoreView() {
        return mLoadMoreView;
    }

    public View getErrorView() {
        return mErrorView;
    }

    /**
     * set the adapter of the recycler view
     *
     * @param adapter
     */
    public void setAdapter(RecyclerView.Adapter adapter) {
        if (mRecyclerView != null && adapter != null) {
            mAdapter = adapter;
            mRecyclerView.setAdapter(adapter);
            adapter.registerAdapterDataObserver(mAdapterDataObserver);
        }
    }

    /**
     * auto refresh the swipe layout and listener
     */
    public void setAutoRefresh() {
        this.post(new RefreshRunnable());
    }

    protected class RefreshRunnable implements Runnable {

        @Override
        public void run() {
            SwipeRecyclerView.this.setRefreshing(true);
            if (mOnRefreshListener != null) {
                mOnRefreshListener.onRefresh();
            }
        }
    }

    /**
     * @param refreshing refresh or not
     */
    @Override
    public void setRefreshing(boolean refreshing) {
        this.setRefreshing(refreshing, true);
    }

    /**
     * @param refreshing refresh or not
     * @param success    refresh success or not
     */
    public void setRefreshing(boolean refreshing, boolean success) {
        if (refreshing) {
            super.setRefreshing(true);
        } else {
            while (isRefreshing()) {
                super.setRefreshing(false);
            }
        }

        if (mAdapter != null && mAdapter.getItemCount() == 0 && !success) {
            changeStateSetupUI(State.ERROR);
        }
    }

    /**
     * @param enable load more or not
     */
    public void setLoadingMore(boolean enable) {
        this.setLoadingMore(enable, true);
    }

    /**
     * @param enable  load more or not
     * @param success load more success or not
     */
    public void setLoadingMore(boolean enable, boolean success) {
        mLoadMoreView.setVisibility(enable ? View.VISIBLE : View.GONE);
        if (mAdapter != null && mAdapter.getItemCount() == 0 && !success) {
            changeStateSetupUI(State.ERROR);
        }
    }

    /**
     * @return get load mare max item count
     */
    public int getMaxItemCount() {
        return LOAD_MORE_MAX_ITEM_COUNT;
    }

    /**********************************************************************************************/
    @Override
    public void setOnRefreshListener(@Nullable OnRefreshListener listener) {
        this.mOnRefreshListener = listener;
        super.setOnRefreshListener(listener);
    }

    /**
     * @param loadMoreListener the load more listener
     * @param maxItemCount     when scroll to the max item count then begin  to load more items;
     */
    public void setOnLoadMoreListener(@Nullable OnLoadMoreListener loadMoreListener, int maxItemCount) {
        this.mLoadMoreListener = loadMoreListener;
        this.LOAD_MORE_MAX_ITEM_COUNT = maxItemCount;
    }

    /**********************************************************************************************/
    public interface OnLoadMoreListener {
        void onLoadMore(int overallItemsCount, int lastVisibleItemPosition);
    }
    /**********************************************************************************************/
}