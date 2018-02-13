package com.zyh.extendedrecyclerview.widget;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * RecyclerView.Adapter used when a RecyclerView has header views. This RecyclerView.Adapter
 * wraps another one and also keeps track of the header views and their
 * associated data objects.
 * <p>
 * Created by Oscar-Zhang on 2018-2-12.
 */
public class HeaderViewRecyclerAdapter extends RecyclerView.Adapter implements WrapperRecyclerAdapter {
    public static final int ITEM_VIEW_TYPE_HEADER = -1000;
    public static final int ITEM_VIEW_TYPE_FOOTER = -2000;
    private final RecyclerView.Adapter mAdapter;
    ArrayList<View> mHeaderViews;
    ArrayList<View> mFootViews;
    static final ArrayList<View> EMPTY_INFO_LIST = new ArrayList<>();
    SparseIntArray mHeadersTypeMap = new SparseIntArray();
    SparseIntArray mFootersTypeMap = new SparseIntArray();
    /**
     * 为了确保 index 大于 0
     */
    private static final int HEADER_FOOTER_INDEX_OFFSET = 2;

    private RecyclerView.AdapterDataObserver mObserver = new RecyclerView.AdapterDataObserver() {
        @Override
        public void onChanged() {
            HeaderViewRecyclerAdapter.this.notifyDataSetChanged();
        }

        @Override
        public void onItemRangeChanged(int positionStart, int itemCount) {
            HeaderViewRecyclerAdapter.this.notifyItemRangeChanged(positionStart + getHeadersCount(), itemCount);
        }

        /*@Override
        public void onItemRangeChanged(int positionStart, int itemCount, Object payload) {
            HeaderViewRecyclerAdapter.this.notifyItemRangeChanged(positionStart + getHeadersCount(), itemCount, payload);
        }*/

        @Override
        public void onItemRangeInserted(int positionStart, int itemCount) {
            HeaderViewRecyclerAdapter.this.notifyItemRangeInserted(positionStart + getHeadersCount(), itemCount);
        }

        @Override
        public void onItemRangeRemoved(int positionStart, int itemCount) {
            HeaderViewRecyclerAdapter.this.notifyItemRangeRemoved(positionStart + getHeadersCount(), itemCount);
        }

        @Override
        public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
            HeaderViewRecyclerAdapter.this.notifyDataSetChanged();
        }
    };

    public HeaderViewRecyclerAdapter(RecyclerView.Adapter adapter, ArrayList<View> headerViews, ArrayList<View> footViews) {
        this.mAdapter = adapter;
        if (headerViews == null) {
            mHeaderViews = EMPTY_INFO_LIST;
        } else {
            mHeaderViews = headerViews;
        }
        if (footViews == null) {
            mFootViews = EMPTY_INFO_LIST;
        } else {
            mFootViews = footViews;
        }
        mAdapter.registerAdapterDataObserver(mObserver);
    }


    public int getHeadersCount() {
        return mHeaderViews.size();
    }

    public boolean isHeadersEmpty() {
        return mHeaderViews.size() == 0;
    }

    public int getFootersCount() {
        return mFootViews.size();
    }

    public boolean isFootersEmpty() {
        return mFootViews.size() == 0;
    }

    @Override
    public int getItemCount() {
        if (mAdapter != null) {
            return getHeadersCount() + getFootersCount() + mAdapter.getItemCount();
        } else {
            return getHeadersCount() + getFootersCount();
        }
    }

    @Override
    public int getItemViewType(int position) {
        int numHeaders = getHeadersCount();
        if (position < numHeaders) {
            int headerType = ITEM_VIEW_TYPE_HEADER + position;
            // viewType设置为key，用来找出views数组中的view
            mHeadersTypeMap.put(headerType, position + HEADER_FOOTER_INDEX_OFFSET);
            return headerType;
        }
        int footerIndex;
        if (mAdapter != null && position >= numHeaders) {
            int adjPosition = position - numHeaders;
            int adapterCount = mAdapter.getItemCount();
            if (adjPosition < adapterCount) {
                return mAdapter.getItemViewType(adjPosition);
            }
            footerIndex = position - numHeaders - adapterCount;
        } else {
            footerIndex = position - numHeaders;
        }
        int footerType = ITEM_VIEW_TYPE_FOOTER + footerIndex;
        // viewType设置为key，用来找出views数组中的view
        mFootersTypeMap.put(footerType, footerIndex + HEADER_FOOTER_INDEX_OFFSET);
        return footerType;
    }


    @Override
    public long getItemId(int position) {
        int numHeaders = getHeadersCount();
        if (position < numHeaders) {
            return ITEM_VIEW_TYPE_HEADER + position;
        }
        if (mAdapter != null && position >= numHeaders) {
            int adjPosition = position - numHeaders;
            int adapterCount = mAdapter.getItemCount();
            if (adjPosition < adapterCount) {
                return mAdapter.getItemId(adjPosition);
            }
        }
        return ITEM_VIEW_TYPE_FOOTER + position;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (!isHeadersEmpty()) {
            int headerViewIndex = mHeadersTypeMap.get(viewType);
            if (headerViewIndex > 0) {
                return new HeaderViewHolder(mHeaderViews.get(headerViewIndex - HEADER_FOOTER_INDEX_OFFSET));
            }
        }
        if (!isFootersEmpty()) {
            int footerViewIndex = mFootersTypeMap.get(viewType);
            if (footerViewIndex > 0) {
                return new FooterViewHolder(mFootViews.get(footerViewIndex - HEADER_FOOTER_INDEX_OFFSET));
            }
        }
        return mAdapter.onCreateViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        int numHeaders = getHeadersCount();
        if (position < numHeaders) {
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            if (layoutParams != null && layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
                StaggeredGridLayoutManager.LayoutParams layoutParamsStaggered = (StaggeredGridLayoutManager.LayoutParams) layoutParams;
                layoutParamsStaggered.setFullSpan(true);
                holder.itemView.setLayoutParams(layoutParams);
            }
            return;
        }
        if (mAdapter != null) {
            int adjPosition = position - numHeaders;
            int adapterCount = mAdapter.getItemCount();
            if (adjPosition < adapterCount) {
                mAdapter.onBindViewHolder(holder, adjPosition);
                return;
            }
        }
        ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
        if (layoutParams != null && layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams layoutParamsStaggered = (StaggeredGridLayoutManager.LayoutParams) layoutParams;
            layoutParamsStaggered.setFullSpan(true);
            holder.itemView.setLayoutParams(layoutParams);
        }
    }


    @Override
    public RecyclerView.Adapter getWrappedAdapter() {
        return mAdapter;
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder {
        public HeaderViewHolder(View itemView) {
            super(itemView);
        }
    }

    private static class FooterViewHolder extends RecyclerView.ViewHolder {
        public FooterViewHolder(View itemView) {
            super(itemView);
        }
    }
}
