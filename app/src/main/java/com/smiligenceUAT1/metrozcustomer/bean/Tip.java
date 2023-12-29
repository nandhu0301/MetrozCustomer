package com.smiligenceUAT1.metrozcustomer.bean;

public class Tip
{
    String creationDate;
    String url;
    int tipsAmount;
    String tipsName;
    int tipAmount;


    public int getTipAmount() {
        return tipAmount;
    }

    public void setTipAmount(int tipAmount) {
        this.tipAmount = tipAmount;
    }

    public int getTipsAmount() {
        return tipsAmount;
    }

    public void setTipsAmount(int tipsAmount) {
        this.tipsAmount = tipsAmount;
    }

    public String getTipsName() {
        return tipsName;
    }

    public void setTipsName(String tipsName) {
        this.tipsName = tipsName;
    }

    public Tip() {
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
