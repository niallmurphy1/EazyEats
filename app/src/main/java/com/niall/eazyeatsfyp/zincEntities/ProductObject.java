package com.niall.eazyeatsfyp.zincEntities;

import com.google.gson.annotations.SerializedName;

import java.util.HashMap;
import java.util.Map;

public class ProductObject {

    private String product_id;
    //@SerializedName("title")
    private String title;
    private int quantity;
    private SellerSelectionCriteria sellerSelectionCriteria;
    private double stars;
    private int num_reviews;
    private int price;
    private String image;

    public ProductObject(String product_id, String title, int quantity, double stars, int num_reviews, int price, String image) {
        this.product_id = product_id;
        this.title = title;
        this.quantity = quantity;
        this.stars = stars;
        this.num_reviews = num_reviews;
        this.price = price;
        this.image = image;
    }

    public ProductObject(String product_id, int quantity) {
        this.product_id = product_id;
        this.quantity = quantity;
    }

    public ProductObject(){


    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("product_id", product_id);
        result.put("title", title);
        result.put("quantity", quantity);
        result.put("stars", stars);
        result.put("num_reviews", num_reviews);
        result.put("price", price);
        result.put("image", image);
        return result;

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String productName) {
        this.title = productName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getProduct_id() {
        return product_id;
    }


    public int getNum_reviews() {
        return num_reviews;
    }

    public void setNum_reviews(int num_reviews) {
        this.num_reviews = num_reviews;
    }

    public void setProduct_id(String product_id) {
        this.product_id = product_id;
    }

    public int getQuantity() {
        return quantity;
    }

    public double getStars() {
        return stars;
    }

    public void setStars(double stars) {
        this.stars = stars;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public SellerSelectionCriteria getSellerSelectionCriteria() {
        return sellerSelectionCriteria;
    }

    public void setSellerSelectionCriteria(SellerSelectionCriteria sellerSelectionCriteria) {
        this.sellerSelectionCriteria = sellerSelectionCriteria;
    }


    @Override
    public String toString() {
        return "ProductObject{" +
                "product_id='" + product_id + '\'' +
                ", title='" + title + '\'' +
                ", quantity=" + quantity +
                ", stars=" + stars +
                ", num_reviews=" + num_reviews +
                ", price=" + price +
                ", image='" + image + '\'' +
                '}';
    }
}

