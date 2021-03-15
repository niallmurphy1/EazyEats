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
import com.niall.eazyeatsfyp.adapters.ShoppingListItemAdapter;
import com.niall.eazyeatsfyp.entities.Food;
import com.niall.eazyeatsfyp.entities.Recipe;
import com.niall.eazyeatsfyp.entities.ShoppingListItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;
import static com.niall.eazyeatsfyp.util.Constants.RECIPE_SEARCH;
import static com.niall.eazyeatsfyp.util.Constants.SP_APIKEY;


public class MyFoodIngredientsFragment extends Fragment implements IngredientCardAdapter.ViewHolder.OnIngredientListener{


    private FloatingActionButton fabAddItem;

    public FirebaseAuth fAuth = FirebaseAuth.getInstance();
    public FirebaseUser fUser = fAuth.getCurrentUser();
    final String userId = fUser.getUid();
    private DatabaseReference userIngredientsRef;


    public Map newFoodMap = new HashMap();

    public RecyclerView ingredientRecycler;
    IngredientCardAdapter adapter = new IngredientCardAdapter();
    public ArrayList<Food> foodIngredients = new ArrayList<>();

    private Toolbar itemsSeelectedTolbar;
    private ImageButton itemsSelectedBackBtn;

    private Intent recipeFromIngredientsIntent;
    public static final String INGREDIENTSQUERY = "ingredientsQuery";


    public boolean isActionMode = false;

    Food food;


    private TextView emptyIngredientsText;

    public MyFoodIngredientsFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

    public void getUserInventoryFromFirebase(){

        Log.d(TAG, "getUserInv: method started" );
        userIngredientsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                foodIngredients.clear();

                for(DataSnapshot keyNode: snapshot.getChildren()){
                    Log.d(TAG, "onDataChange: Here we go");
                    Food food = keyNode.getValue(Food.class);
                    food.setFoodId(keyNode.getKey());
                    foodIngredients.add(food);

                }


                System.out.println("Food items:"+foodIngredients.toString());

                adapter.setIngredients(foodIngredients);

                adapter.notifyDataSetChanged();



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public String addQuants(String previousQuant, String newQuant){


       return String.valueOf( Integer.valueOf(previousQuant) +Integer.valueOf(newQuant));


    }

    public void showDialog(){


        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_new_ingredient,null);
        builder.setView(dialogView);
        builder.setTitle("New Ingredient");
        EditText dialogIngredientName = dialogView.findViewById(R.id.dialog_new_ingredient_name);
        EditText dialogIngredientQuant = dialogView.findViewById(R.id.dialog_ingredient_quant);
        Spinner spinner = dialogView.findViewById(R.id.dialog_new_ingredient_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item
                , getResources().getStringArray(R.array.units));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(!spinner.getSelectedItem().toString().equalsIgnoreCase("Choose a Unit…")){


                    Food food = new Food(dialogIngredientName.getText().toString(), dialogIngredientQuant.getText().toString(), spinner.getSelectedItem().toString());

                    for(Food aFood : foodIngredients){

                        if(aFood.getName().equalsIgnoreCase(food.getName()) && aFood.getUnit().equalsIgnoreCase(food.getUnit())){

                           food.setQuantity(addQuants(food.getQuantity(), aFood.getQuantity()));
                        }
                    }



                    //Log.d(TAG, "onClick: Food item created: " + food.toString());

                    AndroidNetworking.get("https://api.spoonacular.com/food/ingredients/search?query="+food.getName()
                            +"&apiKey=c029b15f6c654e36beba722a71295883&metaInformation=true&number=1")
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
                            Log.d(TAG, "onError:" +  anError);
                        }
                    });


                }


            }
        });
        AlertDialog alertDialog = builder.create();

        alertDialog.show();



    }

    public void showMeTheMoney(ArrayList<Food> foodIngredients){


        ArrayList<String> ingredientNames = new ArrayList<>();
        StringBuilder query = new StringBuilder();

        if(foodIngredients.isEmpty())
            Toast.makeText(getContext(), "You need to select ingredients to create a recipe", Toast.LENGTH_SHORT).show();

        else        {
            Toast.makeText(getContext(), "This is the money: " +  foodIngredients.toString(), Toast.LENGTH_SHORT).show();

            for(Food food: foodIngredients) {
                ingredientNames.add(food.getName());


                query.append(food.getName()).append(",");


            }

            query.deleteCharAt(query.length()-1);



            //searchByIngredient("&includeIngredients=" + query + "&sort=random&fillIngredients=true&number=10");


            String ingredientQuery = "&includeIngredients=" + query + "&sort=random&fillIngredients=true&number=10";


            recipeFromIngredientsIntent = new Intent(getContext(), RecipeFromIngredientsActivity.class);
            recipeFromIngredientsIntent.putExtra(INGREDIENTSQUERY, ingredientQuery);

            startActivity(recipeFromIngredientsIntent);



            Log.d(TAG, "showMeTheMoney: These are the ingredients comma sep: " + query);

        }

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
    public void onIngredientClick(int position) {

    }

    public void deleteIngredientsfromFirebase(ArrayList<Food> selectedItemsDelete) {


        Log.d(TAG, "deleteIngredientsfromFirebase: selected items: " + selectedItemsDelete.toString());


        // TODO: find out why selectedItemsDelete gets cleared in here

            Log.d(TAG, "deleteIngredientsfromFirebase: method started");
            userIngredientsRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot keyNode : snapshot.getChildren()) {

                        Log.d(TAG, "onDataChange: Here we go");
                        Food food = keyNode.getValue(Food.class);

                        Log.d(TAG, "onDataChanging: food name " + food.getName());


                        Log.d(TAG, "onDataChanging: selected item food name " + selectedItemsDelete.toString());


                        for(Food foo: selectedItemsDelete){
                            if(food.getName().equalsIgnoreCase(foo.getName())){
                                keyNode.getRef().removeValue();

                                Log.d(TAG, "onDataChange: Great success ");

                            }

                        }


                    }

                    for(Food food : selectedItemsDelete){
                        foodIngredients.remove(food);
                        adapter.notifyDataSetChanged();
                    }

                    Toast.makeText(getActivity(),  selectedItemsDelete.size() + " items deleted successfully", Toast.LENGTH_SHORT).show();


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


    }
}