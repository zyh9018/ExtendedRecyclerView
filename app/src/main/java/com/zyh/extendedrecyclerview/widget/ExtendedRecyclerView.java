package com.zyh.extendedrecyclerview.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import java.util.ArrayList;

/**
 * 具有下拉刷新和上拉加载更多功能的RecyclerView
 * <p>
 * Created by Oscar-Zhang on 2018-2-12.
 */
public class ExtendedRecyclerView extends RecyclerView {

    private ExtendedRecyclerViewRefreshHeaderView mRefreshHeaderView;
    private ExtendedRecyclerViewLoadMoreFooterView mLoadMoreFooterView;
    private ArrayList<View> mHeaderViews = new ArrayList<>();
    private ArrayList<View> mFootViews = new ArrayList<>();
    protected Adapter mAdapter;

    private int mRefreshHeaderViewOriginHeight = 0;
    private int mRefreshHeaderViewHeight = 0;
    private boolean mRefreshEnabled = false;

    public ExtendedRecyclerView(Context context) {
        super(context);
        init();
    }

    public ExtendedRecyclerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ExtendedRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public void addHeaderView(View view) {
        mHeaderViews.add(view);
        if (mAdapter != null) {
            if (!(mAdapter instanceof HeaderViewRecyclerAdapter)) {
                mAdapter = new HeaderViewRecyclerAdapter(mAdapter, mHeaderViews, mFootViews);
            }
        }
    }

    public void addFootView(View view) {
        mFootViews.add(view);
        if (mAdapter != null) {
            if (!(mAdapter instanceof HeaderViewRecyclerAdapter)) {
                mAdapter = new HeaderViewRecyclerAdapter(mAdapter, mHeaderViews, mFootViews);
            }
        }
    }

    public void setRefreshEnabled(boolean mRefreshEnabled) {
        this.mRefreshEnabled = mRefreshEnabled;
    }

    /**
     * 设置下拉刷新用的Header
     *
     * @param refreshHeader TRecyclerViewRefreshHeaderView
     */
    public void setRefreshHeader(ExtendedRecyclerViewRefreshHeaderView refreshHeader) {
        this.mRefreshHeaderView = refreshHeader;
        if (mRefreshHeaderView.getLayoutParams() == null) {
            mRefreshHeaderView.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        }
        measureHeaderView();
        mRefreshHeaderViewOriginHeight = mRefreshHeaderView.getMeasuredHeight();
        setRefreshHeaderViewHeight(0);
        mHeaderViews.add(mRefreshHeaderView);
    }

    public void setLoadMoreFooter(ExtendedRecyclerViewLoadMoreFooterView footerView) {
        mLoadMoreFooterView = footerView;
        mFootViews.add(mLoadMoreFooterView);
    }

    /**
     * 设置完Header以及Footer之后调用
     *
     * @param adapter adapter
     */
    @Override
    public void setAdapter(Adapter adapter) {
        if (mHeaderViews.size() > 0 || mFootViews.size() > 0) {
            if (mAdapter != null) {
                if (!(mAdapter instanceof HeaderViewRecyclerAdapter)) {
                    mAdapter = new HeaderViewRecyclerAdapter(adapter, mHeaderViews, mFootViews);
                }
            } else {
                mAdapter = new HeaderViewRecyclerAdapter(adapter, mHeaderViews, mFootViews);
            }
        } else {
            mAdapter = adapter;
        }
        super.setAdapter(mAdapter);
    }

    private OnScrollLoadMoreListener mScrollLoadMoreListener = new OnScrollLoadMoreListener() {
        @Override
        public void onLoadMore(RecyclerView recyclerView) {
            if (mOnLoadMoreListener != null) {
                mOnLoadMoreListener.onLoadMore(recyclerView);
            }
        }
    };

    protected void init() {
        addOnScrollListener(mScrollLoadMoreListener);
    }

    // ====================== Refresh ====================

    private int mActivePointerId = -1;
    private int mLastTouchX = 0;
    private int mLastTouchY = 0;

    public static final int STATUS_DEFAULT = 100;
    public static final int STATUS_SWIPING_TO_REFRESH = 200;
    public static final int STATUS_RELEASE_TO_REFRESH = 300;
    public static final int STATUS_REFRESHING = 400;

    private int mRefreshState = STATUS_DEFAULT;

    public interface OnRefreshListener {
        void onRefresh(RecyclerView recyclerView);
    }

    private OnRefreshListener mOnRefreshListener;

    public void setOnRefreshListener(OnRefreshListener listener) {
        mOnRefreshListener = listener;
    }

    /**
     * 调用该方法会强制让界面处于下拉刷新状态并回回调onRefresh方法
     * call this method to force refresh data, which will call back onRefresh method.
     */
    public void startRefreshing() {
        setRefreshing(true);
    }

