package com.happyselling.niznet.Models;

public class SettingsModel {

    Integer iv_profile;

    String tv_account;

    public SettingsModel(Integer iv_profile, String tv_account) {
        this.iv_profile = iv_profile;
        this.tv_account = tv_account;
    }

    public Integer getIv_profile() {
        return iv_profile;
    }

    public void setIv_profile(Integer iv_profile) {
        this.iv_profile = iv_profile;
    }

    public String getTv_account() {
        return tv_account;
    }

    public void setTv_account(String tv_account) {
        this.tv_account = tv_account;
    }
}