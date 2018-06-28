package com.ucoin.ucoinnew.fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.orhanobut.logger.Logger;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.ucoin.ucoinnew.R;
import com.ucoin.ucoinnew.activity.GetCoinQrcodeActivity;
import com.ucoin.ucoinnew.activity.LoginActivity;
import com.ucoin.ucoinnew.activity.MainActivity;
import com.ucoin.ucoinnew.adapter.UserCoinAdapter;
import com.ucoin.ucoinnew.api.Api;
import com.ucoin.ucoinnew.entity.UserCoinEntity;
import com.ucoin.ucoinnew.util.Util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class UserFragment extends Fragment {

    private View mView;
    private MainActivity mMainActivity;
    private ArrayList<UserCoinEntity> mDataList;
    private BaseQuickAdapter mUserCoinAdapter;
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
        mView = inflater.inflate(R.layout.tab_user, container, false);

        mRecyclerView = mView.findViewById(R.id.rv_user_coin_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mMainActivity));

        mUserCoinAdapter = new UserCoinAdapter(R.layout.entity_user_coin, mDataList);
        View loadingView = inflater.inflate(R.layout.view_loading, (ViewGroup) mRecyclerView.getParent(), false);
        mUserCoinAdapter.setEmptyView(loadingView);
        mRecyclerView.setAdapter(mUserCoinAdapter);

        mRefreshLayout = mView.findViewById(R.id.refreshLayout);

        initHeaderView();
        try {
            getUserCoinEntity(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        refresh();
        loadMore();

        return mView;
    }

    @SuppressLint("ResourceAsColor")
    private void initHeaderView() {
        try {
            JSONObject params = new JSONObject();
            Api.request("getUserInfo", "GET", params, mMainActivity, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, IOException e) {
                    Logger.e(String.valueOf(e));
                }

                @Override
                public void onResponse(@NonNull Call call, Response response) throws IOException {
                    String jsonStr = response.body().string();
                    Logger.i(jsonStr);
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        View view = getLayoutInflater().inflate(R.layout.tab_user_header, (ViewGroup) mRecyclerView.getParent(), false);
        String pic = "https://wx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTLeYdiaVF5aueicbwnSNic3BaajXn61rPMn0HKXdYWHOsM4z8h6vZWyxZ6QQWTkDMKvjL6RVhH7dpLaA/132";
        Uri picUri = Uri.parse(pic);
        SimpleDraweeView picDraweeView = view.findViewById(R.id.tab_user_avatar);
        picDraweeView.setImageURI(picUri);
        RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f);
        roundingParams.setBorder(ContextCompat.getColor(mMainActivity, R.color.colorWhite), 5.0f);
        roundingParams.setRoundAsCircle(true);
        picDraweeView.getHierarchy().setRoundingParams(roundingParams);
        view.findViewById(R.id.tab_user_header_qrcode).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mMainActivity, GetCoinQrcodeActivity.class);
                startActivity(intent);
            }
        });
        mUserCoinAdapter.addHeaderView(view);
    }

    private void refresh() {
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                try {
                    getUserCoinEntity(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void loadMore() {
        mUserCoinAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                try {
                    getUserCoinEntity(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, mRecyclerView);
    }

    private void getUserCoinEntity(final boolean isRefresh) throws JSONException {
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
                                UserCoinEntity entity = new UserCoinEntity();
                                String name = e.getString("name");
                                String pic = e.getString("pic");
                                String price = e.getString("price");
                                String marcketValue = e.getString("market_value");
                                entity.setName(name);
                                entity.setPic(pic);
                                entity.setPrice(price);
                                entity.setMarcketValue(marcketValue);
                                mDataList.add(entity);
                            }
                            mMainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (isRefresh) {
                                        mUserCoinAdapter.setNewData(mDataList);
                                        mRefreshLayout.finishRefresh();
                                    } else {
                                        mUserCoinAdapter.addData(mDataList);
                                        mUserCoinAdapter.loadMoreComplete();
                                    }
                                    mCurrentPage += 1;
                                }
                            });
                        } else {
                            mMainActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mUserCoinAdapter.loadMoreComplete();
                                    mUserCoinAdapter.setEnableLoadMore(false);
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