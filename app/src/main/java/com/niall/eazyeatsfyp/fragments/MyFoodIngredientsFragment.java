package com.niall.eazyeatsfyp.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.niall.eazyeatsfyp.R;
import com.niall.eazyeatsfyp.RecipeFromIngredientsActivity;
import com.niall.eazyeatsfyp.adapters.IngredientCardAdapter;
import com.niall.eazyeatsfyp.entities.Food;
import com.niall.eazyeatsfyp.entities.Recipe;
import com.niall.eazyeatsfyp.entities.ShoppingListItem;
import com.niall.eazyeatsfyp.util.DuplicateChecker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.niall.eazyeatsfyp.util.Constants.RECIPE_SEARCH;
import static com.niall.eazyeatsfyp.util.Constants.SP_APIKEY;


public class MyFoodIngredientsFragment extends Fragment implements IngredientCardAdapter.ViewHolder.OnIngredientListener {


    private FloatingActionButton fabAddItem;

    private ArrayList<String> ingredientNames;

    public FirebaseAuth fAuth = FirebaseAuth.getInstance();
    public FirebaseUser fUser = fAuth.getCurrentUser();
    final String userId = fUser.getUid();
    private DatabaseReference userIngredientsRef;

    private DatabaseReference allItemsRef;


    public Map newFoodMap = new HashMap();

    public RecyclerView ingredientRecycler;
    IngredientCardAdapter adapter = new IngredientCardAdapter();
    public ArrayList<Food> foodIngredients = new ArrayList<>();

    private Toolbar itemsSeelectedTolbar;
    private ImageButton itemsSelectedBackBtn;

    private Intent recipeFromIngredientsIntent;
    public static final String INGREDIENTSQUERY = "ingredientsQuery";


    private TextView emptyIngredientsText;

    public MyFoodIngredientsFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        allItemsRef = FirebaseDatabase.getInstance().getReference("Recipe");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        userIngredientsRef = FirebaseDatabase.getInstance().getReference("User").child(userId).child("user-inventory");

