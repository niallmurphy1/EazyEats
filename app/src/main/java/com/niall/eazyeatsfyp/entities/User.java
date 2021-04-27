package com.niall.eazyeatsfyp.entities;

import java.util.ArrayList;
import java.util.List;

public class User {

    private String uId;
    private String username;
    private String email;
    private String password;
    private ShoppingListItem sList = new ShoppingListItem();
    private List<Recipe> favRecipes = new ArrayList<Recipe>();
    private List<String> allergens = new ArrayList<>();
    private List<Food> inventory = new ArrayList<>();



    public User(){

    }


    public User(String username, String email, ShoppingListItem sList, List<Recipe> favRecipes, List<String> allergens, List<Food> inventory) {

        this.username = username;
        this.email = email;
        this.sList = sList;
        this.favRecipes = favRecipes;
        this.allergens = allergens;
        this.inventory = inventory;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    public ShoppingListItem getsList() {
        return sList;
    }

    public void setsList(ShoppingListItem sList) {
        this.sList = sList;
    }

    public List<Recipe> getFavRecipes() {
        return favRecipes;
    }

    public void setFavRecipes(List<Recipe> favRecipes) {
        this.favRecipes = favRecipes;
    }


    public void addRecipe(Recipe recipe) {
        favRecipes.add(recipe);
    }

    public List<String> getAllergens() {
        return allergens;
    }

    public void setAllergens(List<String> allergens) {
        this.allergens = allergens;
    }

    public List<Food> getInventory() {
        return inventory;
    }

    public void setInventory(List<Food> inventory) {
        this.inventory = inventory;
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }
}
