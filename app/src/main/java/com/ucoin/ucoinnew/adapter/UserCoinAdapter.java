package com.ucoin.ucoinnew.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ucoin.ucoinnew.R;
import com.ucoin.ucoinnew.entity.UserCoinEntity;

import java.util.List;

public class UserCoinAdapter extends BaseQuickAdapter<UserCoinEntity, BaseViewHolder> {
    public UserCoinAdapter(int layoutResId, List data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, UserCoinEntity entity) {
        helper.setText(R.id.entity_coin_name, entity.getName());
        helper.setText(R.id.entity_coin_price, entity.getPrice());
        helper.setText(R.id.entity_coin_market_value, entity.getMarketValue());
        helper.setText(R.id.entity_coin_index, String.valueOf(helper.getAdapterPosition()));
    }
}
