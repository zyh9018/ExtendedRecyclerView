package com.zyh.extendedrecyclerview.widget;

import android.support.v7.widget.RecyclerView;

/**
 * RecyclerView adapter that wraps another RecyclerView adapter. The wrapped adapter can be retrieved
 * by calling {@link #getWrappedAdapter()}.
 *
 * @see RecyclerView
 * <p>
 * Created by Oscar-Zhang on 2018-2-12.
 */
public interface WrapperRecyclerAdapter {
    /**
     * Returns the adapter wrapped by this list adapter.
     *
     * @return The {@link RecyclerView.Adapter} wrapped by this adapter.
     */
    RecyclerView.Adapter getWrappedAdapter();
}
