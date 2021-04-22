package com.niall.eazyeatsfyp.entities;


import androidx.annotation.NonNull;

import java.util.HashMap;
import java.util.Map;

public class ShoppingListItem {


    private String sId;
    private String name;
    private String barcodeUPC;
    private String category;


    public Map<String, Object> toMap(){
        HashMap<String, Object> result = new HashMap<>();
        result.put("sId", sId);
        result.put("name", name);
        result.put("category", category);
        return result;
    }

    public ShoppingListItem(String sId, String name, String category) {
       this.sId = sId;
        this.name = name;
        this.category = category;
    }




    public ShoppingListItem(){

    }


    public String getsId() {
        return sId;
    }

    public void setsId(String sId) {
        this.sId = sId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getBarcodeUPC() {
        return barcodeUPC;
    }

    public void setBarcodeUPC(String barcodeUPC) {
        this.barcodeUPC = barcodeUPC;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "ShoppingListItem{" +
                "sId='" + sId + '\'' +
                ", name='" + name + '\'' +
                ", category='" + category + '\'' +
                '}';
    }
}
