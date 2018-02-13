package com.zyh.extendedrecyclerview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.zyh.extendedrecyclerview.widget.ExtendedRecyclerViewRefreshHeaderView;


/**
 * 下拉刷新HeaderView
 * <p>
 * Created by Oscar-Zhang on 2018-2-12.
 */
public class PullToRefreshHeaderView extends ExtendedRecyclerViewRefreshHeaderView {
    private Context mContext;

    private ImageView hintIv;
    private TextView hintTv;
    private Animation mRotateAnim;

    private int mHeight;

    public PullToRefreshHeaderView(Context context) {
        this(context, null);
    }

    public PullToRefreshHeaderView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PullToRefreshHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = inflate(context, R.layout.pulltorefresh_header, this);

        mContext = context;
        hintTv = (TextView) view.findViewById(R.id.friends_pulltorefresh_header_hint_tv);
        hintIv = (ImageView) view.findViewById(R.id.friends_pulltorefresh_header_hint_iv);

        mRotateAnim = AnimationUtils.loadAnimation(mContext, R.anim.loading_anim);
        mRotateAnim.setRepeatCount(Animation.INFINITE);

    }

    @Override
    public void onStart(int headerHeight, int finalHeight) {
        this.mHeight = headerHeight;
    }

    @Override
    public void onMove(boolean isComplete, int moved) {
        if (!isComplete) {
            hintTv.setVisibility(VISIBLE);
            hintIv.setVisibility(VISIBLE);
            if (moved <= mHeight) {
                hintTv.setText(mContext.getResources().getString(R.string.pulltorefresh_header_hint_normal));
            } else {
                hintTv.setText(mContext.getResources().getString(R.string.pulltorefresh_header_hint_ready));
            }
        }
    }

    @Override
    public void onRefresh() {
        if (mRotateAnim != null) {
            hintIv.startAnimation(mRotateAnim);
        }
        hintTv.setText(mContext.getResources().getString(R.string.pulltorefresh_header_hint_loading));
    }

    @Override
    public void onReset() {
        if (mRotateAnim != null) {
            hintIv.clearAnimation();
        }
        hintTv.setText(mContext.getResources().getString(R.string.pulltorefresh_header_hint_normal));
        hintTv.setVisibility(GONE);
        hintIv.setVisibility(GONE);
    }
}
