package com.niall.eazyeatsfyp.entities;

import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Recipe {

    private String name;
    private ArrayList<String> dishType;
    private ArrayList<Food> ingredients;
    private int time;
    private int servings;
    private ArrayList<String> cuisines;
    private String imageURI;
    private String recipeID;
    private String method;

    public Recipe(){

    }

    public Recipe(String name, ArrayList<String> dishType, ArrayList<Food> ingredients, int time, int servings, String imageURI, String recipeID, ArrayList<String> cuisines) {
        this.name = name;
        this.dishType = dishType;
        this.ingredients = ingredients;
        this.time = time;
        this.servings = servings;
        this.imageURI = imageURI;
        this.recipeID = recipeID;
        this.cuisines = cuisines;
    }

    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("name", name);
        result.put("time", time);
        result.put("ingredients", ingredients);
        result.put("dishType", dishType);
        result.put("imageURI", imageURI);
        result.put("servings", servings);
        result.put("recipeID", recipeID);
        result.put("cuisines", cuisines);
        result.put("method", method);

        return result;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getDishType() {
        return dishType;
    }

    public void setDishType(ArrayList<String> dishType) {
        this.dishType = dishType;
    }

    public ArrayList<Food> getIngredients() {
        return ingredients;
    }

    public void setIngredients(ArrayList<Food> ingredients) {
        this.ingredients = ingredients;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getServings() {
        return servings;
    }

    public void setServings(int servings) {
        this.servings = servings;
    }

    public String getImageURI() {
        return imageURI;
    }

    public void setImageURI(String imageURI) {
        this.imageURI = imageURI;
    }

    public String getRecipeID() {
        return recipeID;
    }

    public void setRecipeID(String recipeID) {
        this.recipeID = recipeID;
    }

    public ArrayList<String> getCuisines() {
        return cuisines;
    }

    public void setCuisines(ArrayList<String> cuisines) {
        this.cuisines = cuisines;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }


    @Override
    public String toString() {
        return "Recipe{" +
                "name='" + name + '\'' +
                ", dishType=" + dishType +
                ", ingredients=" + ingredients +
                ", time=" + time +
                ", servings=" + servings +
                ", cuisines=" + cuisines +
                ", imageURI='" + imageURI + '\'' +
                ", recipeID='" + recipeID + '\'' +
                ", method='" + method + '\'' +
                '}';
    }
}




