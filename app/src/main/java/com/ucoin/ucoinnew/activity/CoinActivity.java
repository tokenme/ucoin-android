package com.ucoin.ucoinnew.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.ucoin.ucoinnew.R;

public class CoinActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin);
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
        String coinName = intent.getStringExtra("coin_name");
        String coinPic = intent.getStringExtra("coin_pic");
        String userName = intent.getStringExtra("user_name");
        String userAvatar = intent.getStringExtra("user_avatar");

        Uri coinPicUri = Uri.parse(coinPic);
        SimpleDraweeView coinPicDraweeView = findViewById(R.id.activity_coin_pic);
        coinPicDraweeView.setImageURI(coinPicUri);

        TextView coinNameView = findViewById(R.id.activity_coin_name);
        coinNameView.setText(coinName);

        TextView userNameView = findViewById(R.id.activity_coin_user_name);
        userNameView.setText(userName);

        Uri userAvatarUri = Uri.parse(userAvatar);
        SimpleDraweeView userDavatarDraweeView = findViewById(R.id.activity_coin_user_avatar);
        RoundingParams roundingParams = RoundingParams.fromCornersRadius(20f);
        roundingParams.setRoundAsCircle(true);
        userDavatarDraweeView.getHierarchy().setRoundingParams(roundingParams);
        userDavatarDraweeView.setImageURI(userAvatarUri);
    }

    private void initTitleBar() {
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
    }
}
