package com.ucoin.ucoinnew.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.orhanobut.logger.Logger;
import com.ucoin.ucoinnew.R;
import com.ucoin.ucoinnew.util.UiUtil;

public class CoinTaskActivity extends BaseActivity {

    private Intent mIntent;

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
        Button getTask = findViewById(R.id.activity_coin_task_get_task);
        getTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIntent.setClass(CoinTaskActivity.this, CoinTaskDetailActivity.class);
                startActivity(mIntent);
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    private void initView() {
        mIntent = getIntent();
        String title = mIntent.getStringExtra("title");
        String desc = mIntent.getStringExtra("desc");
        String coinName = mIntent.getStringExtra("coin_name");
        String coinPic = mIntent.getStringExtra("coin_pic");
        String userName = mIntent.getStringExtra("user_name");
        String userAvatar = mIntent.getStringExtra("user_avatar");
        String pic = mIntent.getStringExtra("pic");
        String startDate = mIntent.getStringExtra("start_date");
        String endDate = mIntent.getStringExtra("end_date");

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

        TextView coinNameView = findViewById(R.id.activity_coin_task_coin_name);
        coinNameView.setText(coinName);

        TextView startDateView = findViewById(R.id.activity_coin_task_start_date);
        startDateView.setText(startDate);

        TextView endDateView = findViewById(R.id.activity_coin_task_end_date);
        endDateView.setText(endDate);

        int i = 3;
        int screenWidth = UiUtil.getScreenWidth(CoinTaskActivity.this);
        android.support.v7.widget.GridLayout picsWrapper = findViewById(R.id.activity_coin_task_pics);
        while (i > 0) {
            Uri picUri = Uri.parse(pic);
            SimpleDraweeView picView = new SimpleDraweeView(CoinTaskActivity.this);
            picView.setImageURI(picUri);

            GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();

            int w = (int) Math.round(screenWidth / 4);
            layoutParams.width = Integer.valueOf(w);
            layoutParams.height = layoutParams.width;
            picView.setLayoutParams(layoutParams);
            picView.setPadding(0, 6, 12, 6);
            RoundingParams picRoundingParams = RoundingParams.fromCornersRadius(16f);
            picRoundingParams.setBorder(R.color.colorWhite, 0.1f);
            picView.getHierarchy().setRoundingParams(picRoundingParams);

            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(picUri)
                    .setResizeOptions(new ResizeOptions(layoutParams.width, layoutParams.height))
                    .build();
            PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                    .setOldController(picView.getController())
                    .setImageRequest(request)
                    .build();
            picView.setController(controller);
            picsWrapper.addView(picView);
            i --;
        }
    }

    private void initTitleBar() {
    }
}
