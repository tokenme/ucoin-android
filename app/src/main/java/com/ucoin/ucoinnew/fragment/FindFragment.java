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
import com.ucoin.ucoinnew.activity.RightActivity;
import com.ucoin.ucoinnew.adapter.FindAdapter;
import com.ucoin.ucoinnew.api.Api;
import com.ucoin.ucoinnew.entity.FindEntity;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class FindFragment extends Fragment {
    private View mView;
    private MainActivity mMainActivity;
    private ArrayList<FindEntity> mDataList;
    private BaseQuickAdapter mFindAdapter;
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
        mView = inflater.inflate(R.layout.tab_find, container, false);

        mRecyclerView = mView.findViewById(R.id.rv_find_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mMainActivity));

        mFindAdapter = new FindAdapter(R.layout.entity_find, mDataList);
        View loadingView = inflater.inflate(R.layout.view_loading, (ViewGroup) mRecyclerView.getParent(), false);
        mFindAdapter.setEmptyView(loadingView);
        mRecyclerView.setAdapter(mFindAdapter);

        mRefreshLayout = mView.findViewById(R.id.refreshLayout);

        try {
            getFindEntity(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        refresh();
        loadMore();
        click();

        return mView;
    }

    private void click() {
        mFindAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                final FindEntity fe = (FindEntity) adapter.getItem(position);
                switch (view.getId()) {
                    case R.id.entity_find_title:
                    case R.id.entity_find_pic:
                        Intent intent = new Intent(mMainActivity, RightActivity.class);
                        String title = fe.getTitle();
                        String pic = fe.getPic();
                        String desc = fe.getDesc();
                        String coinName = fe.getCoinName();
                        String coinPic = fe.getCoinPic();
                        Double coinNum = fe.getCoinNum();
                        String startDate = fe.getStartDate();
                        String endDate = fe.getEndDate();
                        String userAvatar = fe.getUserAvatar();
                        String userName = fe.getUserName();
                        int exchangeNum = fe.getExchangeNum();
                        int likeNum = fe.getLikeNum();
                        Logger.i(startDate);

                        intent.putExtra("title", title);
                        intent.putExtra("desc", desc);
                        intent.putExtra("pic", pic);
                        intent.putExtra("coin_name", coinName);
                        intent.putExtra("coin_pic", coinPic);
                        intent.putExtra("coin_num", coinNum);
                        intent.putExtra("start_date", startDate);
                        intent.putExtra("end_date", endDate);
                        intent.putExtra("user_avatar", userAvatar);
                        intent.putExtra("user_name", userName);
                        intent.putExtra("exchange_num", exchangeNum);
                        intent.putExtra("like_num", likeNum);
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
                    getFindEntity(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadMore() {
        mFindAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                try {
                    getFindEntity(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, mRecyclerView);
    }

    private void getFindEntity(final boolean isRefresh) throws JSONException {
        try {
            JSONObject params = new JSONObject();
            if (isRefresh) {
                mCurrentPage = 1;
            }
            params.put("page", mCurrentPage);
            Api.request("getItemList", "GET", params, mMainActivity, new Callback() {
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
                                FindEntity entity = new FindEntity();
                                String title = e.getString("title");
                                String desc = e.getString("desc");
                                String pic = e.getString("pic");
                                String coinName = e.getString("coin_name");
                                String coinPic = e.getString("coin_pic");
                                Double coinNum = e.getDouble("coin_num");
                                String startDate = e.getString("start_date");
                                String endDate = e.getString("end_date");
                                String userName = e.getString("user_name");
                                String userAvatar = e.getString("user_avatar");
                                int exchangeNum = e.getInt("exchange_num");
                                int likeNum = e.getInt("like_num");
                                JSONArray tags = e.getJSONArray("tags");
                                entity.setTitle(title);
                                entity.setDesc(desc);
                                entity.setPic(pic);
                                entity.setCoinName(coinName);
                                entity.setCoinPic(coinPic);
                                entity.setCoinNum(coinNum);
                                entity.setStartDate(startDate);
                                entity.setEndDate(endDate);
                                entity.setUserName(userName);
                                entity.setUserAvatar(userAvatar);
                                entity.setExchangeNum(exchangeNum);
                                entity.setLikeNum(likeNum);
                                entity.setTags(tags);
                                mDataList.add(entity);
                            }
                            mMainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (isRefresh) {
                                        mFindAdapter.setNewData(mDataList);
                                        mRefreshLayout.finishRefresh();
                                    } else {
                                        mFindAdapter.addData(mDataList);
                                        mFindAdapter.loadMoreComplete();
                                    }
                                    mCurrentPage += 1;
                                }
                            });
                        } else {
                            mMainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mFindAdapter.loadMoreComplete();
                                    mFindAdapter.setEnableLoadMore(false);
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