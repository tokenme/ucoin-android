package com.ucoin.ucoinnew.entity;

import org.json.JSONArray;

public class FindEntity {
    private String mTitle;
    private String mDesc;
    private String mPic;
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

    public Class<?> getActivity() {
        return mActivity;
    }

    public void setActivity(Class<?> activity) {
        this.mActivity = activity;
    }
}