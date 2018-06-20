package com.ucoin.ucoinnew.adapter;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.GridLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.generic.RoundingParams;
import com.facebook.drawee.view.SimpleDraweeView;
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
        SimpleDraweeView coinPicdraweeView = helper.getView(R.id.entity_task_coin_pic);
        coinPicdraweeView.setImageURI(coinPicUri);

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
                    GridLayout.LayoutParams layoutParams = new GridLayout.LayoutParams(
                        GridLayout.spec(GridLayout.UNDEFINED,GridLayout.FILL,1f),
                        GridLayout.spec(GridLayout.UNDEFINED,GridLayout.FILL,1f)
                    );

                    int w = (int) Math.round(screenWidth / 3.5);
                    layoutParams.width = Integer.valueOf(w);
                    layoutParams.height = layoutParams.width;
                    picView.setLayoutParams(layoutParams);
                    picView.setPadding(6, 6, 6, 6);

                    RoundingParams roundingParams = RoundingParams.fromCornersRadius(16f);
                    roundingParams.setBorder(R.color.colorWhite, 0.1f);
                    picView.getHierarchy().setRoundingParams(roundingParams);

                    picsWrapper.addView(picView);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        /*

        JSONArray tags = entity.getTags();
        if (tags.length() > 0) {
            LinearLayout tagsWarpper = helper.getView(R.id.entity_find_tags);
            tagsWarpper.removeAllViews();
            for (int i = 0; i < tags.length(); i ++) {
                TextView tag = new TextView(mContext);
                try {
                    tag.setText(tags.getString(i));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                tagsWarpper.addView(tag);
                tag.setBackgroundResource(R.drawable.find_item_tag_border_radius);
                tag.setPadding(20, 2, 20, 2);
                tag.setTextColor(tagsWarpper.getResources().getColor(R.color.findTag));
                UiUtil.setMargins(tag, 0, 0, 10, 0);
            }
        }
        */
    }
}
