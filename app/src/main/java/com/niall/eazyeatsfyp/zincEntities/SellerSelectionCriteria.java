package com.niall.eazyeatsfyp.zincEntities;

import java.lang.reflect.Array;

public class SellerSelectionCriteria {

    private String addOn;

    //irrelevant because food
    private Array conditionIn;

    private String maxHandlingDays;
    private String maxItemPrice;
    private boolean prime;

    public SellerSelectionCriteria(String addOn, String maxHandlingDays, String maxItemPrice, boolean prime) {
        this.addOn = addOn;
        this.maxHandlingDays = maxHandlingDays;
        this.maxItemPrice = maxItemPrice;
        this.prime = prime;
    }

    public String getAddOn() {
        return addOn;
    }

    public void setAddOn(String addOn) {
        this.addOn = addOn;
    }

    public Array getConditionIn() {
        return conditionIn;
    }

    public void setConditionIn(Array conditionIn) {
        this.conditionIn = conditionIn;
    }

    public String getMaxHandlingDays() {
        return maxHandlingDays;
    }

    public void setMaxHandlingDays(String maxHandlingDays) {
        this.maxHandlingDays = maxHandlingDays;
    }

    public String getMaxItemPrice() {
        return maxItemPrice;
    }

    public void setMaxItemPrice(String maxItemPrice) {
        this.maxItemPrice = maxItemPrice;
    }

    public boolean isPrime() {
        return prime;
    }

    public void setPrime(boolean prime) {
        this.prime = prime;
    }
}
