package com.ucoin.ucoinnew.adapter;

import android.net.Uri;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.view.SimpleDraweeView;
import com.ucoin.ucoinnew.R;
import com.ucoin.ucoinnew.entity.UserCoinEntity;

import java.util.List;

public class UserCoinAdapter extends BaseQuickAdapter<UserCoinEntity, BaseViewHolder> {
    public UserCoinAdapter(int layoutResId, List data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, UserCoinEntity entity) {
        helper.setText(R.id.entity_user_coin_name, entity.getName());
        helper.setText(R.id.entity_user_coin_balance, String.valueOf(entity.getBalance()));
        helper.setText(R.id.entity_user_coin_total_supply, String.valueOf(entity.getTotalSupply()));
        helper.setText(R.id.entity_user_coin_index, String.valueOf(helper.getAdapterPosition()));

        /*
        String logo = entity.getLogo();
        Uri logoUri = Uri.parse(logo);
        SimpleDraweeView logoDraweeView = helper.getView(R.id.entity_user_coin_logo);
        logoDraweeView.setImageURI(logoUri);
        */
    }
}