        return inflater.inflate(R.layout.fragment_my_food_ingredients, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        getRecipeIngredientsFromFirebase(allItemsRef);

        fabAddItem = view.findViewById(R.id.fab_add_to_inventory);

        emptyIngredientsText = view.findViewById(R.id.empty_ingredients_text_view);

        itemsSeelectedTolbar = view.findViewById(R.id.my_ingredients_toolbar);
        itemsSeelectedTolbar.setVisibility(View.GONE);
        itemsSelectedBackBtn = view.findViewById(R.id.my_ing_toolbar_back);
        itemsSelectedBackBtn.setVisibility(View.GONE);

        getUserInventoryFromFirebase();

        fabAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialog();
            }
        });


        ingredientRecycler = view.findViewById(R.id.my_ingredients_rcv);
        ingredientRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new IngredientCardAdapter(getContext(), foodIngredients, this, this);
        ingredientRecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }


    public void getRecipeIngredientsFromFirebase(DatabaseReference recipeRef) {

        ArrayList<Recipe> allRecipes = new ArrayList<>();
        allRecipes.clear();
        ArrayList<String> recipeKeys = new ArrayList<>();
        recipeKeys.clear();

        recipeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                for (DataSnapshot keyNode : snapshot.getChildren()) {

                    String key = keyNode.getKey();

                    keyNode.child(key).child("ingredients").getChildren();

                    Recipe recipe = keyNode.getValue(Recipe.class);
                    allRecipes.add(recipe);
                    recipeKeys.add(key);

                }

                ingredientNames = getIngredientNames(allRecipes);
                Log.d(TAG, "onDataChange: ingredients without duplicates: " + getIngredientNames(allRecipes));

                Log.d(TAG, "onDataChange: ingredients without duplicates size: " + getIngredientNames(allRecipes).size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public ArrayList<String> getIngredientNames(ArrayList<Recipe> recipes) {

        ArrayList<String> ingredientNames = new ArrayList<>();

        ingredientNames.clear();

        for (Recipe recipe : recipes) {
            for (Food food : recipe.getIngredients())
                ingredientNames.add(food.getName());
        }

        Log.d(TAG, "getIngredientNames: ingredients with duplicates: " + ingredientNames);
        Log.d(TAG, "getIngredientNames: ingredients with duplicates size: " + ingredientNames.size());
        return DuplicateChecker.getRidOfDuplicates(ingredientNames);

    }


    public void getUserInventoryFromFirebase() {

        Log.d(TAG, "getUserInv: method started");
        userIngredientsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                foodIngredients.clear();

                for (DataSnapshot keyNode : snapshot.getChildren()) {
                    Log.d(TAG, "onDataChange: Here we go");
                    Food food = keyNode.getValue(Food.class);
                    food.setFoodId(keyNode.getKey());
                    foodIngredients.add(food);

                }


                System.out.println("Food items:" + foodIngredients.toString());

                adapter.setIngredients(foodIngredients);

                adapter.notifyDataSetChanged();


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.d(TAG, "onCancelled: error getUserInvFromFirebase: " + error);
            }
        });

    }

    public String addQuants(String previousQuant, String newQuant) {


        return String.valueOf(Integer.valueOf(previousQuant) + Integer.valueOf(newQuant));


    }

    public void showDialog() {


        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_new_ingredient, null);
        builder.setView(dialogView);
        builder.setTitle("New Ingredient");
        AutoCompleteTextView dialogIngredientName = dialogView.findViewById(R.id.dialog_new_ingredient_name);
        String[] ingredientSuggestions = ingredientNames.toArray(new String[0]);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, ingredientSuggestions);
        dialogIngredientName.setAdapter(arrayAdapter);
        EditText dialogIngredientQuant = dialogView.findViewById(R.id.dialog_ingredient_quant);
        Spinner spinner = dialogView.findViewById(R.id.dialog_new_ingredient_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item
                , getResources().getStringArray(R.array.units));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        builder.setPositiveButton("Add", (dialog, which) -> {

            if (!spinner.getSelectedItem().toString().equalsIgnoreCase("Choose a Unit???")) {


                Food food = new Food(dialogIngredientName.getText().toString(), dialogIngredientQuant.getText().toString(), spinner.getSelectedItem().toString());

                Log.d(TAG, "showDialog: new food toString() " + food.toString());
                for (Food aFood : foodIngredients) {

                    if (aFood.getName().equalsIgnoreCase(food.getName()) && aFood.getUnit().equalsIgnoreCase(food.getUnit())) {

                        food.setQuantity(addQuants(food.getQuantity(), aFood.getQuantity()));

                        Snackbar.make(getView(), "Food quantity added to ingredients!", Snackbar.LENGTH_SHORT).show();
                    }
                }

                AndroidNetworking.get("https://api.spoonacular.com/food/ingredients/search?query=" + food.getName()
                        + "&apiKey=c029b15f6c654e36beba722a71295883&metaInformation=true")
                        .build().getAsString(new StringRequestListener() {
                    @Override
                    public void onResponse(String response) {

                        Log.d(TAG, "onResponse: " + response);

                        JSONObject results;
                        try {

                            Log.d(TAG, "onResponse: " + response);
                            results = new JSONObject(response);
                            JSONArray arr = results.getJSONArray("results");

                            String id = arr.getJSONObject(0).getString("id");

                            String name = arr.getJSONObject(0).getString("name");

                            food.setFoodId(id);

                            Log.d(TAG, "Food  : " + food.toString());


                            Map<String, Object> foodMap = food.toMap();
                            newFoodMap.put(food.getFoodId(), foodMap);
                            userIngredientsRef.updateChildren(newFoodMap).addOnSuccessListener(new OnSuccessListener() {
                                @Override
                                public void onSuccess(Object o) {
                                    Log.d(TAG, "onSuccess: Food added to database: " + food.toString());
                                }
                            });

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.d(TAG, "onError:" + anError);
                        Snackbar.make(getView(), "Ingredient" + food.getName() + " not found in database, please try again", Snackbar.LENGTH_SHORT);

                    }
                });

            } else {
                Snackbar.make(getView(), " Please enter a unit", Snackbar.LENGTH_LONG).show();
            }

        });
        AlertDialog alertDialog = builder.create();

        alertDialog.show();


    }

    public void showMeTheMoney(ArrayList<Food> foodIngredients) {


        ArrayList<String> ingredientNames = new ArrayList<>();
        StringBuilder query = new StringBuilder();

        if (foodIngredients.isEmpty())
            Snackbar.make(getView(), "You need to select ingredients to create a recipe", Snackbar.LENGTH_SHORT).show();

        else {
            for (Food food : foodIngredients) {
                ingredientNames.add(food.getName());


                query.append(food.getName()).append(",");


            }
            query.deleteCharAt(query.length() - 1);
            String ingredientQuery = "&includeIngredients=" + query + "&sort=random&addRecipeInformation=true&fillIngredients=true";

            recipeFromIngredientsIntent = new Intent(getContext(), RecipeFromIngredientsActivity.class);
            recipeFromIngredientsIntent.putExtra(INGREDIENTSQUERY, ingredientQuery);

            startActivity(recipeFromIngredientsIntent);


            Log.d(TAG, "showMeTheMoney: These are the ingredients comma sep: " + query);

        }

    }

    @Override
    public void onIngredientClick(int position) {

    }

    public void deleteIngredientsFromFirebase(ArrayList<Food> selectedItemsDelete) {


        Log.d(TAG, "deleteIngredientsfromFirebase: selected items: " + selectedItemsDelete.toString());

        Log.d(TAG, "deleteIngredientsfromFirebase: method started");

     for(Food deletedFood: selectedItemsDelete) {

         userIngredientsRef.child(deletedFood.getFoodId()).removeValue();

         Log.d(TAG, "deleteIngredientsFromFirebase: Food removed: " + deletedFood.toString());
     }



                for (Food food : selectedItemsDelete) {
                    foodIngredients.remove(food);

                }
                adapter.setSelectedItems(new ArrayList<Food>());
                adapter.notifyDataSetChanged();


            }




}