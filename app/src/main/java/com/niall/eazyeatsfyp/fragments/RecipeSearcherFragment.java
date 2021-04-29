package com.niall.eazyeatsfyp.fragments;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.SearchView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.niall.eazyeatsfyp.R;
import com.niall.eazyeatsfyp.RecipeViewerActivity;
import com.niall.eazyeatsfyp.adapters.RecipeCardAdapter;
import com.niall.eazyeatsfyp.entities.Food;
import com.niall.eazyeatsfyp.entities.Recipe;
import com.niall.eazyeatsfyp.entities.ShoppingListItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static com.niall.eazyeatsfyp.util.Constants.RECIPE_SEARCH;
import static com.niall.eazyeatsfyp.util.Constants.SP_APIKEY;

public class RecipeSearcherFragment extends Fragment implements RecipeCardAdapter.ViewHolder.OnRecipeListener{


    RecyclerView recipeRecycler;

    RecyclerView.Adapter recipeAdapter;
    ArrayList<Recipe> recipes = new ArrayList<>();
    ArrayList<Food> ingredients = new ArrayList<>();
    ArrayList<ShoppingListItem> shoppingListItems = new ArrayList<>();

    private ArrayList<String> recipeNames;


    private DatabaseReference recipeRef;
    private SearchView searchView;


    private String[] recipeNamesArray;

    Intent recipeViewerIntent;
    public static final String RECIPE_ID = "recipeID";
    public static final String RECIPE_IMAGEURI = "recipeImage";


    public RecipeSearcherFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recipeRef = FirebaseDatabase.getInstance().getReference("Recipe");

        getRecipesFromFirebase();
    }

    private void getRecipesFromFirebase() {

        recipeNames = new ArrayList<>();


        recipeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for(DataSnapshot keyNode: snapshot.getChildren()){

                    String key = keyNode.getKey();

                    String recipeName = keyNode.child(key).child("Name:").getValue(String.class);

                    recipeNames.add(recipeName);

                }
                 recipeNamesArray = recipeNames.toArray(new String[0]);


                //autoCompleteTextView = getView().findViewById(R.id.autocomplete_text_view_searc_recipes);
//
//                ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, recipeNamesArray);
//                autoCompleteTextView.setAdapter(arrayAdapter);

              //  setUpTextView();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.e(RecipeSearcherFragment.class.getSimpleName(), "onCancelled: error: " + error );
            }
        });
    }

