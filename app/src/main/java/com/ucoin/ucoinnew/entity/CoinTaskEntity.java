package com.ucoin.ucoinnew.entity;

import org.json.JSONArray;

public class CoinTaskEntity {
    private String mTitle;
    private String mDesc;
    private String mStartDate;
    private String mEndDate;
    private int mBonus;
    private Double mAmount;
    private JSONArray mImages;
    private int mNeedEvidence;
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

    public int getBonus() {
        return mBonus;
    }

    public void setBonus(int bonus) {
        this.mBonus = bonus;
    }

    public int getNeedEvidence() {
        return mNeedEvidence;
    }

    public void setNeedEvidence(int needEvidence) {
        this.mNeedEvidence = needEvidence;
    }

    public Double getAmount() {
        return mAmount;
    }

    public void setAmount(Double amount) {
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