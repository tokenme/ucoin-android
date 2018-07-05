package com.ucoin.ucoinnew.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.orhanobut.logger.Logger;
import com.ucoin.ucoinnew.R;
import com.ucoin.ucoinnew.activity.CoinManageActivity;
import com.ucoin.ucoinnew.adapter.CoinProductAdapter;
import com.ucoin.ucoinnew.api.Api;
import com.ucoin.ucoinnew.entity.CoinProductEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CoinProductFragment extends Fragment {
    private View mView;
    private CoinManageActivity mCoinManageActivity;
    private ArrayList<CoinProductEntity> mDataList;
    private BaseQuickAdapter mCoinProductAdapter;
    private RecyclerView mRecyclerView;
    private int mCurrentPage = 1;
    private String mTokenAddress;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCoinManageActivity = (CoinManageActivity) context;
        Bundle bundle = getArguments();
        mTokenAddress = bundle.getString("token_address");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.tab_coin_product, container, false);

        mRecyclerView = mView.findViewById(R.id.rv_coin_product_list);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mCoinManageActivity));

        mCoinProductAdapter = new CoinProductAdapter(R.layout.entity_coin_product, mDataList);
        View loadingView = inflater.inflate(R.layout.view_loading, (ViewGroup) mRecyclerView.getParent(), false);
        mCoinProductAdapter.setEmptyView(loadingView);
        mRecyclerView.setAdapter(mCoinProductAdapter);

        try {
            getCoinProductEntity(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        loadMore();
        click();

        return mView;
    }

    private void click() {
        mCoinProductAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                final CoinProductEntity fe = (CoinProductEntity) adapter.getItem(position);
                switch (view.getId()) {
                    case R.id.entity_coin_product_title:
                        break;
                }
            }
        });
    }

    private void refresh() {
        try {
            getCoinProductEntity(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadMore() {
        mCoinProductAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                try {
                    getCoinProductEntity(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, mRecyclerView);
    }

    private void getCoinProductEntity(final boolean isRefresh) throws JSONException {
        try {
            JSONObject params = new JSONObject();
            if (isRefresh) {
                mCurrentPage = 1;
            }
            params.put("page", mCurrentPage);
            params.put("token", mTokenAddress);
            Api.request("getCoinProductList", "GET", params, false, mCoinManageActivity, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, IOException e) {
                    Logger.e("onFailure: " + e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, Response response) throws IOException {
                    String jsonStr = response.body().string();
                    Logger.i(jsonStr);
                    if (jsonStr != null && !TextUtils.isEmpty(jsonStr)) {
                        try {
                            JSONArray data = new JSONArray(jsonStr);
                            if (data.length() > 0) {
                                mDataList = new ArrayList<>();
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject e = data.getJSONObject(i);
                                    CoinProductEntity entity = new CoinProductEntity();
                                    String title = e.optString("title");
                                    entity.setTitle(title);
                                    mDataList.add(entity);
                                }
                                mCoinManageActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (isRefresh) {
                                            mCoinProductAdapter.setNewData(mDataList);
                                        } else {
                                            mCoinProductAdapter.addData(mDataList);
                                            mCoinProductAdapter.loadMoreComplete();
                                        }
                                        mCurrentPage += 1;
                                    }
                                });
                            } else {
                                mCoinManageActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mCoinProductAdapter.loadMoreComplete();
                                        mCoinProductAdapter.setEnableLoadMore(false);
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        mCoinProductAdapter.loadMoreEnd();
                    }
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}