    public void resetRefreshing() {
        setRefreshing(false);
    }

    private void setRefreshing(boolean refreshing) {
        if (mRefreshState == STATUS_DEFAULT && refreshing) {
            mRefreshState = STATUS_RELEASE_TO_REFRESH;
            scrollHeaderToRefreshHeight();
        } else if (mRefreshState == STATUS_REFRESHING && !refreshing) {
            mRefreshState = STATUS_DEFAULT;
            scrollHeaderToDefaultHeight();
        }
    }

    private ValueAnimator mScrollAnimator;

    private void startScrollAnimation(final int time, final Interpolator interpolator, int value, int toValue) {
        if (mScrollAnimator == null) {
            mScrollAnimator = new ValueAnimator();
        }
        //cancel
        mScrollAnimator.removeAllUpdateListeners();
        mScrollAnimator.removeAllListeners();
        mScrollAnimator.cancel();

        //reset new value
        mScrollAnimator.setIntValues(value, toValue);
        mScrollAnimator.setDuration(time);
        mScrollAnimator.setInterpolator(interpolator);
        mScrollAnimator.addUpdateListener(mAnimatorUpdateListener);
        mScrollAnimator.addListener(mAnimationListener);
        mScrollAnimator.start();
    }

    Animator.AnimatorListener mAnimationListener = new Animator.AnimatorListener() {
        @Override
        public void onAnimationEnd(Animator animation) {
            switch (mRefreshState) {
                case STATUS_SWIPING_TO_REFRESH: {
                    mRefreshState = STATUS_DEFAULT;
                    scrollHeaderToDefaultHeight();
                }
                break;

                case STATUS_RELEASE_TO_REFRESH: {
                    setRefreshHeaderViewHeight(mRefreshHeaderViewOriginHeight);
                    mRefreshState = STATUS_REFRESHING;
                    if (mOnRefreshListener != null) {
                        mOnRefreshListener.onRefresh(ExtendedRecyclerView.this);
                        mRefreshHeaderView.onRefresh();
                    }
                }
                break;

                case STATUS_REFRESHING: {
                    setRefreshHeaderViewHeight(0);
                    mRefreshState = STATUS_DEFAULT;
                    mRefreshHeaderView.onReset();
                }
                break;
            }

        }

        @Override
        public void onAnimationStart(Animator animation) {
        }

        @Override
        public void onAnimationCancel(Animator animation) {
        }

        @Override
        public void onAnimationRepeat(Animator animation) {
        }
    };

    ValueAnimator.AnimatorUpdateListener mAnimatorUpdateListener = new ValueAnimator.AnimatorUpdateListener() {
        @Override
        public void onAnimationUpdate(ValueAnimator animation) {
            int heightValue = (Integer) animation.getAnimatedValue();
            if (heightValue < 0) {
                heightValue = 0;
            }
            final int height = heightValue;
            setRefreshHeaderViewHeight(height);
            switch (mRefreshState) {
                case STATUS_SWIPING_TO_REFRESH: {
                    mRefreshHeaderView.onMove(false, height);
                }
                break;

                case STATUS_RELEASE_TO_REFRESH: {
                    mRefreshHeaderView.onMove(false, height);
                }
                break;

                case STATUS_REFRESHING: {
                    mRefreshHeaderView.onMove(true, height);
                }
                break;
            }
        }
    };

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        final int action = MotionEventCompat.getActionMasked(e);
        final int actionIndex = MotionEventCompat.getActionIndex(e);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mActivePointerId = MotionEventCompat.getPointerId(e, 0);
                mLastTouchX = (int) (MotionEventCompat.getX(e, actionIndex) + 0.5f);
                mLastTouchY = (int) (MotionEventCompat.getY(e, actionIndex) + 0.5f);
                break;

            case MotionEvent.ACTION_POINTER_DOWN:
                mActivePointerId = MotionEventCompat.getPointerId(e, actionIndex);
                mLastTouchX = (int) (MotionEventCompat.getX(e, actionIndex) + 0.5f);
                mLastTouchY = (int) (MotionEventCompat.getY(e, actionIndex) + 0.5f);

                break;

