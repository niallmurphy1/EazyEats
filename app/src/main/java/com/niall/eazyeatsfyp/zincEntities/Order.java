package com.niall.eazyeatsfyp.zincEntities;

import java.util.List;

public class Order {

    private String retailer;
    private List<ProductObject> products;
    private Address shipping_address;
    private Shipping shipping;
    private Address billing_address;
    private PaymentMethod payment_method;
    private RetailerCreds retailer_credentials;
    private boolean is_gift = false;
    private Number max_price =0;

    public Order(String retailer, List<ProductObject> products, Address shipping_address, Shipping shipping, Address billing_address, PaymentMethod payment_method, RetailerCreds retailer_credentials, boolean is_gift, Number max_price) {
        this.retailer = retailer;
        this.products = products;
        this.shipping_address = shipping_address;
        this.shipping = shipping;
        this.billing_address = billing_address;
        this.payment_method = payment_method;
        this.retailer_credentials = retailer_credentials;
        this.is_gift = is_gift;
        this.max_price = max_price;
    }



    public String getRetailer() {
        return retailer;
    }

    public void setRetailer(String retailer) {
        this.retailer = retailer;
    }

    public List<ProductObject> getProducts() {
        return products;
    }

    public void setProducts(List<ProductObject> products) {
        this.products = products;
    }

    public Address getShipping_address() {
        return shipping_address;
    }

    public void setShipping_address(Address shipping_address) {
        this.shipping_address = shipping_address;
    }



    public Shipping getShipping() {
        return shipping;
    }

    public void setShipping(Shipping shipping) {
        this.shipping = shipping;
    }

    public Address getBilling_address() {
        return billing_address;
    }

    public void setBilling_address(Address billing_address) {
        this.billing_address = billing_address;
    }

    public PaymentMethod getPayment_method() {
        return payment_method;
    }

    public void setPayment_method(PaymentMethod payment_method) {
        this.payment_method = payment_method;
    }

    public RetailerCreds getRetailer_credentials() {
        return retailer_credentials;
    }

    public void setRetailer_credentials(RetailerCreds retailer_credentials) {
        this.retailer_credentials = retailer_credentials;
    }

    public boolean isIs_gift() {
        return is_gift;
    }

    public void setIs_gift(boolean is_gift) {
        this.is_gift = is_gift;
    }

    public Number getMax_price() {
        return max_price;
    }

    public void setMax_price(Number max_price) {
        this.max_price = max_price;
    }

    @Override
    public String toString() {
        return "Order{" +
                "retailer='" + retailer + '\'' +
                ", products=" + products +
                ", shipping_address=" + shipping_address +
                ", shipping=" + shipping +
                ", billing_address=" + billing_address +
                ", payment_method=" + payment_method +
                ", retailer_credentials=" + retailer_credentials +
                ", is_gift=" + is_gift +
                ", max_price=" + max_price +
                '}';
    }
}
