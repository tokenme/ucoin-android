package com.ucoin.ucoinnew.fragment;

import android.content.Context;
import android.content.Intent;
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
import com.ucoin.ucoinnew.activity.CreateCoinTaskActivity;
import com.ucoin.ucoinnew.adapter.CoinTaskAdapter;
import com.ucoin.ucoinnew.api.Api;
import com.ucoin.ucoinnew.entity.CoinTaskEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CoinManageTaskFragment extends Fragment {
    private View mView;
    private View mNoDataView;
    private CoinManageActivity mCoinManageActivity;
    private ArrayList<CoinTaskEntity> mDataList;
    private BaseQuickAdapter mCoinTaskAdapter;
    private RecyclerView mRecyclerView;
    private int mCurrentPage = 0;
    private String mCoinAddress;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCoinManageActivity = (CoinManageActivity) context;
        Bundle bundle = getArguments();
        mCoinAddress = bundle.getString("coin_address");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.tab_coin_manage_task, container, false);
        mNoDataView = inflater.inflate(R.layout.view_rv_list_no_data, container, false);

        mRecyclerView = mView.findViewById(R.id.rv_coin_task_list);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.setFocusableInTouchMode(false);
        mRecyclerView.requestFocus();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mCoinManageActivity));

        mCoinTaskAdapter = new CoinTaskAdapter(R.layout.entity_coin_task, mDataList);
        View loadingView = inflater.inflate(R.layout.view_loading, (ViewGroup) mRecyclerView.getParent(), false);
        mCoinTaskAdapter.setEmptyView(loadingView);

        mRecyclerView.setAdapter(mCoinTaskAdapter);

        try {
            getCoinTaskEntity(false);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        loadMore();
        click();

        return mView;
    }

    private void click() {
        mCoinTaskAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                final CoinTaskEntity fe = (CoinTaskEntity) adapter.getItem(position);
                switch (view.getId()) {
                    case R.id.entity_coin_task_title:
                        break;
                }
            }
        });

        mView.findViewById(R.id.tab_coin_manage_task_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mCoinManageActivity, CreateCoinTaskActivity.class);
                intent.putExtra("coin_address", mCoinAddress);
                startActivityForResult(intent, 601);
            }
        });
    }

    private void refresh() {
        try {
            getCoinTaskEntity(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void loadMore() {
        mCoinTaskAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                try {
                    getCoinTaskEntity(false);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, mRecyclerView);
    }

    private void getCoinTaskEntity(final boolean isRefresh) throws JSONException {
        try {
            JSONObject params = new JSONObject();
            if (isRefresh) {
                mCurrentPage = 0;
            }
            params.put("page", mCurrentPage);
            params.put("token", mCoinAddress);
            Api.request("getCoinTaskList", "GET", params, false, mCoinManageActivity, new Callback() {
                @Override
                public void onFailure(@NonNull Call call, IOException e) {
                    Logger.e("onFailure: " + e.getMessage());
                }

                @Override
                public void onResponse(@NonNull Call call, Response response) throws IOException {
                    String jsonStr = response.body().string();
                    Logger.i(jsonStr);
                    if (!jsonStr.equals("null") && !TextUtils.isEmpty(jsonStr)) {
                        try {
                            JSONArray data = new JSONArray(jsonStr);
                            if (data.length() > 0) {
                                mDataList = new ArrayList<>();
                                for (int i = 0; i < data.length(); i++) {
                                    JSONObject e = data.getJSONObject(i);
                                    CoinTaskEntity entity = new CoinTaskEntity();
                                    String title = e.optString("title");
                                    String desc = e.optString("desc");
                                    String startDate = e.optString("start_date");
                                    String endDate = e.optString("end_date");
                                    int bonus = e.optInt("bonus");
                                    Double amount = e.optDouble("amount");
                                    int needEvidence = e.optInt("need_evidence");
                                    JSONArray images = e.optJSONArray("images");
                                    entity.setTitle(title);
                                    entity.setDesc(desc);
                                    entity.setStartDate(startDate);
                                    entity.setEndDate(endDate);
                                    entity.setBonus(bonus);
                                    entity.setAmount(amount);
                                    entity.setNeedEvidence(needEvidence);
                                    entity.setImages(images);
                                    mDataList.add(entity);
                                }
                                mCoinManageActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (isRefresh) {
                                            mCoinTaskAdapter.setNewData(mDataList);
                                        } else {
                                            mCoinTaskAdapter.addData(mDataList);
                                            mCoinTaskAdapter.loadMoreComplete();
                                        }
                                        mCurrentPage += 1;
                                    }
                                });
                            } else {
                                mCoinManageActivity.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mCoinTaskAdapter.setEmptyView(mNoDataView);
                                        mCoinTaskAdapter.loadMoreEnd();
                                        mCoinTaskAdapter.setEnableLoadMore(false);
                                    }
                                });
                            }
                        } catch (JSONException e) {
                            mCoinManageActivity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mCoinTaskAdapter.setEmptyView(mNoDataView);
                                    mCoinTaskAdapter.loadMoreEnd();
                                }
                            });
                            e.printStackTrace();
                        }
                    } else {
                        mCoinManageActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mCoinTaskAdapter.setEmptyView(mNoDataView);
                                mCoinTaskAdapter.loadMoreEnd();
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