//    public void setUpTextView(){
//        autoCompleteTextView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//
//                recipes.clear();
//
//
//                AndroidNetworking.get(RECIPE_SEARCH + SP_APIKEY + "&query=" + v.getText().toString().trim() + "&addRecipeInformation=true&fillIngredients=true")
//
//                            .build().getAsString(new StringRequestListener() {
//
//                        @Override
//                        public void onResponse(String response) {
//                            Log.w("API", response);
//                            String jsonString = response;
//                            try {
//                                JSONObject obj = new JSONObject(jsonString);
//                                JSONArray arr = obj.getJSONArray("results");
//
//
//                                for (int i = 0; i < arr.length(); i++) {
//                                    //get recipe names
//                                    String name = arr.getJSONObject(i).getString("title");
//
//                                    //get recipe dish types
//                                    JSONArray jArray = arr.getJSONObject(i).getJSONArray("dishTypes");
//                                    ArrayList<String> dishTypes = new ArrayList<>();
//
//                                    for (int x = 0; x < jArray.length(); x++) {
//                                        dishTypes.add(jArray.get(x).toString());
//                                    }
//
//                                    JSONArray cuisinesArray = arr.getJSONObject(i).getJSONArray("cuisines");
//                                    ArrayList<String> cuisines = new ArrayList<>();
//
//                                    for (int x = 0; x < cuisinesArray.length(); x++) {
//                                        cuisines.add(cuisinesArray.get(x).toString());
//                                    }
//
//
//                                    //get cuisines
//
//
//                                    String id = arr.getJSONObject(i).getString("id");
//
//
//                                    String recipeImage = arr.getJSONObject(i).getString("image");
//
//
//                                    //get recipe times
//                                    int time = arr.getJSONObject(i).getInt("readyInMinutes");
//
//                                    //get recipe servings
//                                    int servings = arr.getJSONObject(i).getInt("servings");
//
//
//                                    JSONArray extIngredients = arr.getJSONObject(i).getJSONArray("extendedIngredients");
//
//                                    for (int f = 0; f < extIngredients.length(); f++) {
//
//                                        String ingredientName = extIngredients.getJSONObject(i).getString("name");
//
//                                        String categories = extIngredients.getJSONObject(i).getString("aisle");
//
//                                        String foodId = extIngredients.getJSONObject(i).getString("id");
//
//                                        ShoppingListItem shoppingListItem = new ShoppingListItem(foodId, ingredientName, categories);
//
//                                        String unit = extIngredients.getJSONObject(i).getString("unit");
//
//                                        String quant = extIngredients.getJSONObject(i).getString("amount");
//
//                                        Food food = new Food(ingredientName, quant, unit);
//
//                                        food.setFoodId(foodId);
//                                        ingredients.add(food);
//
//                                        System.out.println("Ingredient #" + i + ": " + ingredientName);
//
//
//                                        shoppingListItems.add(shoppingListItem);
//
//
//                                    }
//
//                                    Recipe recipe = new Recipe(name, dishTypes, ingredients, time, servings, recipeImage, id, cuisines);
//
//
//                                    recipes.add(recipe);
//
//                                    System.out.println(recipe.toString());
//
//                                    setUpRCV();
//
//                                }
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//
//                        @Override
//                        public void onError(ANError anError) {
//                            Log.w("API", anError);
//
//                        }
//                    });
//
//
//                }
//                return true;
//            }
//        });
//
//
//    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipe_searcher, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        searchView = view.findViewById(R.id.searchBar);



        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                recipes.clear();


                AndroidNetworking.get(RECIPE_SEARCH + SP_APIKEY + "&query=" + query + "&addRecipeInformation=true&fillIngredients=true")

                        .build().getAsString(new StringRequestListener() {

                    @Override
                    public void onResponse(String response) {
                        Log.w("API", response);
                        String jsonString = response;
                        try {
                            JSONObject obj = new JSONObject(jsonString);
                            JSONArray arr = obj.getJSONArray("results");


                            for (int i = 0; i < arr.length(); i++) {
                                //get recipe names
                                String name = arr.getJSONObject(i).getString("title");

                                //get recipe dish types
                                JSONArray jArray = arr.getJSONObject(i).getJSONArray("dishTypes");
                                ArrayList<String> dishTypes = new ArrayList<>();

                                for(int x = 0; x < jArray.length(); x++){
                                    dishTypes.add(jArray.get(x).toString());
                                }

                                JSONArray cuisinesArray = arr.getJSONObject(i).getJSONArray("cuisines");
                                ArrayList<String> cuisines = new ArrayList<>();

                                for(int x = 0; x < cuisinesArray.length(); x++){
                                    cuisines.add(cuisinesArray.get(x).toString());
                                }



                                //get cuisines


                                String id = arr.getJSONObject(i).getString("id");





                                String recipeImage = arr.getJSONObject(i).getString("image");


                                //get recipe times
                                int time = arr.getJSONObject(i).getInt("readyInMinutes");

                                //get recipe servings
                                int servings = arr.getJSONObject(i).getInt("servings");


                                JSONArray extIngredients = arr.getJSONObject(i).getJSONArray("extendedIngredients");

                                for(int f = 0; f < extIngredients.length(); f++){

                                    String ingredientName = extIngredients.getJSONObject(i).getString("name");

                                    String categories = extIngredients.getJSONObject(i).getString("aisle");

                                    String foodId = extIngredients.getJSONObject(i).getString("id");

                                    ShoppingListItem shoppingListItem = new ShoppingListItem(foodId, ingredientName, categories);

                                    String unit = extIngredients.getJSONObject(i).getString("unit");

                                    String quant = extIngredients.getJSONObject(i).getString("amount");

                                    Food food = new Food(ingredientName, quant, unit);

                                    food.setFoodId(foodId);
                                    ingredients.add(food);

                                    System.out.println("Ingredient #" + i + ": " + ingredientName);


                                    shoppingListItems.add(shoppingListItem);


                                }

                                Recipe recipe = new Recipe(name, dishTypes,ingredients, time, servings,recipeImage, id, cuisines);



                                recipes.add(recipe);

                                System.out.println(recipe.toString());

                                setUpRCV();

                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.w("API", anError);

                    }
                });

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });



    }

    @Override
    public void onRecipeClick(int position) {
        Log.d("ONRECIPE", "Clicked! at position:" + position);

        System.out.println(recipes.get(position).toString() + "\nRecipe ID: " + recipes.get(position).getRecipeID());

        recipeViewerIntent.putExtra("WhereFrom", "RECIPEFROMSEARCH");

        recipeViewerIntent.putExtra(RECIPE_ID, recipes.get(position).getRecipeID());

        recipeViewerIntent.putExtra(RECIPE_IMAGEURI, recipes.get(position).getImageURI());


        startActivity(recipeViewerIntent);
    }

    public void setUpRCV(){
        recipeRecycler = getView().findViewById(R.id.recipe_recyclerView);
        recipeRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        recipeAdapter = new RecipeCardAdapter(getActivity(), recipes, this);
        recipeRecycler.setAdapter(recipeAdapter);

        recipeAdapter.notifyDataSetChanged();

        recipeViewerIntent = new Intent(getContext(), RecipeViewerActivity.class);
    }
}