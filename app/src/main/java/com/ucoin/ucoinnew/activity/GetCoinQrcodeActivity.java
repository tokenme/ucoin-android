package com.ucoin.ucoinnew.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ucoin.ucoinnew.R;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

public class GetCoinQrcodeActivity extends AppCompatActivity {

    private CommonTitleBar mTitleBar;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_coin_qrcode);

        init();
    }

    private void init() {
        initTitleBar();
        initView();
    }

    private void initTitleBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        mTitleBar = findViewById(R.id.title_bar);
        View leftCustomLayout = mTitleBar.getLeftCustomView();
        leftCustomLayout.findViewById(R.id.title_bar_left_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initView() {
    }

    public void onClick(View v) {
    }
}
