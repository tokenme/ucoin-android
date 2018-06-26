package com.ucoin.ucoinnew.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.orhanobut.logger.Logger;
import com.ucoin.ucoinnew.R;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

public class CoinTaskActivity extends AppCompatActivity {

    private CommonTitleBar mTitleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_task);
        init();
    }

    private void init() {
        initTitleBar();
        initView();
        initClick();
    }

    private void initClick() {
    }

    @SuppressLint("ResourceAsColor")
    private void initView() {
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String desc = intent.getStringExtra("desc");
        String coinName = intent.getStringExtra("coin_name");
        String coinPic = intent.getStringExtra("coin_pic");
        String userName = intent.getStringExtra("user_name");
        String userAvatar = intent.getStringExtra("user_avatar");

        TextView userNameView = findViewById(R.id.activity_coin_task_user_name);
        userNameView.setText(userName);

        Uri userAvatarUri = Uri.parse(userAvatar);
        SimpleDraweeView userDavatarDraweeView = findViewById(R.id.activity_coin_task_user_avatar);
        RoundingParams roundingParams = RoundingParams.fromCornersRadius(20f);
        roundingParams.setRoundAsCircle(true);
        userDavatarDraweeView.getHierarchy().setRoundingParams(roundingParams);
        userDavatarDraweeView.setImageURI(userAvatarUri);

        TextView descCoinNameView = findViewById(R.id.activity_coin_task_desc_coin_name);
        descCoinNameView.setText(coinName);

        TextView titleView = findViewById(R.id.activity_coin_task_title);
        titleView.setText(title);

        TextView descView = findViewById(R.id.activity_coin_task_desc);
        descView.setText(desc);
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
}
