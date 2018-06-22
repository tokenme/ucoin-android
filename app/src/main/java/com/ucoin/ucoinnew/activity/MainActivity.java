package com.ucoin.ucoinnew.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.ucoin.ucoinnew.R;
import com.ucoin.ucoinnew.fragment.ChangeCoinFragment;
import com.ucoin.ucoinnew.fragment.FindFragment;
import com.ucoin.ucoinnew.fragment.GetCoinFragment;
import com.ucoin.ucoinnew.fragment.UserFragment;

import com.mikepenz.iconics.view.IconicsTextView;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

import cn.bingoogolapple.qrcode.core.QRCodeView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout mTabFind;
    private LinearLayout mTabGetCoin;
    private LinearLayout mTabChangeCoin;
    private LinearLayout mTabUser;

    private IconicsTextView mImgFind;
    private IconicsTextView mImgGetCoin;
    private IconicsTextView mImgChangeCoin;
    private IconicsTextView mImgUser;

    private TextView mTextFind;
    private TextView mTextGetCoin;
    private TextView mTextChangeCoin;
    private TextView mTextUser;

    private Fragment mFragFind;
    private Fragment mFragGetCoin;
    private Fragment mFragChangeCoin;
    private Fragment mFragUser;

    private long mExitTime = 0;
    private int mCurrentTab;

    private CommonTitleBar mTitleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();
    }

    @Override
    public void onClick(View v) {
        supportInvalidateOptionsMenu();
        mCurrentTab = v.getId();
        switch (v.getId()) {
            case R.id.tab_find:
                mTitleBar.setVisibility(View.VISIBLE);
                selectTab(0);
                break;
            case R.id.tab_get_coin:
                mTitleBar.setVisibility(View.VISIBLE);
                selectTab(1);
                break;
            case R.id.tab_change_coin:
                mTitleBar.setVisibility(View.VISIBLE);
                selectTab(2);
                break;
            case R.id.tab_user:
                mTitleBar.setVisibility(View.GONE);
                selectTab(3);
                break;
        }
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        super.onAttachFragment(fragment);
        if (mFragFind == null && fragment instanceof FindFragment) {
            mFragFind = fragment;
        } else if (mFragGetCoin == null && fragment instanceof GetCoinFragment) {
            mFragGetCoin = fragment;
        } else if (mFragChangeCoin == null && fragment instanceof ChangeCoinFragment) {
            mFragChangeCoin = fragment;
        } else if (mFragUser == null && fragment instanceof UserFragment) {
            mFragUser = fragment;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - mExitTime) > 2000) {
                Toast.makeText(getApplicationContext(), "再按一次将退出Ucoin", Toast.LENGTH_SHORT).show();
                mExitTime = System.currentTimeMillis();
            } else {
                finish();
                System.exit(0);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void init() {
        initTitleBar();
        initTabViews();
        initTabEvents();
    }

    private void initTitleBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        mTitleBar = findViewById(R.id.title_bar);
        View leftCustomLayout = mTitleBar.getLeftCustomView();
        leftCustomLayout.findViewById(R.id.find_title_bar_scan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ScanQrcodeActivity.class));
            }
        });
    }

    private void initTabEvents() {
        mTabFind.setOnClickListener(this);
        mTabGetCoin.setOnClickListener(this);
        mTabChangeCoin.setOnClickListener(this);
        mTabUser.setOnClickListener(this);
    }

    private void initTabViews() {
        mTabFind = findViewById(R.id.tab_find);
        mTabGetCoin = findViewById(R.id.tab_get_coin);
        mTabChangeCoin = findViewById(R.id.tab_change_coin);
        mTabUser = findViewById(R.id.tab_user);

        mImgFind = findViewById(R.id.tab_find_img);
        mImgGetCoin = findViewById(R.id.tab_get_coin_img);
        mImgChangeCoin = findViewById(R.id.tab_change_coin_img);
        mImgUser = findViewById(R.id.tab_user_img);

        mTextFind = findViewById(R.id.tab_find_text);
        mTextGetCoin = findViewById(R.id.tab_get_coin_text);
        mTextChangeCoin = findViewById(R.id.tab_change_coin_text);
        mTextUser = findViewById(R.id.tab_user_text);

        selectTab(0);
    }

    public void selectTab(int i) {
        resetIcons();
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        hideFragments(transaction);
        iconActive(i);
        switch (i) {
            case 0:
                if (mFragFind == null) {
                    mFragFind = new FindFragment();
                    transaction.add(R.id.id_content, mFragFind);
                } else {
                    transaction.show(mFragFind);
                }
                break;
            case 1:
                if (mFragGetCoin == null) {
                    mFragGetCoin = new GetCoinFragment();
                    transaction.add(R.id.id_content, mFragGetCoin);
                } else {
                    transaction.show(mFragGetCoin);
                }
                break;
            case 2:
                if (mFragChangeCoin == null) {
                    mFragChangeCoin = new ChangeCoinFragment();
                    transaction.add(R.id.id_content, mFragChangeCoin);
                } else {
                    transaction.show(mFragChangeCoin);
                }
                break;
            case 3:
                if (mFragUser == null) {
                    mFragUser = new UserFragment();
                    transaction.add(R.id.id_content, mFragUser);
                } else {
                    transaction.show(mFragUser);
                }
                break;
        }
        transaction.commitAllowingStateLoss();
    }

    private void resetIcons() {
        @SuppressLint("ResourceType") int c = Color.parseColor(getString(R.color.tabDefault));
        mImgFind.setTextColor(c);
        mImgGetCoin.setTextColor(c);
        mImgChangeCoin.setTextColor(c);
        mImgUser.setTextColor(c);

        mTextFind.setTextColor(c);
        mTextGetCoin.setTextColor(c);
        mTextChangeCoin.setTextColor(c);
        mTextUser.setTextColor(c);
    }

    private void iconActive(int i ) {
        @SuppressLint("ResourceType") int c = Color.parseColor(getString(R.color.icon));
        switch (i) {
            case 0:
                mImgFind.setTextColor(c);
                mTextFind.setTextColor(c);
                break;
            case 1:
                mImgGetCoin.setTextColor(c);
                mTextGetCoin.setTextColor(c);
                break;
            case 2:
                mImgChangeCoin.setTextColor(c);
                mTextChangeCoin.setTextColor(c);
                break;
            case 3:
                mImgUser.setTextColor(c);
                mTextUser.setTextColor(c);
                break;
        }

    }

    private void hideFragments(FragmentTransaction transaction) {
        if (mFragFind != null) {
            transaction.hide(mFragFind);
        }
        if (mFragGetCoin != null) {
            transaction.hide(mFragGetCoin);
        }
        if (mFragChangeCoin != null) {
            transaction.hide(mFragChangeCoin);
        }
        if (mFragUser != null) {
            transaction.hide(mFragUser);
        }
    }

}