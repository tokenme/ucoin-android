package com.ucoin.ucoinnew.adapter;

import android.annotation.SuppressLint;
import android.net.Uri;
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
import com.ucoin.ucoinnew.R;
import com.ucoin.ucoinnew.entity.CoinTaskEntity;
import com.ucoin.ucoinnew.util.DTUtil;
import com.ucoin.ucoinnew.util.UiUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

public class CoinTaskAdapter extends BaseQuickAdapter<CoinTaskEntity, BaseViewHolder> {
    public CoinTaskAdapter(int layoutResId, List data) {
        super(layoutResId, data);
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void convert(BaseViewHolder helper, CoinTaskEntity entity) {
        helper.setText(R.id.entity_coin_task_title, entity.getTitle());
        helper.setText(R.id.entity_coin_task_bonus, String.valueOf(entity.getBonus()));
        helper.setText(R.id.entity_coin_task_amount, String.valueOf(entity.getAmount()));

        String startDateStr = entity.getStartDate();
        String endDateStr = entity.getEndDate();

        try {
            Date startDate = DTUtil.dateParse(startDateStr.replace("T", " ").replace("Z", ""), DTUtil.DATE_TIME_API_PARSE_PATTERN);
            Date endDate = DTUtil.dateParse(endDateStr.replace("T", " ").replace("Z", ""), DTUtil.DATE_TIME_API_PARSE_PATTERN);
            helper.setText(R.id.entity_coin_task_start_date, DTUtil.dateFormat(startDate, DTUtil.DATE_PATTERN));
            helper.setText(R.id.entity_coin_task_end_date, DTUtil.dateFormat(endDate, DTUtil.DATE_PATTERN));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        JSONArray images = entity.getImages();
        int screenWidth = UiUtil.getScreenWidth(mContext);
        if (images != null && images.length() > 0) {
            android.support.v7.widget.GridLayout imagesWrapper = helper.getView(R.id.entity_coin_task_images);
            imagesWrapper.removeAllViews();
            for (int i = 0; i < images.length(); i ++) {
                try {
                    String pic = images.getString(i);
                    Uri picUri = Uri.parse(pic);
                    SimpleDraweeView picView = new SimpleDraweeView(mContext);
                    picView.setImageURI(picUri);

                    GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams();

                    int w = (int) Math.round(screenWidth / 3) - 18;
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
                    imagesWrapper.addView(picView);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
