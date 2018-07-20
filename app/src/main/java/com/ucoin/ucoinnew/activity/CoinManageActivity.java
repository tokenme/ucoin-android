package com.ucoin.ucoinnew.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.ucoin.ucoinnew.R;
import com.ucoin.ucoinnew.fragment.CoinManageProductFragment;
import com.ucoin.ucoinnew.fragment.CoinManageTaskFragment;
import com.ucoin.ucoinnew.fragment.CoinManageIntroFragment;
import com.ucoin.ucoinnew.util.UiUtil;

@RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
public class CoinManageActivity extends BaseActivity implements View.OnClickListener {

    private String mCoinAddress = "";
    private String mCoinSymbol = "";
    private String mCoinName = "";
    private String mCoinLogo = "";
    private String mCoinDesc = "";
    private int mCoinTotalSupply = 0;
    private int mCoinTotalHolders = 0;
    private int mCoinDecimals = 0;
    private int mCoinCirculatingSupply = 0;
    private Double mCoinTotalTransfers = 0.0;

    private LinearLayout mTabCoinIntro;
    private LinearLayout mTabCoinProduct;
    private LinearLayout mTabCoinTask;

    private TextView mTabCoinIntroTitle;
    private TextView mTabCoinProductTitle;
    private TextView mTabCoinTaskTitle;

    private Fragment mFragCoinIntro;
    private Fragment mFragCoinTask;
    private Fragment mFragCoinProduct;

