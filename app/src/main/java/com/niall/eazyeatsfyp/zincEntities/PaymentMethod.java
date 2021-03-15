package com.niall.eazyeatsfyp.zincEntities;

public class PaymentMethod {

    private String name_on_card;
    //cardNo
    private String number;
    private String security_code;
    private int expiration_month;
    private int expiration_year;
    private boolean use_gift = false;

    public PaymentMethod(){

    }

    public PaymentMethod(String name_on_card, String number, String security_code, int expiration_month, int expiration_year, boolean use_gift) {
        this.name_on_card = name_on_card;
        this.number = number;
        this.security_code = security_code;
        this.expiration_month = expiration_month;
        this.expiration_year = expiration_year;
        this.use_gift = use_gift;
    }

    public String getName_on_card() {
        return name_on_card;
    }

    public void setName_on_card(String name_on_card) {
        this.name_on_card = name_on_card;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getSecurity_code() {
        return security_code;
    }

    public void setSecurity_code(String security_code) {
        this.security_code = security_code;
    }

    public int getExpiration_month() {
        return expiration_month;
    }

    public void setExpiration_month(int expiration_month) {
        this.expiration_month = expiration_month;
    }

    public int getExpiration_year() {
        return expiration_year;
    }

    public void setExpiration_year(int expiration_year) {
        this.expiration_year = expiration_year;
    }

    public boolean isUse_gift() {
        return use_gift;
    }

    public void setUse_gift(boolean use_gift) {
        this.use_gift = use_gift;
    }

    @Override
    public String toString() {
        return "PaymentMethod{" +
                "name_on_card='" + name_on_card + '\'' +
                ", number='" + number + '\'' +
                ", security_code='" + security_code + '\'' +
                ", expiration_month=" + expiration_month +
                ", expiration_year=" + expiration_year +
                ", use_gift=" + use_gift +
                '}';
    }
}
