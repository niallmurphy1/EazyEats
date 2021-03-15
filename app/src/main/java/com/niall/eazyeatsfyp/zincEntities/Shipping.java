package com.niall.eazyeatsfyp.zincEntities;

public class Shipping {

    private String order_by;
    private int max_days;
    private double max_price;

    public Shipping() {


    }

    public Shipping(String order_by, int max_days, double max_price) {
        this.order_by = order_by;
        this.max_days = max_days;
        this.max_price = max_price;
    }

    public String getOrder_by() {
        return order_by;
    }

    public void setOrder_by(String order_by) {
        this.order_by = order_by;
    }

    public int getMax_days() {
        return max_days;
    }

    public void setMax_days(int max_days) {
        this.max_days = max_days;
    }

    public double getMax_price() {
        return max_price;
    }

    public void setMax_price(double max_price) {
        this.max_price = max_price;
    }
}
