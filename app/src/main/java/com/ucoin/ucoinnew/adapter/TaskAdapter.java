package com.ucoin.ucoinnew.adapter;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.GridLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.backends.pipeline.PipelineDraweeController;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.orhanobut.logger.Logger;
import com.ucoin.ucoinnew.R;
import com.ucoin.ucoinnew.entity.TaskEntity;
import com.ucoin.ucoinnew.util.UiUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class TaskAdapter extends BaseQuickAdapter<TaskEntity, BaseViewHolder> {
    public TaskAdapter(int layoutResId, List data) {
        super(layoutResId, data);
    }

    @SuppressLint("ResourceAsColor")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void convert(BaseViewHolder helper, TaskEntity entity) {
        helper.setText(R.id.entity_task_title, entity.getTitle());
        helper.setText(R.id.entity_task_desc, entity.getDesc());
        helper.setText(R.id.entity_task_coin_name, entity.getCoinName());
        helper.setText(R.id.entity_task_start_date, entity.getStartDate());
        helper.setText(R.id.entity_task_end_date, entity.getEndDate());
        helper.setText(R.id.entity_task_coin_num, String.valueOf(entity.getCoinNum()));

        String coinPic = entity.getCoinPic();
        Uri coinPicUri = Uri.parse(coinPic);
        SimpleDraweeView coinPicDraweeView = helper.getView(R.id.entity_task_coin_pic);
        coinPicDraweeView.setImageURI(coinPicUri);

        JSONArray pics = entity.getPics();
        int screenWidth = UiUtil.getScreenWidth(mContext);
        if (pics.length() > 0) {
            android.support.v7.widget.GridLayout picsWrapper = helper.getView(R.id.entity_task_pics);
            picsWrapper.removeAllViews();
            for (int i = 0; i < pics.length(); i ++) {
                try {
                    String pic = pics.getString(i);
                    Uri picUri = Uri.parse(pic);
                    SimpleDraweeView picView = new SimpleDraweeView(mContext);
                    picView.setImageURI(picUri);

                    GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();

                    int w = (int) Math.round(screenWidth / 3.5);
                    layoutParams.width = Integer.valueOf(w);
                    layoutParams.height = layoutParams.width;
                    picView.setLayoutParams(layoutParams);
                    picView.setPadding(6, 6, 6, 6);
                    RoundingParams roundingParams = RoundingParams.fromCornersRadius(16f);
                    roundingParams.setBorder(R.color.colorWhite, 0.1f);
                    picView.getHierarchy().setRoundingParams(roundingParams);

                    ImageRequest request = ImageRequestBuilder.newBuilderWithSource(picUri)
                            .setResizeOptions(new ResizeOptions(layoutParams.width, layoutParams.height))
                            .build();
                    PipelineDraweeController controller = (PipelineDraweeController) Fresco.newDraweeControllerBuilder()
                            .setOldController(picView.getController())
                            .setImageRequest(request)
                            .build();
                    picView.setController(controller);
                    picsWrapper.addView(picView);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
        helper.addOnClickListener(R.id.entity_task_go_to_make);
    }
}
