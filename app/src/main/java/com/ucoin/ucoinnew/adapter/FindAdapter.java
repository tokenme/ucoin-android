package com.ucoin.ucoinnew.adapter;

import android.net.Uri;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.ucoin.ucoinnew.R;
import com.ucoin.ucoinnew.entity.FindEntity;
import com.ucoin.ucoinnew.util.UiUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class FindAdapter extends BaseQuickAdapter<FindEntity, BaseViewHolder> {
    public FindAdapter(int layoutResId, List data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, FindEntity entity) {
        helper.setText(R.id.entity_find_title, entity.getTitle());
        helper.setText(R.id.entity_find_desc, entity.getDesc());
        helper.setText(R.id.entity_find_coin_name, entity.getCoinName());
        helper.setText(R.id.entity_find_coin_num, String.valueOf(entity.getCoinNum()));

        String pic = entity.getPic();
        Uri uri = Uri.parse(pic);
        SimpleDraweeView draweeView = helper.getView(R.id.entity_find_pic);
        draweeView.setImageURI(uri);

        String coinPic = entity.getCoinPic();
        Uri coinPicUri = Uri.parse(coinPic);
        SimpleDraweeView coinPicDarweeView = helper.getView(R.id.entity_find_coin_pic);
        coinPicDarweeView.setImageURI(coinPicUri);

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
    }
}
