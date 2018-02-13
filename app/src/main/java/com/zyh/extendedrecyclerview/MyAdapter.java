package com.zyh.extendedrecyclerview;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Adapter
 * <p>
 * Created by Oscar-Zhang on 2018-2-12.
 */
public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private Context mContext;
    private List<String> mData;

    public MyAdapter(Context context, List<String> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public MyAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recyclerview_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final MyAdapter.MyViewHolder holder, final int position) {
        String item = getItem(position);
        holder.mTitleTv.setText(item);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    private String getItem(int position) {
        return mData.get(position);
    }

    public void refreshData(List<String> data) {
        if (data == null) {
            return;
        }
        int oldSize = mData.size();
        mData.clear();
        notifyItemRangeRemoved(0, oldSize);
        mData.addAll(data);
        notifyItemRangeInserted(0, mData.size());
    }

    public void addDataAtTop(List<String> data) {
        if (data == null) {
            return;
        }
        mData.addAll(0, data);
        notifyItemRangeChanged(0, data.size());
    }

    public void addDataAtBottom(List<String> data) {
        if (data == null) {
            return;
        }
        int oldSize = mData.size();
        mData.addAll(data);
        notifyItemRangeInserted(oldSize, data.size());
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView mTitleTv;

        MyViewHolder(View itemView) {
            super(itemView);
            prepareView(itemView);
        }

        private void prepareView(View itemView) {
            mTitleTv = (TextView) itemView.findViewById(R.id.title_tv);
        }
    }
}
