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
import android.widget.Button;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.orhanobut.logger.Logger;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.ucoin.ucoinnew.R;
import com.ucoin.ucoinnew.activity.CoinManageActivity;
import com.ucoin.ucoinnew.activity.MainActivity;
import com.ucoin.ucoinnew.activity.RightActivity;
import com.ucoin.ucoinnew.adapter.FindAdapter;
import com.ucoin.ucoinnew.api.Api;
import com.ucoin.ucoinnew.entity.FindEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class CoinManageIntroFragment extends Fragment {
    private View mView;
    private CoinManageActivity mCoinManageActivity;
    private String mCoinAddress;
    private String mCoinDesc;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCoinManageActivity = (CoinManageActivity) context;
        Bundle bundle = getArguments();
        mCoinAddress = bundle.getString("coin_address");
        mCoinDesc = bundle.getString("coin_desc");
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.tab_coin_manage_intro, container, false);

        TextView coinDescView = mView.findViewById(R.id.tab_coin_manage_intro_desc);
        Button coinDescAddButtonView = mView.findViewById(R.id.tab_coin_manage_intro_desc_add);
        if (!TextUtils.isEmpty(mCoinDesc)) {
            coinDescView.setText(mCoinDesc);
        } else {
            coinDescView.setText(R.string.coin_manage_intro_add_tip);
            coinDescAddButtonView.setVisibility(View.VISIBLE);
        }

        return mView;
    }

}