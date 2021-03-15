package com.niall.eazyeatsfyp.util;

import java.util.ArrayList;

public class Constants {

    public static final String BASE_URL = "https://api.spoonacular.com/";

    public static final String SP_APIKEY = "apiKey=c029b15f6c654e36beba722a71295883";

    public static final String RECIPE_SEARCH = "https://api.spoonacular.com/recipes/complexSearch?addRecipeInformation=true&";
    public static final String FOOD_SEARCH = "food/ingredients/search?";

    public static final String RECIPE_ID_SEARCH = "https://api.spoonacular.com/recipes/";

    public static final String RANDOM_RECIPESEARCH = "https://api.spoonacular.com/recipes/" +
            "random?apiKey=c029b15f6c654e36beba722a71295883&number=5"; //change to 5 recipes for testing, as API runs out of calls

    public static final String BARCODE_SEARCH ="https://api.spoonacular.com/food/products/upc/";

    public static final String ZINC_KEY = "88DD74AB4C41EACFB9354D8A";

    public static final String AMAZON_PRODUCT_SEARCH = "https://api.zinc.io/v1/search?query=";
    public static final String AMAZON_PRODUCT_SEARCH2 = "&page=1&retailer=amazon_uk";


    public static final String ZINC_AUTH = okhttp3.Credentials.basic("88DD74AB4C41EACFB9354D8A", "");

    public static final String[] cuisines = {"african", "american", "british"};
}
