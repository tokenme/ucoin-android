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
import com.ucoin.ucoinnew.R;
import com.ucoin.ucoinnew.activity.CoinManageActivity;
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

public class CoinTaskFragment extends Fragment {
    private View mView;
    private CoinManageActivity mCoinManageActivity;
    private ArrayList<FindEntity> mDataList;
    private BaseQuickAdapter mFindAdapter;
    private RecyclerView mRecyclerView;
    private int mCurrentPage = 1;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCoinManageActivity = (CoinManageActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.tab_coin_task, container, false);

        mRecyclerView = mView.findViewById(R.id.rv_coin_task_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mCoinManageActivity));

        mFindAdapter = new FindAdapter(R.layout.entity_find, mDataList);
        View loadingView = inflater.inflate(R.layout.view_loading, (ViewGroup) mRecyclerView.getParent(), false);
        mFindAdapter.setEmptyView(loadingView);
        mRecyclerView.setAdapter(mFindAdapter);

        try {
            getFindEntity(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
                        Intent intent = new Intent(mCoinManageActivity, RightActivity.class);
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
        try {
            getFindEntity(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
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
            Api.request("getItemList", "GET", params, false, mCoinManageActivity, new Callback() {
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
                                String title = e.optString("title");
                                String desc = e.optString("desc");
                                String pic = e.optString("pic");
                                String coinName = e.optString("coin_name");
                                String coinPic = e.optString("coin_pic");
                                Double coinNum = e.optDouble("coin_num");
                                String startDate = e.optString("start_date");
                                String endDate = e.optString("end_date");
                                String userName = e.optString("user_name");
                                String userAvatar = e.optString("user_avatar");
                                int exchangeNum = e.optInt("exchange_num");
                                int likeNum = e.optInt("like_num");
                                JSONArray tags = e.optJSONArray("tags");
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
                            mCoinManageActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (isRefresh) {
                                        mFindAdapter.setNewData(mDataList);
                                    } else {
                                        mFindAdapter.addData(mDataList);
                                        mFindAdapter.loadMoreComplete();
                                    }
                                    mCurrentPage += 1;
                                }
                            });
                        } else {
                            mCoinManageActivity.runOnUiThread(new Runnable() {
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