package com.ucoin.ucoinnew.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ucoin.ucoinnew.R;
import com.ucoin.ucoinnew.entity.CoinEntity;
import com.ucoin.ucoinnew.entity.CoinProductEntity;

import java.util.List;

public class CoinProductAdapter extends BaseQuickAdapter<CoinProductEntity, BaseViewHolder> {
    public CoinProductAdapter(int layoutResId, List data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, CoinProductEntity entity) {
        helper.setText(R.id.entity_coin_product_title, entity.getTitle());
    }
}
