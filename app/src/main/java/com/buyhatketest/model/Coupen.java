package com.buyhatketest.model;


import com.google.gson.annotations.Expose;

import java.io.Serializable;

public class Coupen implements Serializable {

    @Expose
    private String coupen_code;

    @Expose
    private String coupen_discount;

    @Expose
    private boolean isMaxDiscount;

    public boolean isMax() {
        return isMaxDiscount;
    }

    public void setMax(boolean max) {
        isMaxDiscount = max;
    }

    public String getCoupen_code() {
        return coupen_code;
    }

    public void setCoupen_code(String coupen_code) {
        this.coupen_code = coupen_code;
    }

    public String getCoupen_discount() {
        return coupen_discount;
    }

    public void setCoupen_discount(String coupen_discount) {
        this.coupen_discount = coupen_discount;
    }
}
