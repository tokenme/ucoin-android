package com.ucoin.ucoinnew.entity;

import org.json.JSONArray;

import java.util.ArrayList;

public class CoinProductEntity {
    private String mTitle;
    private String mDesc;
    private String mStartDate;
    private String mEndDate;
    private int mTotalSupply;
    private int mPrice;
    private int mAmount;
    private JSONArray mImages;
    private Class<?> mActivity;

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public String getDesc() {
        return mDesc;
    }

    public void setDesc(String desc) {
        this.mDesc = desc;
    }

    public String getStartDate() {
        return mStartDate;
    }

    public void setStartDate(String startDate) {
        this.mStartDate = startDate;
    }

    public String getEndDate() {
        return mEndDate;
    }

    public void setEndDate(String endDate) {
        this.mEndDate = endDate;
    }

    public int getPrice() {
        return mPrice;
    }

    public void setPrice(int price) {
        this.mPrice = price;
    }

    public int getmTotalSupply() {
        return mTotalSupply;
    }

    public void setTotalSupply(int totalSupply) {
        this.mTotalSupply = totalSupply;
    }

    public int getAmount() {
        return mAmount;
    }

    public void setAmount(int amount) {
        this.mAmount = amount;
    }

    public JSONArray getImages() {
        return mImages;
    }

    public void setImages(JSONArray images) {
        this.mImages = images;
    }

    public Class<?> getActivity() {
        return mActivity;
    }

    public void setActivity(Class<?> activity) {
        this.mActivity = activity;
    }
}