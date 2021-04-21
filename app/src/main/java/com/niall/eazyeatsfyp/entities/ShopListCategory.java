package com.niall.eazyeatsfyp.entities;

import java.util.ArrayList;

public class ShopListCategory {


    private String name;
    private ArrayList<ShoppingListItem> items;


    public ShopListCategory(String name) {
        this.name = name;
        items = new ArrayList<>();

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<ShoppingListItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<ShoppingListItem> items) {
        this.items = items;
    }

    public void addItemToItems(ShoppingListItem item){

        this.items.add(item);
    }

    @Override
    public String toString() {
        return "ShopListCategory{" +
                "name='" + name + '\'' +
                ", items=" + items +
                '}';
    }
}
