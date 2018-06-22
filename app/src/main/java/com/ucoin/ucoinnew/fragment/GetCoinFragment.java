package com.ucoin.ucoinnew.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.orhanobut.logger.Logger;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.ucoin.ucoinnew.R;
import com.ucoin.ucoinnew.activity.MainActivity;
import com.ucoin.ucoinnew.activity.MakeTaskActivity;
import com.ucoin.ucoinnew.adapter.TaskAdapter;
import com.ucoin.ucoinnew.api.Api;
import com.ucoin.ucoinnew.entity.TaskEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class GetCoinFragment extends Fragment {
    private View mView;
    private MainActivity mMainActivity;
    private ArrayList<TaskEntity> mDataList;
    private BaseQuickAdapter mTaskAdapter;
    private RecyclerView mRecyclerView;
    private RefreshLayout mRefreshLayout;
    private int mCurrentPage = 1;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.mMainActivity = (MainActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.tab_get_coin, container, false);

        mRecyclerView = mView.findViewById(R.id.rv_task_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mMainActivity));

        mTaskAdapter = new TaskAdapter(R.layout.entity_task, mDataList);
        View loadingView = inflater.inflate(R.layout.view_loading, (ViewGroup) mRecyclerView.getParent(), false);
        mTaskAdapter.setEmptyView(loadingView);
        mRecyclerView.setAdapter(mTaskAdapter);

        mRefreshLayout = mView.findViewById(R.id.refreshLayout);

        try {
            getTaskEntity(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        refresh();
        loadMore();
        click();

        return mView;
    }

    private void click() {
        mTaskAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                final TaskEntity te = (TaskEntity) adapter.getItem(position);
                switch (view.getId()) {
                    case R.id.entity_task_go_to_make:
                        Intent intent = new Intent(mMainActivity, MakeTaskActivity.class);
                        intent.putExtra("title", te.getTitle());
                        intent.putExtra("desc", te.getDesc());
                        try {
                            JSONArray pics = te.getPics();
                            String pic = "";
                            if (pics.length() > 0) {
                                pic = pics.getString(0);
                            }
                            intent.putExtra("pic", pic);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        startActivity(intent);
                        break;
                }
            }
        });
    }

    private void refresh() {
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                try {
                    getTaskEntity(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadMore() {
        mTaskAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                try {
                    getTaskEntity(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, mRecyclerView);
    }

    private void getTaskEntity(final boolean isRefresh) throws JSONException {
        try {
            JSONObject params = new JSONObject();
            if (isRefresh) {
                mCurrentPage = 1;
            }
            params.put("page", mCurrentPage);
            Api.request("getTaskList", "GET", params, mMainActivity, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, IOException e) {
                    Logger.e("onFailure: " + e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, Response response) throws IOException {
                    String jsonStr = response.body().string();
                    Logger.d(jsonStr);
                    try {
                        JSONArray data = new JSONArray(jsonStr);
                        if (data.length() > 0) {
                            mDataList = new ArrayList<>();
                            for (int i = 0; i < data.length(); i++) {
                                JSONObject e = data.getJSONObject(i);
                                TaskEntity entity = new TaskEntity();
                                String title = e.getString("title");
                                String desc = e.getString("desc");
                                String coinName = e.getString("coin_name");
                                String coinPic = e.getString("coin_pic");
                                Double coinNum = e.getDouble("coin_num");
                                String startDate = e.getString("start_date");
                                String endDate = e.getString("end_date");
                                JSONArray pics = e.getJSONArray("pics");
                                entity.setTitle(title);
                                entity.setDesc(desc);
                                entity.setPics(pics);
                                entity.setCoinName(coinName);
                                entity.setCoinPic(coinPic);
                                entity.setCoinNum(coinNum);
                                entity.setStartDate(startDate);
                                entity.setEndDate(endDate);
                                mDataList.add(entity);
                            }
                            mMainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (isRefresh) {
                                        mTaskAdapter.setNewData(mDataList);
                                        mRefreshLayout.finishRefresh();
                                    } else {
                                        mTaskAdapter.addData(mDataList);
                                        mTaskAdapter.loadMoreComplete();
                                    }
                                    mCurrentPage += 1;
                                }
                            });
                        } else {
                            mMainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mTaskAdapter.loadMoreComplete();
                                    mTaskAdapter.setEnableLoadMore(false);
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
