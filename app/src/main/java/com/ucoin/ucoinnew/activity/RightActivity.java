package com.ucoin.ucoinnew.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.orhanobut.logger.Logger;
import com.ucoin.ucoinnew.R;
import com.ucoin.ucoinnew.util.UiUtil;
import com.ucoin.ucoinnew.util.Util;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

public class RightActivity extends AppCompatActivity {

    private CommonTitleBar mTitleBar;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_right);
        init();
    }

    private void init() {
        initTitleBar();
        initView();
        initClick();
    }

    private void initClick() {
        Button btn = findViewById(R.id.activity_right_exchange);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String[] items = {"该权益需要 FVBx100", "您已拥有 FVBx75"};
                new MaterialDialog.Builder(RightActivity.this)
                        .autoDismiss(false)
                        .canceledOnTouchOutside(false)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                intent.setClass(RightActivity.this, CoinTaskActivity.class);
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                intent.setClass(RightActivity.this, CoinActivity.class);
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        })
                        .title(R.string.activity_right_exchange_dialog_title)
                        .items(items)
                        .negativeText(R.string.activity_right_exchange_dialog_buy)
                        .positiveText(R.string.activity_right_exchange_dialog_task)
                        .itemsCallback(new MaterialDialog.ListCallback() {
                            @Override
                            public void onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                Logger.i(String.valueOf(which));
                            }
                        })
                        .show();
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    private void initView() {
        intent = getIntent();
        String title = intent.getStringExtra("title");
        String desc = intent.getStringExtra("desc");
        String pic = intent.getStringExtra("pic");
        String coinName = intent.getStringExtra("coin_name");
        String coinPic = intent.getStringExtra("coin_pic");
        Double coinNum = intent.getDoubleExtra("coin_num", 0.0);
        String startDate = intent.getStringExtra("start_date");
        String endDate = intent.getStringExtra("end_date");
        String userName = intent.getStringExtra("user_name");
        String userAvatar = intent.getStringExtra("user_avatar");
        int exchangeNum = intent.getIntExtra("exchange_num", 0);
        int likeNum = intent.getIntExtra("like_num", 0);

        TextView titleView = findViewById(R.id.activity_right_title);
        titleView.setText(title);

        TextView descView = findViewById(R.id.activity_right_desc);
        descView.setText(desc);

        Uri coinPicUri = Uri.parse(coinPic);
        SimpleDraweeView coinPicDraweeView = findViewById(R.id.activity_right_coin_pic);
        coinPicDraweeView.setImageURI(coinPicUri);

        TextView coinNameView = findViewById(R.id.activity_right_coin_name);
        coinNameView.setText(coinName);

        TextView coinNumView = findViewById(R.id.activity_right_coin_num);
        coinNumView.setText(String.valueOf(coinNum));

        TextView startDateView = findViewById(R.id.activity_right_start_date);
        startDateView.setText(startDate);

        TextView endDateView = findViewById(R.id.activity_right_end_date);
        endDateView.setText(endDate);

        TextView userNameView = findViewById(R.id.activity_right_user_name);
        userNameView.setText(userName);

        TextView exchangeNumView = findViewById(R.id.activity_right_exchange_num);
        exchangeNumView.setText(String.valueOf(exchangeNum));

        TextView likeNumView = findViewById(R.id.activity_right_like_num);
        likeNumView.setText(String.valueOf(likeNum));

        Uri userAvatarUri = Uri.parse(userAvatar);
        SimpleDraweeView userAvatarDraweeView = findViewById(R.id.activity_right_user_avatar);
        RoundingParams userAvatarRoundingParams = RoundingParams.fromCornersRadius(20f);
        userAvatarRoundingParams.setRoundAsCircle(true);
        userAvatarDraweeView.getHierarchy().setRoundingParams(userAvatarRoundingParams);
        userAvatarDraweeView.setImageURI(userAvatarUri);

        Uri picUri = Uri.parse(pic);
        SimpleDraweeView picDraweeView = findViewById(R.id.activity_right_pic);
        picDraweeView.setImageURI(picUri);
        int screenWidth = UiUtil.getScreenWidth(RightActivity.this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, 0);
        layoutParams.width = screenWidth - Util.dip2px(RightActivity.this, 10) * 2;
        layoutParams.height = layoutParams.width;
        RoundingParams roundingParams = RoundingParams.fromCornersRadius(20f);
        picDraweeView.getHierarchy().setRoundingParams(roundingParams);
        picDraweeView.setLayoutParams(layoutParams);

        String avatar = "https://wx.qlogo.cn/mmopen/vi_32/Q0j4TwGTfTLeYdiaVF5aueicbwnSNic3BaajXn61rPMn0HKXdYWHOsM4z8h6vZWyxZ6QQWTkDMKvjL6RVhH7dpLaA/132";
        Uri avatarUri = Uri.parse(avatar);
        SimpleDraweeView avatarDraweeView = findViewById(R.id.activity_right_comment_user_avatar);
        avatarDraweeView.setImageURI(avatarUri);
        RoundingParams avatarRoundingParams = RoundingParams.fromCornersRadius(5f);
        avatarRoundingParams.setRoundAsCircle(true);
        avatarDraweeView.getHierarchy().setRoundingParams(avatarRoundingParams);
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
