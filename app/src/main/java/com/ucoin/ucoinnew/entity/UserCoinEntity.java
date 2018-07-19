package com.ucoin.ucoinnew.entity;

public class UserCoinEntity {
    private String mAddress;
    private String mOwner;
    private String mName;
    private String mSymbol;
    private Double mInitialSupply;
    private Double mTotalTransfers;
    private Double mBalance;
    private int mTotalSupply;
    private int mTotalHolders;
    private int mCirculatingSupply;
    private int mDecimals;
    private int mTxStatus;
    private String mLogo;
    private Class<?> mActivity;

    public String getAddress() {
        return mAddress;
    }

    public void setAddress(String address) {
        this.mAddress = address;
    }

    public String getOwner() {
        return mOwner;
    }

    public void setOwner(String owner) {
        this.mOwner = owner;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public String getSymbol() {
        return mSymbol;
    }

    public void setSymbol(String symbol) {
        this.mSymbol = symbol;
    }

    public int getDecimals() {
        return mDecimals;
    }

    public void setDecimals(int decimals) {
        this.mDecimals = decimals;
    }

    public Double getInitialSupply() {
        return mInitialSupply;
    }

    public void setInitialSupply(Double initialSupply) {
        this.mInitialSupply = initialSupply;
    }

    public int getTotalSupply() {
        return mTotalSupply;
    }

    public void setTotalSupply(int totalSupply) {
        this.mTotalSupply = totalSupply;
    }

    public Double getTotalTransfers() {
        return mTotalTransfers;
    }

    public void setTotalTransfers(Double totalTransfers) {
        this.mTotalTransfers = totalTransfers;
    }

    public int getTotalHolders() {
        return mTotalHolders;
    }

    public void setTotalHolders(int totalHolders) {
        this.mTotalHolders = totalHolders;
    }

    public int getCirculatingSupply() {
        return mCirculatingSupply;
    }

    public void setCirculatingSupply(int circulatingSupply) {
        this.mCirculatingSupply = circulatingSupply;
    }

    public Double getBalance() {
        return mBalance;
    }

    public void setBalance(Double balance) {
        this.mBalance = balance;
    }

    public int getTxStatus() {
        return mTxStatus;
    }

    public void setTxStatus(int txStatus) {
        this.mTxStatus = txStatus;
    }

    public String getLogo() {
        return mLogo;
    }

    public void setLogo(String logo) {
        this.mLogo = logo;
    }

    public Class<?> getActivity() {
        return mActivity;
    }

    public void setActivity(Class<?> activity) {
        this.mActivity = activity;
    }
}