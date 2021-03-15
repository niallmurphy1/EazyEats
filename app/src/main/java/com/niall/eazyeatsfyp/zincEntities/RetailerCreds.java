package com.niall.eazyeatsfyp.zincEntities;

import java.util.HashMap;
import java.util.Map;

public class RetailerCreds {


    private String email;
    private String password;

    //optional
    private String totp2faKey;

    public RetailerCreds(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public RetailerCreds(){

    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("email", email);
        result.put("password", password);
        return result;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getTotp2faKey() {
        return totp2faKey;
    }

    public void setTotp2faKey(String totp2faKey) {
        this.totp2faKey = totp2faKey;
    }


    @Override
    public String toString() {
        return "ReatilerCreds{" +
                "email='" + email + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
