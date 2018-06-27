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
import com.ucoin.ucoinnew.R;
import com.ucoin.ucoinnew.util.UiUtil;
import com.wuhenzhizao.titlebar.widget.CommonTitleBar;

public class CoinTaskDetailActivity extends AppCompatActivity {

    private CommonTitleBar mTitleBar;
    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coin_task_detail);
        init();
    }

    private void init() {
        initTitleBar();
        initView();
        initClick();
    }

    private void initClick() {
        Button goToMakeTaskView = findViewById(R.id.activity_coin_task_go_to_make_task);
        goToMakeTaskView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                intent.setClass(CoinTaskDetailActivity.this, MakeTaskActivity.class);
                startActivity(intent);
            }
        });
    }

    @SuppressLint("ResourceAsColor")
    private void initView() {
        intent = getIntent();
        String title = intent.getStringExtra("title");
        String desc = intent.getStringExtra("desc");
        String coinName = intent.getStringExtra("coin_name");
        String coinPic = intent.getStringExtra("coin_pic");
        String userName = intent.getStringExtra("user_name");
        String userAvatar = intent.getStringExtra("user_avatar");
        String pic = intent.getStringExtra("pic");
        String startDate = intent.getStringExtra("start_date");
        String endDate = intent.getStringExtra("end_date");

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

        TextView userNameView = findViewById(R.id.activity_coin_task_detail_user_name);
        userNameView.setText(userName);

        Uri userAvatarUri = Uri.parse(userAvatar);
        SimpleDraweeView userDavatarDraweeView = findViewById(R.id.activity_coin_task_detail_user_avatar);
        RoundingParams roundingParams = RoundingParams.fromCornersRadius(20f);
        roundingParams.setRoundAsCircle(true);
        userDavatarDraweeView.getHierarchy().setRoundingParams(roundingParams);
        userDavatarDraweeView.setImageURI(userAvatarUri);

        int i = 3;
        int screenWidth = UiUtil.getScreenWidth(CoinTaskDetailActivity.this);
        android.support.v7.widget.GridLayout picsWrapper = findViewById(R.id.activity_coin_task_pics);
        while (i > 0) {
            Uri picUri = Uri.parse(pic);
            SimpleDraweeView picView = new SimpleDraweeView(CoinTaskDetailActivity.this);
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
