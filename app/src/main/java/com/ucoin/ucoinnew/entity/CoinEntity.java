package com.ucoin.ucoinnew.entity;

import org.json.JSONArray;

public class CoinEntity {
    private Long mId;
    private String mName;
    private String mPic;
    private String mPrice;
    private String mMarcketValue;
    private Double mIncrease;
    private Class<?> mActivity;

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        this.mId = id;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getPic() {
        return mPic;
    }

    public void setPic(String pic) {
        this.mPic = pic;
    }

    public String getPrice() {
        return mPrice;
    }

    public void setPrice(String price) {
        this.mPrice = price;
    }

    public String getMarketValue() {
        return mMarcketValue;
    }

    public void setMarcketValue(String marcketValue) {
        this.mMarcketValue = marcketValue;
    }

    public Double getIncrease() {
        return mIncrease;
    }

    public void setIncrease(Double increase) {
        this.mIncrease = increase;
    }

    public Class<?> getActivity() {
        return mActivity;
    }

    public void setActivity(Class<?> activity) {
        this.mActivity = activity;
    }
}