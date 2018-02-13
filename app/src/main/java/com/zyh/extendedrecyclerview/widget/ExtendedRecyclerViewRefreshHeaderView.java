package com.zyh.extendedrecyclerview.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * ExtendedRecyclerView使用的下拉刷新用抽象HeaderView
 * <p>
 * Created by Oscar-Zhang on 2018-2-12.
 */

public abstract class ExtendedRecyclerViewRefreshHeaderView extends FrameLayout implements IExtendedRecyclerViewRefreshListener {
    public ExtendedRecyclerViewRefreshHeaderView(Context context) {
        super(context);
    }

    public ExtendedRecyclerViewRefreshHeaderView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExtendedRecyclerViewRefreshHeaderView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
