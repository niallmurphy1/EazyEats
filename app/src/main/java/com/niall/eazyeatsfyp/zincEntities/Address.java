package com.niall.eazyeatsfyp.zincEntities;

public class Address {

    private String first_name;
    private String last_name;
    private String address_line1;
    private String address_line2;
    private String zip_code;
    private String city;
    private String state;
    //ISO country code
    private String country;
    private String phone_number;


    public Address(String first_name, String last_name, String address_line1, String address_line2, String zip_code, String city, String state, String country, String phone_number) {
        this.first_name = first_name;
        this.last_name = last_name;
        this.address_line1 = address_line1;
        this.address_line2 = address_line2;
        this.zip_code = zip_code;
        this.city = city;
        this.state = state;
        this.country = country;
        this.phone_number = phone_number;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getAddress_line1() {
        return address_line1;
    }

    public void setAddress_line1(String address_line1) {
        this.address_line1 = address_line1;
    }

    public String getAddress_line2() {
        return address_line2;
    }

    public void setAddress_line2(String address_line2) {
        this.address_line2 = address_line2;
    }

    public String getZip_code() {
        return zip_code;
    }

    public void setZip_code(String zip_code) {
        this.zip_code = zip_code;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }


}
