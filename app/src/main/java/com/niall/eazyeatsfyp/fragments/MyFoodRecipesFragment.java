package com.niall.eazyeatsfyp.fragments;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.gms.common.api.internal.ApiKey;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.niall.eazyeatsfyp.R;
import com.niall.eazyeatsfyp.adapters.IngredientCardAdapter;
import com.niall.eazyeatsfyp.adapters.MyIngredientsAdapter;
import com.niall.eazyeatsfyp.adapters.RecipeCardAdapter;
import com.niall.eazyeatsfyp.entities.Food;
import com.niall.eazyeatsfyp.entities.Recipe;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static com.niall.eazyeatsfyp.util.Constants.SP_APIKEY;


public class MyFoodRecipesFragment extends Fragment implements RecipeCardAdapter.ViewHolder.OnRecipeListener, MyIngredientsAdapter.ViewHolder.OnMyIngredientListener{

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER


    public FirebaseAuth fAuth = FirebaseAuth.getInstance();
    public FirebaseUser fUser = fAuth.getCurrentUser();
    final String userId = fUser.getUid();

    public RecyclerView recipeRecycler;
    RecyclerView.Adapter recipeAdapter;
    RecipeCardAdapter adapter;
    ArrayList<Recipe> recipes = new ArrayList<>();
    private DatabaseReference userFavRecipesRef;

    MyIngredientsAdapter ingredientsAdapter;

    //Bottom sheet components
    private ConstraintLayout bottomSheetConstraint;
    private LinearLayout headerLayout;
    private BottomSheetBehavior bottomSheetBehavior;

    public RecyclerView ingredientsRecycler;
    ArrayList<Food> ingredients = new ArrayList<>();

    private ImageView recipeImage;
    private TextView recipeInstructions;
    private TextView recipeTimeTextView;
    private TextView servingsTextView;
    private Button bapsChangeServingsBtn;

    private ArrayList<Integer> origServings = new ArrayList<>();

    private NumberPicker numberPicker;
    private ArrayList<Double> initialQuants = new ArrayList<>();






