package com.zyh.extendedrecyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.zyh.extendedrecyclerview.widget.ExtendedRecyclerViewLoadMoreFooterView;

/**
 * 加载更多FooterView
 * <p>
 * Created by Oscar-Zhang on 2018-2-12.
 */
public class LoadMoreFooterView extends ExtendedRecyclerViewLoadMoreFooterView {
    private View mRootView;
    private View mLoadingContainer;
    private TextView mLoadingTv;
    private ImageView mLoadingIv;
    private Animation mRotateAnim;

    public LoadMoreFooterView(Context context) {
        this(context, null);
    }

    public LoadMoreFooterView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LoadMoreFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mRootView = inflate(context, R.layout.load_more_footer, this);
        mLoadingContainer = mRootView.findViewById(R.id.common_footer_loading_container_ll);
        mLoadingTv = (TextView) mRootView.findViewById(R.id.common_footer_indicator_tv);
        mLoadingIv = (ImageView) mRootView.findViewById(R.id.common_footer_indicator_Iv);
        mRotateAnim = AnimationUtils.loadAnimation(mRootView.getContext(), R.anim.loading_anim);
        mRotateAnim.setRepeatCount(Animation.INFINITE);
    }

    @Override
    public void onLoading() {
        mRootView.setVisibility(View.VISIBLE);
        mLoadingContainer.setVisibility(View.VISIBLE);
        mLoadingIv.setVisibility(View.VISIBLE);
        mLoadingIv.startAnimation(mRotateAnim);
        mLoadingTv.setText(R.string.loadmore_footer_hint_loading);
    }

    @Override
    public void onFail() {
        mRootView.setVisibility(View.VISIBLE);
        mLoadingContainer.setVisibility(View.VISIBLE);
        mLoadingIv.clearAnimation();
        mLoadingIv.setVisibility(View.GONE);
        mLoadingTv.setText(R.string.loadmore_footer_hint_fail);
    }

    @Override
    public void onEnd() {
        mRootView.setVisibility(View.VISIBLE);
        mLoadingContainer.setVisibility(View.VISIBLE);
        mLoadingIv.clearAnimation();
        mLoadingIv.setVisibility(View.GONE);
        mLoadingTv.setText(R.string.loadmore_footer_hint_no_more);
    }

    @Override
    public void onReset() {
        mRootView.setVisibility(View.VISIBLE);
        mLoadingContainer.setVisibility(View.VISIBLE);
        mLoadingIv.setVisibility(View.VISIBLE);
        mLoadingTv.setText(R.string.loadmore_footer_hint_loading);
    }

    @Override
    public void onHide() {
        mRootView.setVisibility(View.GONE);
        mLoadingIv.clearAnimation();
        mLoadingContainer.setVisibility(View.GONE);
    }
}
