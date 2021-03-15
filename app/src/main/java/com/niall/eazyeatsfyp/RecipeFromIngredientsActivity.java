package com.niall.eazyeatsfyp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.niall.eazyeatsfyp.adapters.RecipeCardAdapter;
import com.niall.eazyeatsfyp.entities.Food;
import com.niall.eazyeatsfyp.entities.Recipe;
import com.niall.eazyeatsfyp.entities.ShoppingListItem;
import com.niall.eazyeatsfyp.fragments.MyFoodIngredientsFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;
import static com.niall.eazyeatsfyp.util.Constants.RECIPE_SEARCH;
import static com.niall.eazyeatsfyp.util.Constants.SP_APIKEY;

public class RecipeFromIngredientsActivity extends AppCompatActivity implements RecipeCardAdapter.ViewHolder.OnRecipeListener{


    private Intent intent;


    public RecyclerView recipeRecycler;
    RecipeCardAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_from_ingredients);

        intent = getIntent();

        String ingredientQuery = intent.getStringExtra(MyFoodIngredientsFragment.INGREDIENTSQUERY);



        searchByIngredient(ingredientQuery);

    }



    public void searchByIngredient(String ingredientQuery) {

        ArrayList<Recipe> recipes = new ArrayList<>();


        AndroidNetworking.get(RECIPE_SEARCH + SP_APIKEY + ingredientQuery)
                .addQueryParameter("limit", "1")
                .addHeaders("token", "1234")
                .setTag("test")
                .setPriority(Priority.LOW)
                .build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: api works...");

                System.out.println("API RESPONSE: " + response);


                try {
                    JSONObject obj = new JSONObject(response);

                    JSONArray arr = obj.getJSONArray("results");

                    Log.d(TAG, "recipe JSON Array size: " + arr.length());

                    for (int i = 0; i < arr.length(); i++) {


                        //pull title, readyInMinutes, Image URI, servings, ID, dishtypes

                        String image = arr.getJSONObject(i).getString("image");

                        Log.d(TAG, "Recipe ImageURI: " + image);
                        String title = arr.getJSONObject(i).getString("title");
                        int readyInMinutes = arr.getJSONObject(i).getInt("readyInMinutes");
                        int servings = arr.getJSONObject(i).getInt("servings");
                        String recipeID = arr.getJSONObject(i).getString("id");
                        Log.d(TAG, "Recipe ID: " + recipeID);


                        // TODO: fix getIngredients... and this whole thing
                        JSONArray extIngredients = arr.getJSONObject(i).getJSONArray("extendedIngredients");

                        ArrayList<Food> ingredients = new ArrayList<>();
                        ArrayList<ShoppingListItem> shoppingListItems = new ArrayList<>();

                        for (int f = 0; f < extIngredients.length(); f++) {


                            String ingredientName = extIngredients.getJSONObject(f).getString("name");

                            String categories = extIngredients.getJSONObject(f).getString("aisle");

                            String foodId = extIngredients.getJSONObject(f).getString("id");

                            ShoppingListItem shoppingListItem = new ShoppingListItem(foodId, ingredientName, categories);

                            String unit = extIngredients.getJSONObject(f).getString("unit");

                            String quant = extIngredients.getJSONObject(f).getString("amount");

                            Food food = new Food(ingredientName, quant, unit);

                            food.setFoodId(foodId);
                            ingredients.add(food);

                            Log.d(TAG, "onResponse: Number of ingredients: " + ingredients.size());

                            System.out.println("Ingredient #" + f + ": " + ingredientName);


                            shoppingListItems.add(shoppingListItem);


                        }


                        JSONArray jArray = arr.getJSONObject(i).getJSONArray("dishTypes");
                        ArrayList<String> dishTypes = new ArrayList<>();

                        for (int x = 0; x < jArray.length(); x++) {
                            dishTypes.add(jArray.get(x).toString());
                        }

                        JSONArray cuisinesArray = arr.getJSONObject(i).getJSONArray("cuisines");
                        ArrayList<String> cuisines = new ArrayList<>();


                        for (int x = 0; x < cuisinesArray.length(); x++) {
                            cuisines.add(cuisinesArray.get(x).toString());
                        }


                        Recipe recipe = new Recipe(title, dishTypes, ingredients, readyInMinutes, servings, image, recipeID, cuisines);

                        recipes.add(recipe);

                    }



                    Log.d(TAG, "onResponse: gottem " + recipes.toString());


                   // setUpRCV();

                    recipeRecycler = findViewById(R.id.recipe_from_ingredients_rcv);
                    recipeRecycler.setLayoutManager(new LinearLayoutManager(RecipeFromIngredientsActivity.this));
                    adapter = new RecipeCardAdapter(RecipeFromIngredientsActivity.this, recipes, RecipeFromIngredientsActivity.this);
                    recipeRecycler.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onError(ANError anError) {

                System.out.println("Error " + anError);
            }
        });


    }

    @Override
    public void onRecipeClick(int position) {
        //TODO: open recipe viewer activity

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }
}