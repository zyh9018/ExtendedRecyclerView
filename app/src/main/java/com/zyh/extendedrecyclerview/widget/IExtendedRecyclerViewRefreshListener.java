package com.zyh.extendedrecyclerview.widget;


/**
 * RecyclerView下拉刷新的接口抽象
 * <p>
 * Created by Oscar-Zhang on 2018-2-12.
 */
public interface IExtendedRecyclerViewRefreshListener {
    void onStart(int headerHeight, int finalHeight);

    void onMove(boolean isComplete, int moved);

    void onRefresh();

    void onReset();

}
