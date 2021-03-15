package com.niall.eazyeatsfyp;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.niall.eazyeatsfyp.adapters.IngredientCardAdapter;
import com.niall.eazyeatsfyp.adapters.MyIngredientsAdapter;
import com.niall.eazyeatsfyp.animations.ViewAnimation;
import com.niall.eazyeatsfyp.databinding.ActivityRecipeViewerNewBindingImpl;
import com.niall.eazyeatsfyp.entities.Food;
import com.niall.eazyeatsfyp.entities.Recipe;
import com.niall.eazyeatsfyp.entities.ShoppingListItem;
import com.niall.eazyeatsfyp.fragments.MyFoodIngredientsFragment;
import com.niall.eazyeatsfyp.fragments.RecipeSearcherFragment;
import com.squareup.picasso.Picasso;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RecipeViewerActivity extends AppCompatActivity implements MyIngredientsAdapter.ViewHolder.OnMyIngredientListener{


    ActivityRecipeViewerNewBindingImpl binding;
   // ActivityRecipeViewerBinding binding;
    private boolean isRotate = false;

    private ImageView recipeImage;
    private TextView recipeInstructions;
    private TextView recipeTimeTextView;
    private TextView servingsTextView;

    private int origServings;

    private ArrayList<Double> initialQuants = new ArrayList<>();

    private CollapsingToolbarLayout collapsingToolbarLayout;

    private Recipe recipe;

    public DatabaseReference dataRef;
    public FirebaseAuth fAuth = FirebaseAuth.getInstance();
    public FirebaseUser fUser = fAuth.getCurrentUser();
    final String userId = fUser.getUid();

    public RecyclerView ingredientsRecycler;
    MyIngredientsAdapter adapter = new MyIngredientsAdapter();
    ArrayList<Food> ingredients = new ArrayList<>();

    private Button changeServingsBtn;
    private NumberPicker numberPicker;

    public ArrayList<ShoppingListItem> shoppingListItems = new ArrayList<>();

    public Map newUserRecipe = new HashMap();

    public Map newShopListItem = new HashMap();

    private ArrayList<String> foodIds = new ArrayList<>();






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_viewer_new);

        collapsingToolbarLayout = findViewById(R.id.collapsing_toolbar_layout_new);

        changeServingsBtn = findViewById(R.id.change_servings_button);

        binding = DataBindingUtil.setContentView(this, R.layout.activity_recipe_viewer_new);

        recipeTimeTextView = findViewById(R.id.ready_in_mins_recipe_viewer);
        recipeInstructions = findViewById(R.id.recipe_instructions);
        servingsTextView = findViewById(R.id.recipe_viewer_servings_text);


        ViewAnimation.init(binding.fabHeart);
        ViewAnimation.init(binding.fabCart);

        binding.fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRotate = ViewAnimation.rotateFab(v, !isRotate);
                if(isRotate){
                    ViewAnimation.showIn(binding.fabHeart);
                    ViewAnimation.showIn(binding.fabCart);
                }else{
                    ViewAnimation.showOut(binding.fabHeart);
                    ViewAnimation.showOut(binding.fabCart);
                }

            }
        });

        Intent intent = getIntent();

        final String recipeID = intent.getStringExtra(RecipeSearcherFragment.RECIPE_ID);
        String recipeImageURI = intent.getStringExtra(RecipeSearcherFragment.RECIPE_IMAGEURI);

        System.out.println("Recipe ID:" + recipeID);
        recipeImage = findViewById(R.id.recipe_viewer_image_new);
        Picasso.get()
                .load(recipeImageURI)
                .fit()
                .centerCrop()
                .into(recipeImage);


        AndroidNetworking.get("https://api.spoonacular.com/recipes/" + recipeID + "/information?" + "apiKey=c029b15f6c654e36beba722a71295883")
                .addPathParameter("pageNumber", "0")
                .addQueryParameter("limit", "1")
                .addHeaders("token", "1234")
                .setTag("test")
                .setPriority(Priority.LOW)
                .build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {

                String jsonString = response;


                Log.d("API", "onResponse: API responding ");

                try {
                    JSONObject obj = new JSONObject(jsonString);


                    JSONArray jArray = obj.getJSONArray("extendedIngredients");

                    for(int i=0; i < jArray.length(); i++){
                       String ingredientName = jArray.getJSONObject(i).getString("name");

                       String categories = jArray.getJSONObject(i).getString("aisle");

                       String foodId = jArray.getJSONObject(i).getString("id");

                       ShoppingListItem shoppingListItem = new ShoppingListItem(foodId, ingredientName, categories);

                       String unit = jArray.getJSONObject(i).getString("unit");

                       String quant = jArray.getJSONObject(i).getString("amount");

                       Double ingredientQuant = jArray.getJSONObject(i).getDouble("amount");

                       initialQuants.add(ingredientQuant);

                       Food food = new Food(ingredientName, quant, unit);



                       food.setFoodId(foodId);
                       ingredients.add(food);

                        System.out.println("Ingredient #" + i + ": " + ingredientName);


                       shoppingListItems.add(shoppingListItem);

                       foodIds.add(foodId);

                    }

                    String instructions = obj.getString("instructions");

                    System.out.println("Recipe instructions: "+ instructions);

                    String parsedInstructions = Html.fromHtml(instructions).toString();

                    System.out.println("Parsed instructions: " + parsedInstructions);


                    if(recipeInstructions == null){
                        recipeInstructions.setText("No method avaialable for this recipe!");
                    }
                    else {
                        recipeInstructions.setText(parsedInstructions);
                    }


                   ArrayList<String> dishTypes = new ArrayList<>();



                   for(int i =0; i< obj.getJSONArray("dishTypes").length(); i++){
                       dishTypes.add(obj.getJSONArray("dishTypes").get(i).toString());
                   }

                    ArrayList<String> cuisines = new ArrayList<>();


                    for(int i =0; i< obj.getJSONArray("cuisines").length(); i++){

                        cuisines.add(obj.getJSONArray("cuisines").get(i).toString());
                    }


                    int servings = obj.getInt("servings");

                    origServings = obj.getInt("servings");


                    servingsTextView.setText("Serves: " + String.valueOf(servings));


                    String rName = obj.getString("title");
                   // collapsingToolbarLayout.setTitleEnabled(false);
                    collapsingToolbarLayout.setTitle(rName);


                    int time = obj.getInt("readyInMinutes");

                    recipeTimeTextView.setText("Time: "+time+ " mins");


                    recipe = new Recipe(rName, dishTypes, ingredients, time, servings, recipeImageURI, recipeID, cuisines);

                    recipe.setMethod(recipeInstructions.getText().toString());


                    setUpRCV();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(ANError anError) {

                Log.w("API:", "NOPE");
            }
        });


    }

    public void onChangeServingsClick(View v){
        Log.d("TAG", "onChangeServingsClick: clicked ");
        changeServings();
    }

    public void changeServings(){



        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_servings,null);
        builder.setView(dialogView);
        builder.setTitle("Change Servings");
        numberPicker = dialogView.findViewById(R.id.change_servings_no_picker);
        numberPicker.setMaxValue(100);
        numberPicker.setMinValue(1);
        numberPicker.setValue(recipe.getServings());

        builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                recipe.setServings(numberPicker.getValue());
                servingsTextView.setText("Serves: " + String.valueOf(numberPicker.getValue()));

                Log.d("TAG", "onClick: new recipe servings from value picker numberPicker.getValue(): " + numberPicker.getValue());

                Log.d("TAG", "onClick: new recipe servings: " + recipe.getServings());

                for(int i = 0; i < ingredients.size(); i++){

                    Log.d("TAG", "onValueChange ingredients and their quants: " + ingredients.get(i).getName() + ": " + ingredients.get(i).getQuantity());
                    Log.d("TAG", "onValueChange: initialQuants arraylist: " + initialQuants.get(i));


                    Log.d("SERVINGSCHECK", "onClick: original servings: " + origServings);
                    Log.d("SERVINGSCHECK", "onClick: number picker value: " + numberPicker.getValue());
                    Log.d("SERVINGSCHECK", "onClick: ingredient : " + ingredients.get(i).getName() + " quant: " + Double.valueOf(ingredients.get(i).getQuantity()));


                    String newQuant = String.valueOf(changeIngredientQuants(origServings, numberPicker.getValue(), initialQuants.get(i)));

                    formatQuants(newQuant);

                    Log.d("TAG", "onClick: new quants after format: " + ingredients.get(i).getName() + " " +  " Qunatity: " + newQuant);

                    ingredients.get(i).setQuantity(newQuant);
                    //ingredients.clear();


                    Log.d("TAG", "onClick: result of setQuant: " + String.valueOf(changeIngredientQuants(initialQuants.get(i), numberPicker.getValue(), Double.valueOf(ingredients.get(i).getQuantity()))));


                    Log.d("TAG", "onClick: the ingredients new quants:  "+ingredients.get(i).getName() + ", Quant: "+ ingredients.get(i).getQuantity());


                    adapter.notifyDataSetChanged();
                }

                recipe.setIngredients(ingredients);
                setUpRCV();
            }
        });

        AlertDialog alertDialog = builder.create();

        alertDialog.show();

    }

    public void formatQuants(String quant){

        double dblQuant = Double.parseDouble(quant);

        int decimalPlaces = 2;

        BigDecimal bd = new BigDecimal(dblQuant);

        bd.setScale(decimalPlaces, BigDecimal.ROUND_HALF_UP);

        dblQuant = bd.doubleValue();

        quant = String.valueOf(dblQuant);

    }

    public double changeIngredientQuants(double oldServe, int newServe, double oldQuant){

       return (newServe / oldServe) * oldQuant;
    }


    public void onFavClick(View View){

        System.out.println("Favourite clicked");
        Log.d("FAVOURITE", "Clicked! ");

        DatabaseReference recipeRef = FirebaseDatabase.getInstance().getReference("Recipe");

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User");

        String key = recipeRef.push().getKey();

        Map<String, Object> recipeValues = recipe.toMap();

        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put(key, recipeValues);
        newUserRecipe.put(key, recipeValues);

        userRef.child(userId).child("user-favRecipes").updateChildren(newUserRecipe).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {
                Log.d("RECIPE FAV", " ADDED!! ");
                Toast.makeText(RecipeViewerActivity.this, recipe.getName() + " added to favourites", Toast.LENGTH_SHORT).show();
            }
        });

        recipeRef.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("RECIPE:", " Successfully added to db");
            }
        });


        System.out.println(recipe.toString());


    }

    public void onCartClick(View view){

        Log.d("CART ", "Clicked! ");

        Toast.makeText(this, recipe.getIngredients().toString(), Toast.LENGTH_SHORT).show();

        System.out.println(foodIds.toString());

        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User");

        Map<String, Object> childUpdates = new HashMap<>();

        for(int i =0; i< shoppingListItems.size(); i++){

            newShopListItem.put(shoppingListItems.get(i).getsId(), shoppingListItems.get(i));

            userRef.child(userId).child("user-shoppinglist").updateChildren(newShopListItem).addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {

                    Toast.makeText(RecipeViewerActivity.this, "Items added!", Toast.LENGTH_SHORT).show();
                }
            });
        }


    }

   public void setUpRCV(){

        ingredientsRecycler = findViewById(R.id.recipe_viewer_ingredients_rcv);
        ingredientsRecycler.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MyIngredientsAdapter(this, ingredients);
        adapter.setmOnMyIngredientListener(this);
        ingredientsRecycler.setAdapter(adapter);
        adapter.notifyDataSetChanged();
   }




    @Override
    public void onIngredientClick(Food ingredient) {
        Toast.makeText(this, ingredient.getName() + " clicked!", Toast.LENGTH_SHORT).show();

        Toast.makeText(this, ingredient.getName()+ " quant: " + ingredient.getQuantity() + ingredient.getUnit(), Toast.LENGTH_SHORT).show();

    }
}