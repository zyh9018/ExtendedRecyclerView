package com.zyh.extendedrecyclerview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.zyh.extendedrecyclerview.widget.ExtendedRecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ExtendedRecyclerView mRecyclerView;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.main_recyclerview);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        prepareRefreshHeaderIndicator(mRecyclerView);
        prepareFooterIndicator(mRecyclerView);

        ArrayList<String> data = new ArrayList<>();
        mAdapter = new MyAdapter(this, data);
        mRecyclerView.setAdapter(mAdapter);

        initData();
        initListener();
    }

    /**
     * 设置下拉刷新用头部
     * init header view
     * @param recyclerView TRecyclerView
     */
    private void prepareRefreshHeaderIndicator(ExtendedRecyclerView recyclerView) {
        PullToRefreshHeaderView refreshHeaderView = new PullToRefreshHeaderView(this);
        recyclerView.setRefreshHeader(refreshHeaderView);
        recyclerView.setRefreshEnabled(true);
    }

    /**
     * 设置底部记载更多用的footerView
     * init footer view
     * @param recyclerView TRecyclerView
     */
    private void prepareFooterIndicator(ExtendedRecyclerView recyclerView) {
        LoadMoreFooterView footerViewHolder = new LoadMoreFooterView(this);
        recyclerView.setLoadMoreFooter(footerViewHolder);
    }

    private void initData() {
        // fake origin data
        List<String> arrayList = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            String title = "origin text: " + i;
            arrayList.add(title);
        }

        mAdapter.refreshData(arrayList);
    }

    private void initListener() {
        mRecyclerView.setOnRefreshListener(new ExtendedRecyclerView.OnRefreshListener() {
            @Override
            public void onRefresh(RecyclerView recyclerView) {
                asyncRefresh();
            }
        });

        mRecyclerView.setOnLoadMoreListener(new ExtendedRecyclerView.OnLoadMoreListener() {
            @Override
            public void onLoadMore(RecyclerView recyclerView) {
                asyncLoadMore();
            }
        });
    }

    private List<String> fakeRefreshData() {
        List<String> arrayList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            String title = "refresh: " + i;
            arrayList.add(title);
        }
        return arrayList;
    }

    private List<String> fakeLoadMoreData() {
        List<String> arrayList = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            String title = "load more: " + i;
            arrayList.add(title);
        }
        return arrayList;
    }

    private void asyncRefresh() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> data = fakeRefreshData();
                try {
                    Thread.sleep(1000); // loading...
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                asyncRefreshComplete(data);
            }
        }).start();
    }

    private void asyncRefreshComplete(final List<String> data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.addDataAtTop(data);
                mRecyclerView.resetRefreshing();
            }
        });
    }

    private void asyncLoadMore() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> data = fakeLoadMoreData();
                try {
                    Thread.sleep(1000); // loading...
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                asyncLoadMoreComplete(data);
            }
        }).start();
    }

    private void asyncLoadMoreComplete(final List<String> data) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //success
                mAdapter.addDataAtBottom(data);
                mRecyclerView.resetLoadMore();

                //fail
//                mRecyclerView.setLoadMoreFailed();

                //end
//                mRecyclerView.setLoadMoreEnd();

                //hide
//                mRecyclerView.hideLoadMore();
            }
        });
    }

}