    public MyFoodRecipesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userFavRecipesRef = FirebaseDatabase.getInstance().getReference("User").child(userId).child("user-favRecipes");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_my_food_recipes, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        fetchRecipesFromFirebase();
        bottomSheetConstraint = getView().findViewById(R.id.bottom_app_sheet_recipe_view);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetConstraint);

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_HIDDEN);

        bapsChangeServingsBtn = view.findViewById(R.id.baps_change_servings_button);



        //TODO: add more func. to bottom sheet eg. removeFromFav btn, see similar recipes,





    }




    public void setUpIngredientsRCV(int pos){

        ingredients = recipes.get(pos).getIngredients();

        Log.d("TAG", "setUpIngredientsRCV: recipe::: " + recipes.get(pos).getIngredients());
        System.out.println("these are the ingredients: " + ingredients.toString());
        ingredientsRecycler = getView().findViewById(R.id.baps_recipe_viewer_ingredients_rcv);
        ingredientsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        ingredientsAdapter = new MyIngredientsAdapter(getContext(), ingredients);
        ingredientsRecycler.setAdapter(ingredientsAdapter);
        adapter.notifyDataSetChanged();


    }


    public void fetchRecipesFromFirebase(){

        userFavRecipesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                recipes.clear();

                List<String> keys = new ArrayList<>();
                for(DataSnapshot keyNode: snapshot.getChildren()){
                    keys.add(keyNode.getKey());
                    Recipe recipe = keyNode.getValue(Recipe.class);
                    System.out.println(recipe);
                    recipes.add(recipe);
                }

                for(Recipe recipe: recipes){

                    System.out.println(recipe.toString());
                    System.out.println("recipe Name: " + recipe.getName());

                    System.out.println("Recipe Image " + recipe.getImageURI());
                    System.out.println("Recipe ID " + recipe.getRecipeID());

                    Integer origServing = recipe.getServings();

                    origServings.add(origServing);

                    for(Food food : recipe.getIngredients()) {

                        initialQuants.add(Double.parseDouble(food.getQuantity()));

                    }

                }

              setUpRCV();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.d("TAG", "onCancelled: DATABASE ERROR " + error);
            }
        });



    }

    public void setUpRCV(){
        if (getView() != null) {
            recipeRecycler = getView().findViewById(R.id.recipes_rcv_my_food);
            recipeRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new RecipeCardAdapter(getContext(), recipes, this);
            recipeRecycler.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

    }




    @Override
    public void onRecipeClick(int position) {

                setUpIngredientsRCV(position);



                if(bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }else{
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }

                headerLayout = getView().findViewById(R.id.baps_header_layout);


                recipeImage = getView().findViewById(R.id.baps_recipe_viewer_image_new);
                recipeInstructions = getView().findViewById(R.id.baps_recipe_instructions_recipe_view);
                recipeTimeTextView = getView().findViewById(R.id.baps_ready_in_mins_recipe_viewer);
                servingsTextView = getView().findViewById(R.id.baps_recipe_viewer_servings_text);

                Picasso.get()
                        .load(recipes.get(position).getImageURI().toString())
                        .fit()
                        .centerCrop()
                        .into(recipeImage);

                recipeInstructions.setText(recipes.get(position).getMethod());

                if(recipeInstructions == null){

                    recipeInstructions.setText("No method avaialable for this recipe!");
                }

                recipeTimeTextView.setText("Time: " + String.valueOf(recipes.get(position).getTime()) + " mins");

                servingsTextView.setText("Serves " + (String.valueOf(recipes.get(position).getServings())));


        bapsChangeServingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeServings(position);
            }
        });

            }


    @Override
    public void onIngredientClick(Food ingredient) {

        //TODO: func. for this
        Toast.makeText(getContext(), ingredient.getName() + " clicked!", Toast.LENGTH_SHORT).show();

    }

    public void changeServings(int pos){

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_servings,null);
        builder.setView(dialogView);
        builder.setTitle("Change Servings");
        numberPicker = dialogView.findViewById(R.id.change_servings_no_picker);
        numberPicker.setMaxValue(100);
        numberPicker.setMinValue(1);
        numberPicker.setValue(recipes.get(pos).getServings());

        builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {


                recipes.get(pos).setServings(numberPicker.getValue());
                servingsTextView.setText("Serves: " + String.valueOf(numberPicker.getValue()));

                Log.d("TAG", "onClick: new recipe servings from value picker numberPicker.getValue(): " + numberPicker.getValue());

                Log.d("TAG", "onClick: new recipe servings: " + recipes.get(pos).getServings());

                for(int i = 0; i < recipes.get(pos).getIngredients().size(); i++){

                    Log.d("TAG", "onValueChange ingredients and their quants: " + ingredients.get(i).getName() + ": " + ingredients.get(i).getQuantity());
                    Log.d("TAG", "onValueChange: initialQuants arraylist: " + initialQuants.get(i));


                    Log.d("SERVINGSCHECK", "onClick: original servings: " + origServings);
                    Log.d("SERVINGSCHECK", "onClick: number picker value: " + numberPicker.getValue());
                    Log.d("SERVINGSCHECK", "onClick: ingredient : " + ingredients.get(i).getName() + " quant: " + Double.valueOf(ingredients.get(i).getQuantity()));


                    String newQuant = String.valueOf(changeIngredientQuants(origServings.get(i), numberPicker.getValue(), initialQuants.get(i)));

                    formatQuants(newQuant);

                    Log.d("TAG", "onClick: new quants after format: " + ingredients.get(i).getName() + " " +  " Qunatity: " + newQuant);

                    ingredients.get(i).setQuantity(newQuant);
                    //ingredients.clear();


                    Log.d("TAG", "onClick: result of setQuant: " + String.valueOf(changeIngredientQuants(initialQuants.get(i), numberPicker.getValue(), Double.valueOf(ingredients.get(i).getQuantity()))));


                    Log.d("TAG", "onClick: the ingredients new quants:  "+ingredients.get(i).getName() + ", Quant: "+ ingredients.get(i).getQuantity());


                    adapter.notifyDataSetChanged();
                }

                recipes.get(pos).setIngredients(ingredients);
                setUpIngredientsRCV(pos);
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


}


