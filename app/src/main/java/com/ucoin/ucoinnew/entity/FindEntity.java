package com.ucoin.ucoinnew.entity;

import org.json.JSONArray;

public class FindEntity {
    private String mTitle;
    private String mDesc;
    private String mPic;
    private String mCoinName;
    private String mCoinPic;
    private Double mCoinNum;
    private String mStartDate;
    private String mEndDate;
    private String mUserAvatar;
    private String mUserName;
    private int mExchangeNum;
    private int mLikeNum;
    private JSONArray mTags;
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

    public JSONArray getTags() {
        return mTags;
    }

    public void setTags(JSONArray tags) {
        this.mTags = tags;
    }

    public String getPic() {
        return mPic;
    }

    public void setPic(String pic) {
        this.mPic = pic;
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

    public String getUserAvatar() {
        return mUserAvatar;
    }

    public void setUserAvatar(String userAvatar) {
        this.mUserAvatar = userAvatar;
    }

    public String getUserName() {
        return mUserName;
    }

    public void setUserName(String userName) {
        this.mUserName = userName;
    }

    public void setExchangeNum(int exchangeNum) {
        this.mExchangeNum = exchangeNum;
    }

    public int getExchangeNum() {
        return mExchangeNum;
    }

    public void setLikeNum(int likeNum) {
        this.mLikeNum = likeNum;
    }

    public int getLikeNum() {
        return mLikeNum;
    }

    public Class<?> getActivity() {
        return mActivity;
    }

    public void setActivity(Class<?> activity) {
        this.mActivity = activity;
    }
}