package com.zyh.extendedrecyclerview.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.FrameLayout;

/**
 * ExtendedRecyclerView使用的上拉加载更多用抽象FooterView
 * <p>
 * Created by Oscar-Zhang on 2018-2-12.
 */
public abstract class ExtendedRecyclerViewLoadMoreFooterView extends FrameLayout implements IExtendedRecyclerViewLoadMoreListener {
    public ExtendedRecyclerViewLoadMoreFooterView(Context context) {
        super(context);
    }

    public ExtendedRecyclerViewLoadMoreFooterView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExtendedRecyclerViewLoadMoreFooterView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
