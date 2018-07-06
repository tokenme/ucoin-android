package com.ucoin.ucoinnew.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.view.SimpleDraweeView;
import com.orhanobut.logger.Logger;
import com.ucoin.ucoinnew.R;
import com.ucoin.ucoinnew.fragment.CoinProductFragment;
import com.ucoin.ucoinnew.fragment.CoinTaskFragment;
import com.ucoin.ucoinnew.fragment.CoinTradeFragment;

public class CoinManageActivity extends AppCompatActivity implements View.OnClickListener {

    private String mTokenAddress = "";
    private String mTokenName = "";
    private String mTokenLogo = "";

    private LinearLayout mTabCoinTask;
    private LinearLayout mTabCoinProduct;
    private LinearLayout mTabCoinTrade;

    private TextView mTabTextTask;
    private TextView mTabTextProduct;
    private TextView mTabTextTrade;

    private Fragment mFragCoinTask;
    private Fragment mFragCoinProduct;
    private Fragment mFragCoinTrade;

    private int mCurrentTab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_manage);
        init();
    }

    @Override
    public void onClick(View v) {
        supportInvalidateOptionsMenu();
        mCurrentTab = v.getId();
        switch (v.getId()) {
            case R.id.activity_coin_manage_tab_task:
                selectTab(0);
                break;
            case R.id.activity_coin_manage_tab_product:
                selectTab(1);
                break;
            case R.id.activity_coin_manage_tab_trade:
                selectTab(2);
                break;
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (mFragCoinTask == null && fragment instanceof CoinTaskFragment) {
            mFragCoinTask = fragment;
        } else if (mFragCoinProduct == null && fragment instanceof CoinProductFragment) {
            mFragCoinProduct = fragment;
        } else if (mFragCoinTrade == null && fragment instanceof CoinTradeFragment) {
            mFragCoinTrade = fragment;
        }
    }

    private void init() {
        initTitleBar();
        initView();
        initTabViews();
        initTabEvents();
    }

    private void initTabEvents() {
        mTabCoinTask.setOnClickListener(this);
        mTabCoinProduct.setOnClickListener(this);
        mTabCoinTrade.setOnClickListener(this);
    }

    private void initTabViews() {
        mTabCoinTask = findViewById(R.id.activity_coin_manage_tab_task);
        mTabCoinProduct = findViewById(R.id.activity_coin_manage_tab_product);
        mTabCoinTrade = findViewById(R.id.activity_coin_manage_tab_trade);

        mTabTextTask = findViewById(R.id.activity_coin_manage_tab_task_text);
        mTabTextProduct = findViewById(R.id.activity_coin_manage_tab_product_text);
        mTabTextTrade = findViewById(R.id.activity_coin_manage_tab_trade_text);

        selectTab(0);
    }

    private void initView() {
        Intent intent = getIntent();
        mTokenAddress = intent.getStringExtra("token_address");
        mTokenName = intent.getStringExtra("token_name");
        mTokenLogo = intent.getStringExtra("token_logo");
        if (!TextUtils.isEmpty(mTokenLogo)) {
            Uri logoUri = Uri.parse(mTokenLogo);
            SimpleDraweeView logoDraweeView = findViewById(R.id.activity_coin_manage_logo);
            logoDraweeView.setImageURI(logoUri);
        }

        TextView coinNameView = findViewById(R.id.activity_coin_manage_coin_name);
        coinNameView.setText(mTokenName);
    }

    public void selectTab(int i) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        hideFragments(transaction);
        Bundle bundle = new Bundle();
        bundle.putString("token_address", mTokenAddress);
        tabColor(i);
        switch (i) {
            case 0:
                if (mFragCoinTask == null) {
                    mFragCoinTask = new CoinTaskFragment();
                    transaction.add(R.id.id_content, mFragCoinTask);
                } else {
                    transaction.show(mFragCoinTask);
                }
                mFragCoinTask.setArguments(bundle);
                break;
            case 1:
                if (mFragCoinProduct == null) {
                    mFragCoinProduct = new CoinProductFragment();
                    transaction.add(R.id.id_content, mFragCoinProduct);
                } else {
                    transaction.show(mFragCoinProduct);
                }
                mFragCoinProduct.setArguments(bundle);
                break;
            case 2:
                if (mFragCoinTrade == null) {
                    mFragCoinTrade = new CoinTradeFragment();
                    transaction.add(R.id.id_content, mFragCoinTrade);
                } else {
                    transaction.show(mFragCoinTrade);
                }
                mFragCoinTrade.setArguments(bundle);
                break;
        }
        transaction.commitAllowingStateLoss();
    }

    @SuppressLint("ResourceType")
    private void tabColor (int i ) {
        int defaultC = Color.parseColor(getString(R.color.colorWhite));
        mTabTextTask.setTextColor(defaultC);
        mTabTextProduct.setTextColor(defaultC);
        mTabTextTrade.setTextColor(defaultC);

        int c = Color.parseColor(getString(R.color.colorGeneralText));
        switch (i) {
            case 0:
                mTabTextTask.setTextColor(c);
                break;
            case 1:
                mTabTextProduct.setTextColor(c);
                break;
            case 2:
                mTabTextTrade.setTextColor(c);
                break;
        }
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (mFragCoinTask != null) {
            transaction.hide(mFragCoinTask);
        }
        if (mFragCoinProduct != null) {
            transaction.hide(mFragCoinProduct);
        }
        if (mFragCoinTrade != null) {
            transaction.hide(mFragCoinTrade);
        }
    }

    private void initTitleBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }
}