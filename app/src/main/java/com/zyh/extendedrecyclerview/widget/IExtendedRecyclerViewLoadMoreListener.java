package com.zyh.extendedrecyclerview.widget;


/**
 * RecyclerView上拉加载更多的接口抽象
 * <p>
 * Created by Oscar-Zhang on 2018-2-12.
 */
public interface IExtendedRecyclerViewLoadMoreListener {
    void onLoading();

    void onFail();

    void onEnd();

    void onReset();

    void onHide();
}
