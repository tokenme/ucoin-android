package com.ucoin.ucoinnew.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
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
import com.facebook.drawee.view.SimpleDraweeView;
import com.orhanobut.logger.Logger;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.ucoin.ucoinnew.R;
import com.ucoin.ucoinnew.activity.MainActivity;
import com.ucoin.ucoinnew.adapter.CoinAdapter;
import com.ucoin.ucoinnew.api.Api;
import com.ucoin.ucoinnew.entity.CoinEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class ChangeCoinFragment extends Fragment {
    private View mView;
    private MainActivity mMainActivity;
    private ArrayList<CoinEntity> mDataList;
    private BaseQuickAdapter mCoinAdapter;
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
        mView = inflater.inflate(R.layout.tab_change_coin, container, false);

        mRecyclerView = mView.findViewById(R.id.rv_coin_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mMainActivity));

        mCoinAdapter = new CoinAdapter(R.layout.entity_coin, mDataList);
        View loadingView = inflater.inflate(R.layout.view_loading, (ViewGroup) mRecyclerView.getParent(), false);
        mCoinAdapter.setEmptyView(loadingView);
        mRecyclerView.setAdapter(mCoinAdapter);

        mRefreshLayout = mView.findViewById(R.id.refreshLayout);

        initHeaderView();
        try {
            getCoinEntity(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        refresh();
        loadMore();
        return mView;
    }

    private void initHeaderView() {
        View view = getLayoutInflater().inflate(R.layout.tab_change_coin_header, (ViewGroup) mRecyclerView.getParent(), false);
        String pic = "https://xbcdn-ssl.xibao100.com/ucoin-logo.png";
        Uri picUri = Uri.parse(pic);
        SimpleDraweeView picDraweeView = view.findViewById(R.id.tab_change_coin_ucoin_logo);
        picDraweeView.setImageURI(picUri);
        view.findViewById(R.id.tab_change_coin_header_asset).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMainActivity.selectTab(3);
            }
        });
        mCoinAdapter.addHeaderView(view);
    }

    private void refresh() {
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                try {
                    getCoinEntity(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadMore() {
        mCoinAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                try {
                    getCoinEntity(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, mRecyclerView);
    }

    private void getCoinEntity(final boolean isRefresh) throws JSONException {
        try {
            JSONObject params = new JSONObject();
            if (isRefresh) {
                mCurrentPage = 1;
            }
            params.put("page", mCurrentPage);
            Api.request("getCoinList", "GET", params, mMainActivity, new Callback() {
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
                                CoinEntity entity = new CoinEntity();
                                String name = e.getString("name");
                                String pic = e.getString("pic");
                                String price = e.getString("price");
                                String marcketValue = e.getString("market_value");
                                Double increase = e.getDouble("increase");
                                entity.setName(name);
                                entity.setPic(pic);
                                entity.setPrice(price);
                                entity.setMarcketValue(marcketValue);
                                entity.setIncrease(increase);
                                mDataList.add(entity);
                            }
                            mMainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (isRefresh) {
                                        mCoinAdapter.setNewData(mDataList);
                                        mRefreshLayout.finishRefresh();
                                    } else {
                                        mCoinAdapter.addData(mDataList);
                                        mCoinAdapter.loadMoreComplete();
                                    }
                                    mCurrentPage += 1;
                                }
                            });
                        } else {
                            mMainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mCoinAdapter.loadMoreComplete();
                                    mCoinAdapter.setEnableLoadMore(false);
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