            case MotionEventCompat.ACTION_POINTER_UP:
                onPointerUp(e);
                break;
        }

        return super.onInterceptTouchEvent(e);
    }

    private void onPointerUp(MotionEvent e) {
        final int actionIndex = MotionEventCompat.getActionIndex(e);
        if (MotionEventCompat.getPointerId(e, actionIndex) == mActivePointerId) {
            // Pick a new pointer to pick up the slack.
            final int newIndex = actionIndex == 0 ? 1 : 0;
            mActivePointerId = MotionEventCompat.getPointerId(e, newIndex);
            mLastTouchX = getMotionEventX(e, newIndex);
            mLastTouchY = getMotionEventY(e, newIndex);
        }
    }

    private int getMotionEventX(MotionEvent e, int pointerIndex) {
        return (int) (MotionEventCompat.getX(e, pointerIndex) + 0.5f);
    }

    private int getMotionEventY(MotionEvent e, int pointerIndex) {
        return (int) (MotionEventCompat.getY(e, pointerIndex) + 0.5f);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        final int action = MotionEventCompat.getActionMasked(e);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                final int index = MotionEventCompat.getActionIndex(e);
                mActivePointerId = MotionEventCompat.getPointerId(e, 0);
                mLastTouchX = getMotionEventX(e, index);
                mLastTouchY = getMotionEventY(e, index);

                break;

            case MotionEvent.ACTION_MOVE:
                final int pinterIndex = MotionEventCompat.findPointerIndex(e, mActivePointerId);
                if (pinterIndex < 0) {
                    return false;
                }

                final int x = getMotionEventX(e, pinterIndex);
                final int y = getMotionEventY(e, pinterIndex);

                final int dx = x - mLastTouchX;
                final int dy = y - mLastTouchY;

                mLastTouchX = x;
                mLastTouchY = y;

                final boolean triggerCondition = isEnabled() && mRefreshEnabled && mRefreshHeaderView != null && isFingerDragging() && canTriggerRefresh();
                if (triggerCondition) {
                    measureHeaderView();
                    final int refreshHeaderViewHeight = mRefreshHeaderView.getMeasuredHeight();

                    if (dy > 0 && mRefreshState == STATUS_DEFAULT) {
                        mRefreshState = STATUS_SWIPING_TO_REFRESH;
                        mRefreshHeaderView.onStart(refreshHeaderViewHeight, refreshHeaderViewHeight + 20);
                    } else if (dy < 0) {
                        if (mRefreshState == STATUS_SWIPING_TO_REFRESH && refreshHeaderViewHeight <= 0) {
                            mRefreshState = STATUS_DEFAULT;
                        }
                        if (mRefreshState == STATUS_DEFAULT) {
                            break;
                        }
                    }

                    if (mRefreshState == STATUS_SWIPING_TO_REFRESH || mRefreshState == STATUS_RELEASE_TO_REFRESH) {
                        if (mRefreshHeaderViewHeight >= mRefreshHeaderViewOriginHeight) {
                            mRefreshState = STATUS_RELEASE_TO_REFRESH;
                        } else {
                            mRefreshState = STATUS_SWIPING_TO_REFRESH;
                        }
                        if (fingerMove(dy)) {
                            return true;
                        } else {
                            super.onTouchEvent(e);
                        }
                    }
                }
                break;

            case MotionEventCompat.ACTION_POINTER_DOWN:
                final int actionIndex = MotionEventCompat.getActionIndex(e);
                mActivePointerId = MotionEventCompat.getPointerId(e, actionIndex);
                mLastTouchX = getMotionEventX(e, actionIndex);
                mLastTouchY = getMotionEventY(e, actionIndex);
                break;

            case MotionEventCompat.ACTION_POINTER_UP:
                onPointerUp(e);
                break;

            case MotionEvent.ACTION_UP:
                onFingerUpStartAnimating();
                break;

            case MotionEvent.ACTION_CANCEL:
                onFingerUpStartAnimating();
                break;
        }
        return super.onTouchEvent(e);
    }

    private boolean isFingerDragging() {
        return getScrollState() == SCROLL_STATE_DRAGGING;
    }

    public boolean canTriggerRefresh() {
        final Adapter adapter = getAdapter();
        if (adapter == null || adapter.getItemCount() <= 0) {
            return true;
        }
        View firstChild = getChildAt(0);
        int position = getChildLayoutPosition(firstChild);
        if (position == 0) {
            if (firstChild.getTop() == mRefreshHeaderView.getTop()) {
                return true;
            }
        }
        return false;
    }

    private void onFingerUpStartAnimating() {
        if (mRefreshState == STATUS_RELEASE_TO_REFRESH) {
            scrollHeaderToRefreshHeight();
        } else if (mRefreshState == STATUS_SWIPING_TO_REFRESH) {
            scrollHeaderToDefaultHeight();
        }
    }

    private void scrollHeaderToRefreshHeight() {
        measureHeaderView();
        mRefreshHeaderView.onStart(mRefreshHeaderView.getMeasuredHeight(), mRefreshHeaderView.getMeasuredHeight() + 20);

        int targetHeight = mRefreshHeaderViewOriginHeight;
        int currentHeight = mRefreshHeaderViewHeight;
        startScrollAnimation(200, new AccelerateInterpolator(), currentHeight, targetHeight);
    }

    private void scrollHeaderToDefaultHeight() {
        mRefreshHeaderView.onReset();
        final int targetHeight = 0;
        final int currentHeight = mRefreshHeaderViewHeight;
        startScrollAnimation(200, new DecelerateInterpolator(), currentHeight, targetHeight);
    }

    private boolean fingerMove(int dy) {
        measureHeaderView();
        int ratioDy = (int) (dy * 0.5f + 0.5);
        int offset = mRefreshHeaderView.getMeasuredHeight();
        int finalDragOffset = mRefreshHeaderViewOriginHeight + 20;

        int nextOffset = offset + ratioDy;
        if (finalDragOffset > 0) {
            if (nextOffset > finalDragOffset) {
                ratioDy = finalDragOffset - offset;
            }
        }

        if (nextOffset < 0) {
            ratioDy = -offset;
        }
        if (ratioDy != 0) {
            int height = mRefreshHeaderViewHeight + ratioDy;
            if (height < 0) {
                height = 0;
            }
            setRefreshHeaderViewHeight(height);
            mRefreshHeaderView.onMove(false, height);
            if (height == 0) {
                return false;
            }
        }
        return true;
    }

    private void setRefreshHeaderViewHeight(int height) {
        mRefreshHeaderView.getLayoutParams().height = height;
        mRefreshHeaderViewHeight = height;
        mRefreshHeaderView.requestLayout();
    }

    private void measureHeaderView() {
        if (mRefreshHeaderView != null) {
            mRefreshHeaderView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        }
    }
    // ====================  Refresh End ===================

    // ====================  Load More ===================

    private int LOAD_MORE_STATE_IDLE = 100;
    private int LOAD_MORE_STATE_LOADING = 200;
    private int LOAD_MORE_STATE_DISABLE = 300;
    private int mLoadMoreState = LOAD_MORE_STATE_IDLE;

    public void resetLoadMore() {
        mLoadMoreState = LOAD_MORE_STATE_IDLE;
        if (mLoadMoreFooterView != null) {
            mLoadMoreFooterView.onReset();
        }
    }

    public void setLoadMoreFailed() {
        mLoadMoreState = LOAD_MORE_STATE_IDLE;
        if (mLoadMoreFooterView != null) {
            mLoadMoreFooterView.onFail();
        }
    }

    public void setLoadMoreEnd() {
        mLoadMoreState = LOAD_MORE_STATE_DISABLE;
        if (mLoadMoreFooterView != null) {
            mLoadMoreFooterView.onEnd();
        }
    }

    public void hideLoadMore() {
        mLoadMoreState = LOAD_MORE_STATE_DISABLE;
        if (mLoadMoreFooterView != null) {
            mLoadMoreFooterView.onHide();
        }
    }

    private boolean isLoadMoreStateIdle() {
        return mLoadMoreState == LOAD_MORE_STATE_IDLE && mLoadMoreFooterView != null;
    }

    private void setLoadMoreStateLoading() {
        mLoadMoreState = LOAD_MORE_STATE_LOADING;
        if (mLoadMoreFooterView != null) {
            mLoadMoreFooterView.onLoading();
        }
    }

    public interface OnLoadMoreListener {
        /**
         * 触发 load more
         *
         * @param recyclerView recyclerView
         */
        void onLoadMore(RecyclerView recyclerView);
    }

    private OnLoadMoreListener mOnLoadMoreListener;

    public void setOnLoadMoreListener(OnLoadMoreListener listener) {
        mOnLoadMoreListener = listener;
    }

    private abstract class OnScrollLoadMoreListener extends RecyclerView.OnScrollListener {

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        }

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            int visibleItemCount = layoutManager.getChildCount();
            boolean triggerCondition = visibleItemCount > 0
                    && newState == RecyclerView.SCROLL_STATE_IDLE
                    && canTriggerLoadMore(recyclerView);
            if (triggerCondition) {
                if (isLoadMoreStateIdle()) {
                    setLoadMoreStateLoading();
                    onLoadMore(recyclerView);
                }
            }
        }

        private boolean canTriggerLoadMore(RecyclerView recyclerView) {
            View lastChild = recyclerView.getChildAt(recyclerView.getChildCount() - 1);
            int lastChildPosition = recyclerView.getChildLayoutPosition(lastChild);
            RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
            int totalItemCount = layoutManager.getItemCount();
            return totalItemCount - 1 == lastChildPosition;
        }

        /**
         * 触发 load more
         *
         * @param recyclerView recyclerView
         */
        public abstract void onLoadMore(RecyclerView recyclerView);
    }
    // ====================  Load More End ===================


    @Override
    protected void onMeasure(int widthSpec, int heightSpec) {
        super.onMeasure(widthSpec, heightSpec);
        if (getChildCount() > 0 && mRefreshHeaderView != null) {
            measureChild(mRefreshHeaderView, widthSpec, heightSpec);
        }
    }
}
