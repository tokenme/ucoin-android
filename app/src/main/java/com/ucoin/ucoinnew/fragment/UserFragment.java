package com.ucoin.ucoinnew.fragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.orhanobut.logger.Logger;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.ucoin.ucoinnew.R;
import com.ucoin.ucoinnew.activity.CoinManageActivity;
import com.ucoin.ucoinnew.activity.CreateCoinActivity;
import com.ucoin.ucoinnew.activity.CreateCoinProductActivity;
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
import java.net.URL;
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
        /*
        View loadingView = inflater.inflate(R.layout.view_loading, mRecyclerView, false);
        mUserCoinAdapter.setEmptyView(loadingView);
        */
        mRecyclerView.setAdapter(mUserCoinAdapter);

        mRefreshLayout = mView.findViewById(R.id.refreshLayout);

        initHeaderView(false);
        try {
            getUserCoinEntity(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        refresh();
        // loadMore();
        initClick();

        return mView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Logger.i(String.valueOf(requestCode));
        Logger.i(String.valueOf(resultCode));
        switch (requestCode) {
            // 第4个tab刷新
            case 401:
                if (resultCode == Activity.RESULT_OK) {
                    // RefreshLayout refreshLayout = mView.findViewById(R.id.refreshLayout);
                    // refreshLayout.autoRefresh();
                    try {
                        getUserCoinEntity(true);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    private void initClick() {
        mUserCoinAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                Intent intent = new Intent(mMainActivity, CoinManageActivity.class);
                UserCoinEntity uce = (UserCoinEntity) adapter.getItem(position);
                String address = uce.getAddress();
                String symbol = uce.getSymbol();
                String name = uce.getName();
                String logo = uce.getLogo();
                String desc = uce.getDesc();
                int decimals = uce.getDecimals();
                int totalSupply = uce.getTotalSupply();
                int totalHolders = uce.getTotalHolders();
                Double totalTransfers = uce.getTotalTransfers();
                intent.putExtra("coin_name", name);
                intent.putExtra("coin_desc", desc);
                intent.putExtra("coin_symbol", symbol);
                intent.putExtra("coin_address", address);
                intent.putExtra("coin_logo", logo);
                intent.putExtra("coin_total_supply", totalSupply);
                intent.putExtra("coin_total_holders", totalHolders);
                intent.putExtra("coin_transfers", totalTransfers);
                intent.putExtra("coin_decimals", decimals);
                startActivity(intent);
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    private void initHeaderView(boolean isRefresh) {
        final View view = getLayoutInflater().inflate(R.layout.tab_user_header, (ViewGroup) mRecyclerView.getParent(), false);
        if (!isRefresh) {
            view.findViewById(R.id.tab_user_header_qrcode).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mMainActivity, GetCoinQrcodeActivity.class);
                    startActivity(intent);
                }
            });
            view.findViewById(R.id.tab_user_header_avatar).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Util.removeSP("userToken");
                    mMainActivity.selectTab(0);
                }
            });
            view.findViewById(R.id.tab_user_header_create_coin).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mMainActivity, CreateCoinActivity.class);
                    startActivityForResult(intent, 401);
                }
            });
            mUserCoinAdapter.addHeaderView(view);
        }
        try {
            JSONObject params = new JSONObject();
            Api.request("getUserInfo", "GET", params, false, mMainActivity, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, IOException e) {
                    Logger.e(String.valueOf(e));
                }

                @Override
                public void onResponse(@NonNull Call call, Response response) throws IOException {
                    final String jsonStr = response.body().string();
                    Logger.i(jsonStr);
                    if (!TextUtils.isEmpty(jsonStr)) {
                        mMainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONObject userInfo = new JSONObject(jsonStr);
                                    String avatar = userInfo.optString("avatar");
                                    String userName = userInfo.optString("showname");
                                    Uri avatarUri = Uri.parse(avatar);
                                    final SimpleDraweeView avatarDraweeView = view.findViewById(R.id.tab_user_header_avatar);
                                    avatarDraweeView.setImageURI(avatarUri);
                                    final RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f);
                                    roundingParams.setBorder(ContextCompat.getColor(mMainActivity, R.color.colorWhite), 5.0f);
                                    roundingParams.setRoundAsCircle(true);
                                    avatarDraweeView.getHierarchy().setRoundingParams(roundingParams);

                                    avatarDraweeView.getHierarchy().setPlaceholderImage(R.drawable.default_avatar);
                                    avatarDraweeView.getHierarchy().setFailureImage(R.drawable.default_avatar);

                                    TextView userNameView = view.findViewById(R.id.tab_user_header_user_name);
                                    userNameView.setText(userName);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void refresh() {
        mRefreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                try {
                    getUserCoinEntity(true);
                    // initHeaderView(true);
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
            JSONObject coinListParams = new JSONObject();
            Api.request("getUserCoinList", "GET", coinListParams, false, mMainActivity, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, IOException e) {
                    Logger.e(String.valueOf(e));
                }

                @Override
                public void onResponse(@NonNull Call call, Response response) throws IOException {
                    final String jsonStr = response.body().string();
                    Logger.i(jsonStr);
                    if (!jsonStr.equals("null") && !TextUtils.isEmpty(jsonStr)) {
                        try {
                            JSONArray data = new JSONArray(jsonStr);
                            if (data.length() > 0) {
                                mDataList = new ArrayList<>();
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject e = data.getJSONObject(i);
                                    UserCoinEntity entity = new UserCoinEntity();
                                    String address = e.optString("address");
                                    String symbol = e.optString("symbol");
                                    String name = e.optString("name");
                                    String desc = e.optString("desc");
                                    String logo = e.optString("logo");
                                    int decimals = e.optInt("decimals");
                                    int totalHolders = e.optInt("total_holders");
                                    int circulatingSupply = e.optInt("circulating_supply");
                                    Double totalTransfers = e.optDouble("total_transfers");
                                    Double balance = e.optDouble("balance");
                                    int totalSupply = e.optInt("total_supply");
                                    if (totalTransfers.isNaN()) {
                                        totalTransfers = 0.0;
                                    }

                                    entity.setAddress(address);
                                    entity.setSymbol(symbol);
                                    entity.setName(name);
                                    entity.setDesc(desc);
                                    entity.setLogo(logo);
                                    entity.setDecimals(decimals);
                                    entity.setTotalTransfers(totalTransfers);
                                    entity.setTotalHolders(totalHolders);
                                    entity.setBalance(balance);
                                    entity.setTotalSupply(totalSupply);
                                    entity.setCirculatingSupply(circulatingSupply);
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
                                        mRefreshLayout.finishRefresh();
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        mMainActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mUserCoinAdapter.loadMoreComplete();
                                mUserCoinAdapter.setEnableLoadMore(false);
                                mRefreshLayout.finishRefresh();
                            }
                        });
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}