package com.ucoin.ucoinnew.entity;

public class UserCoinEntity {
    private Long mId;
    private String mName;
    private String mPic;
    private String mPrice;
    private String mMarcketValue;
    private Integer mNum;
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

    public Integer getNum() {
        return mNum;
    }

    public void setNum(Integer num) {
        this.mNum = num;
    }

    public String getMarketValue() {
        return mMarcketValue;
    }

    public void setMarcketValue(String marcketValue) {
        this.mMarcketValue = marcketValue;
    }

    public Class<?> getActivity() {
        return mActivity;
    }

    public void setActivity(Class<?> activity) {
        this.mActivity = activity;
    }
}