    private Toolbar mToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_manage);
        init();
    }

    @Override
    public void onClick(View v) {
        supportInvalidateOptionsMenu();
        switch (v.getId()) {
            case R.id.activity_coin_manage_tab_intro:
                selectTab(0);
                break;
            case R.id.activity_coin_manage_tab_product:
                selectTab(1);
                break;
            case R.id.activity_coin_manage_tab_task:
                selectTab(2);
                break;
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (mFragCoinIntro == null && fragment instanceof CoinManageIntroFragment) {
            mFragCoinIntro = fragment;
        } else if (mFragCoinTask == null && fragment instanceof CoinManageTaskFragment) {
            mFragCoinTask = fragment;
        } else if (mFragCoinProduct == null && fragment instanceof CoinManageProductFragment) {
            mFragCoinProduct = fragment;
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void init() {
        initTitleBar();
        initView();
        initTabViews();
        initTabEvents();
    }

    private void initTitleBar() {
        mToolbar = findViewById(R.id.view_toolbar);
        TextView textView = mToolbar.findViewById(R.id.view_toolbar_title);
        textView.setText(mCoinName);
        setSupportActionBar(mToolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
        }
        findViewById(R.id.view_toolbar_bottom_line).setVisibility(View.GONE);
    }

    private void initTabEvents() {
        mTabCoinIntro.setOnClickListener(this);
        mTabCoinTask.setOnClickListener(this);
        mTabCoinProduct.setOnClickListener(this);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initTabViews() {
        mTabCoinIntro = findViewById(R.id.activity_coin_manage_tab_intro);
        mTabCoinTask = findViewById(R.id.activity_coin_manage_tab_task);
        mTabCoinProduct = findViewById(R.id.activity_coin_manage_tab_product);

        mTabCoinIntroTitle = findViewById(R.id.activity_coin_manage_tab_intro_title);
        mTabCoinProductTitle = findViewById(R.id.activity_coin_manage_tab_product_title);
        mTabCoinTaskTitle = findViewById(R.id.activity_coin_manage_tab_task_title);

        selectTab(0);
    }

    private void initView() {
        Intent intent = getIntent();
        mCoinAddress = intent.getStringExtra("coin_address");
        mCoinSymbol = intent.getStringExtra("coin_symbol");
        mCoinName = intent.getStringExtra("coin_name");
        mCoinDesc = intent.getStringExtra("coin_desc");
        mCoinLogo = intent.getStringExtra("coin_logo");
        mCoinTotalTransfers = intent.getDoubleExtra("coin_total_transfers", 0.0);
        mCoinTotalSupply = intent.getIntExtra("coin_total_supply", 0);
        mCoinTotalHolders = intent.getIntExtra("coin_total_holders", 1);
        mCoinDecimals = intent.getIntExtra("coin_decimals", 0);
        mCoinCirculatingSupply = intent.getIntExtra("coin_circulating_supply", 0);
        if (!TextUtils.isEmpty(mCoinLogo)) {
            Uri logoUri = Uri.parse(mCoinLogo);
            SimpleDraweeView logoDraweeView = findViewById(R.id.activity_coin_manage_logo);
            RoundingParams roundingParams = RoundingParams.fromCornersRadius(5f);
            roundingParams.setRoundAsCircle(true);
            logoDraweeView.getHierarchy().setRoundingParams(roundingParams);
            logoDraweeView.setImageURI(logoUri);
        }

        TextView coinSymbolView = findViewById(R.id.activity_coin_manage_coin_symbol);
        coinSymbolView.setText(mCoinSymbol);
        TextView coinNameView = findViewById(R.id.activity_coin_manage_coin_name);
        coinNameView.setText(mCoinName);
        TextView coinTotalSupplyView = findViewById(R.id.activity_coin_manage_coin_total_supply);
        if (mCoinDecimals > 0) {
            coinTotalSupplyView.setText(String.valueOf(mCoinTotalSupply / Math.pow(10, mCoinDecimals)));
        } else {
            coinTotalSupplyView.setText(String.valueOf(mCoinTotalSupply));
        }
        TextView coinTotalHoldersView = findViewById(R.id.activity_coin_manage_coin_total_holders);
        coinTotalHoldersView.setText(String.valueOf(mCoinTotalHolders + 1));
        TextView coinCirculatingSupplyView = findViewById(R.id.activity_coin_manage_coin_circulating_supply);
        coinCirculatingSupplyView.setText(String.valueOf(mCoinCirculatingSupply));
        TextView coinTotalTransfersView = findViewById(R.id.activity_coin_manage_coin_total_transfers);
        coinTotalTransfersView.setText(String.valueOf(mCoinTotalTransfers));
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void selectTab(int i) {
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        hideFragments(transaction);
        Bundle bundle = new Bundle();
        bundle.putString("coin_address", mCoinAddress);
        bundle.putString("coin_desc", mCoinDesc);
        tabColor(i);
        switch (i) {
            case 0:
                if (mFragCoinIntro == null) {
                    mFragCoinIntro = new CoinManageIntroFragment();
                    transaction.add(R.id.id_content, mFragCoinIntro);
                } else {
                    transaction.show(mFragCoinIntro);
                }
                mFragCoinIntro.setArguments(bundle);
                break;
            case 1:
                if (mFragCoinProduct == null) {
                    mFragCoinProduct = new CoinManageProductFragment();
                    transaction.add(R.id.id_content, mFragCoinProduct);
                } else {
                    transaction.show(mFragCoinProduct);
                }
                mFragCoinProduct.setArguments(bundle);
                break;
            case 2:
                if (mFragCoinTask == null) {
                    mFragCoinTask = new CoinManageTaskFragment();
                    transaction.add(R.id.id_content, mFragCoinTask);
                } else {
                    transaction.show(mFragCoinTask);
                }
                mFragCoinTask.setArguments(bundle);
                break;
        }
        transaction.commitAllowingStateLoss();
    }

    @SuppressLint("ResourceType")
    private void tabColor (int i ) {
        int defaultC = Color.parseColor(getString(R.color.colorGeneralText));
        mTabCoinIntroTitle.setTextColor(defaultC);
        mTabCoinTaskTitle.setTextColor(defaultC);
        mTabCoinProductTitle.setTextColor(defaultC);
        mTabCoinIntroTitle.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        mTabCoinTaskTitle.setBackgroundColor(getResources().getColor(R.color.colorWhite));
        mTabCoinProductTitle.setBackgroundColor(getResources().getColor(R.color.colorWhite));

        int c = Color.parseColor(getString(R.color.colorWhite));

        switch (i) {
            case 0:
                UiUtil.setBackgroundDrawable(mTabCoinIntroTitle,CoinManageActivity.this, R.drawable.activity_coin_manage_tab_border_radius);
                mTabCoinIntroTitle.setTextColor(c);
                break;
            case 1:
                UiUtil.setBackgroundDrawable(mTabCoinProductTitle,CoinManageActivity.this, R.drawable.activity_coin_manage_tab_border_radius);
                mTabCoinProductTitle.setTextColor(c);
                break;
            case 2:
                UiUtil.setBackgroundDrawable(mTabCoinTaskTitle,CoinManageActivity.this, R.drawable.activity_coin_manage_tab_border_radius);
                mTabCoinTaskTitle.setTextColor(c);
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
        if (mFragCoinIntro != null) {
            transaction.hide(mFragCoinIntro);
        }
    }
}