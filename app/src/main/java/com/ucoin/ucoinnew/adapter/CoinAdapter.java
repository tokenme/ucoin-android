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
import com.ucoin.ucoinnew.R;
import com.ucoin.ucoinnew.entity.CoinEntity;
import com.ucoin.ucoinnew.entity.TaskEntity;
import com.ucoin.ucoinnew.util.UiUtil;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

public class CoinAdapter extends BaseQuickAdapter<CoinEntity, BaseViewHolder> {
    public CoinAdapter(int layoutResId, List data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CoinEntity entity) {
        helper.setText(R.id.entity_coin_name, entity.getName());
        helper.setText(R.id.entity_coin_price, entity.getPrice());
        helper.setText(R.id.entity_coin_market_value, entity.getMarketValue());
        helper.setText(R.id.entity_coin_increase, String.valueOf(entity.getIncrease()));
        helper.setText(R.id.entity_coin_index, String.valueOf(helper.getAdapterPosition()));

        /*
        String pic = entity.getPic();
        Uri picUri = Uri.parse(pic);
        SimpleDraweeView picDraweeView = helper.getView(R.id.entity_coin_pic);
        picDraweeView.setImageURI(picUri);
        */
    }
}
