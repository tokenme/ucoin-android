package com.ucoin.ucoinnew.adapter;

import android.net.Uri;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.facebook.drawee.generic.RoundingParams;
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
        helper.setText(R.id.entity_user_coin_symbol, entity.getSymbol());
        helper.setText(R.id.entity_user_coin_name, entity.getName());
        helper.setText(R.id.entity_user_coin_balance, String.valueOf(entity.getBalance() / (Math.pow(10, entity.getDecimals()))));
        helper.setText(R.id.entity_user_coin_total_transfers, String.valueOf(entity.getTotalTransfers()));
        helper.setText(R.id.entity_user_coin_total_holders, String.valueOf((int) (entity.getTotalHolders() + 1)));
        /*
        helper.setText(R.id.entity_user_coin_index, String.valueOf(helper.getAdapterPosition()));
        */
        String logo = entity.getLogo();
        Uri logoUri = Uri.parse(logo);
        SimpleDraweeView logoDraweeView = helper.getView(R.id.entity_user_coin_logo);
        logoDraweeView.getHierarchy().setPlaceholderImage(R.drawable.default_coin);
        logoDraweeView.getHierarchy().setFailureImage(R.drawable.default_coin);
        RoundingParams roundingParams = RoundingParams.fromCornersRadius(10f);
        roundingParams.setRoundAsCircle(true);
        logoDraweeView.getHierarchy().setRoundingParams(roundingParams);
        logoDraweeView.setImageURI(logoUri);
    }
}
