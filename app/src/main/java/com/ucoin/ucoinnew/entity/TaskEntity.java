package com.ucoin.ucoinnew.entity;

import org.json.JSONArray;

public class TaskEntity {
    private Long mId;
    private String mTitle;
    private String mDesc;
    private String mCoinName;
    private String mCoinPic;
    private Double mCoinNum;
    private String mStartDate;
    private String mEndDate;
    private JSONArray mPics;
    private Class<?> mActivity;

    public Long getId() {
        return mId;
    }

    public void setId(Long id) {
        this.mId = id;
    }

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

    public String getCoinName() {
        return mCoinName;
    }

    public void setCoinName(String coinName) {
        this.mCoinName = coinName;
    }

    public String getCoinPic() {
        return mCoinPic;
    }

    public void setCoinPic(String coinPic) {
        this.mCoinPic = coinPic;
    }

    public Double getCoinNum() {
        return mCoinNum;
    }

    public void setCoinNum(Double coinNum) {
        this.mCoinNum = coinNum;
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

    public JSONArray getPics() {
        return mPics;
    }

    public void setPics(JSONArray pics) {
        this.mPics = pics;
    }

    public Class<?> getActivity() {
        return mActivity;
    }

    public void setActivity(Class<?> activity) {
        this.mActivity = activity;
    }
}