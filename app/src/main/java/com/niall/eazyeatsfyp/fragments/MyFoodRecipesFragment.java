package com.niall.eazyeatsfyp.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.niall.eazyeatsfyp.R;
import com.niall.eazyeatsfyp.RecipeViewerActivity;
import com.niall.eazyeatsfyp.adapters.MyIngredientsAdapter;
import com.niall.eazyeatsfyp.adapters.RecipeCardAdapter;
import com.niall.eazyeatsfyp.entities.Food;
import com.niall.eazyeatsfyp.entities.Recipe;
import com.niall.eazyeatsfyp.util.FormatDouble;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;


public class MyFoodRecipesFragment extends Fragment implements RecipeCardAdapter.ViewHolder.OnRecipeListener, MyIngredientsAdapter.ViewHolder.OnMyIngredientListener {

    public FirebaseAuth fAuth = FirebaseAuth.getInstance();
    public FirebaseUser fUser = fAuth.getCurrentUser();
    final String userId = fUser.getUid();

    private boolean undoClicked;

    public RecyclerView recipeRecycler;
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

    private TextView recipeNameText;
    private ImageView recipeImage;
    private TextView recipeInstructions;
    private TextView recipeTimeTextView;
    private TextView servingsTextView;
    private Button bapsChangeServingsBtn;
    private Button bapsViewFullRecipeBtn;

    private Intent recipeViewerIntent;
    public static final String RECIPE_ID = "recipeID";
    public static final String RECIPE_IMAGEURI = "recipeImage";

    private FormatDouble formatDouble = new FormatDouble();

    private int newServe;
    private ArrayList<Integer> origServings = new ArrayList<>();

    private ArrayList<Food> newIngredients = new ArrayList<>();

    private NumberPicker numberPicker;
    private ArrayList<Double> initialQuants = new ArrayList<>();


    private ItemTouchHelper.SimpleCallback simpleCallback;


    public MyFoodRecipesFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        userFavRecipesRef = FirebaseDatabase.getInstance().getReference("User").child(userId).child("user-favRecipes");


        simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)

                        .addSwipeLeftBackgroundColor(ContextCompat.getColor(getContext(), R.color.deleteRed))
                        .addSwipeLeftActionIcon(R.drawable.ic_delete_bin).create().decorate();

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

                final int position = viewHolder.getAdapterPosition();

                Log.d("TAG", "onSwiped: recipe deleted position " + recipes.get(position));

                // Log.d("TAG", "onSwiped: recipe deleted position -1 " + recipes.get(position-1));
                Recipe deletedRecipe = recipes.get(position);


                if (direction == ItemTouchHelper.LEFT) {
                    // recipes.remove(position);

                    undoClicked = false;
                    Snackbar.make(recipeRecycler, deletedRecipe.getName() + " Deleted!",
                            Snackbar.LENGTH_LONG).show();

                    deleteIfNotUndo(deletedRecipe);
                }
            }
        };
    }


    public void deleteIfNotUndo(Recipe deletedRecipe) {

        if (!undoClicked) {

            Log.d("TAG", "onSwiped: on DeleteRecipe: " + deletedRecipe.toString());

            userFavRecipesRef = FirebaseDatabase.getInstance().getReference("User").child(userId).child("user-favRecipes");

            userFavRecipesRef.child(deletedRecipe.getRecipeID()).removeValue();

        }

    }


    public void addToFirebase(Recipe deletedRecipe) {

        Map newOldRecipe = new HashMap();
        DatabaseReference recipeRef = FirebaseDatabase.getInstance().getReference("Recipe");

        userFavRecipesRef = FirebaseDatabase.getInstance().getReference("User").child(userId).child("user-favRecipes");

        String key = recipeRef.push().getKey();

        Map<String, Object> recipeValues = deletedRecipe.toMap();

        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put(deletedRecipe.getRecipeID(), recipeValues);
        newOldRecipe.put(deletedRecipe.getRecipeID(), recipeValues);

        userFavRecipesRef.updateChildren(newOldRecipe).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {

                Log.d("ADDTOFIREBASE", "onSuccess: recipe added:  " + deletedRecipe.toString());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {


                Log.e("ADDTOFIREBASE", "onFailure: Failed to add ", e);
            }
        });

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

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        bapsChangeServingsBtn = view.findViewById(R.id.baps_change_servings_button);
        bapsViewFullRecipeBtn = view.findViewById(R.id.bap_view_full_recipe_button);


    }


    public void setUpIngredientsRCV(int pos) {

        ingredients = recipes.get(pos).getIngredients();

        Log.d("TAG", "setUpIngredientsRCV: recipe::: " + recipes.get(pos).getIngredients());
        System.out.println("these are the ingredients: " + ingredients.toString());
        ingredientsRecycler = getView().findViewById(R.id.baps_recipe_viewer_ingredients_rcv);
        ingredientsRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        ingredientsAdapter = new MyIngredientsAdapter(getContext(), this);
        ingredientsAdapter.setMyIngredientsData(ingredients);
        ingredientsRecycler.setAdapter(ingredientsAdapter);
        adapter.notifyDataSetChanged();


    }


    public void fetchRecipesFromFirebase() {

        userFavRecipesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                recipes.clear();

                List<String> keys = new ArrayList<>();
                for (DataSnapshot keyNode : snapshot.getChildren()) {
                    keys.add(keyNode.getKey());
                    Recipe recipe = keyNode.getValue(Recipe.class);
                    System.out.println(recipe);
                    recipes.add(recipe);
                }

                for (Recipe recipe : recipes) {

                    System.out.println(recipe.toString());
                    System.out.println("recipe Name: " + recipe.getName());

                    System.out.println("Recipe Image " + recipe.getImageURI());
                    System.out.println("Recipe ID " + recipe.getRecipeID());

                    Integer origServing = recipe.getServings();

                    origServings.add(origServing);

                    for (Food food : recipe.getIngredients()) {

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

    public void setUpRCV() {

        if (getView() != null) {

            ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
            recipeRecycler = getView().findViewById(R.id.recipes_rcv_my_food);
            recipeRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
            adapter = new RecipeCardAdapter(getContext(), recipes, this);
            itemTouchHelper.attachToRecyclerView(recipeRecycler);
            recipeRecycler.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

    }


    @Override
    public void onRecipeClick(int position) {

        setUpIngredientsRCV(position);


        if (bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        } else {
            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        }

        headerLayout = getView().findViewById(R.id.baps_header_layout);


        recipeNameText = getView().findViewById(R.id.baps_recipe_viewer_recipe_name_text);
        recipeImage = getView().findViewById(R.id.baps_recipe_viewer_image_new);
        recipeInstructions = getView().findViewById(R.id.baps_recipe_instructions_recipe_view);
        recipeTimeTextView = getView().findViewById(R.id.baps_ready_in_mins_recipe_viewer);
        servingsTextView = getView().findViewById(R.id.baps_recipe_viewer_servings_text);

        recipeNameText.setText(recipes.get(position).getName());

        Picasso.get()
                .load(recipes.get(position).getImageURI().toString())
                .fit()
                .centerCrop()
                .into(recipeImage);

        recipeInstructions.setText(recipes.get(position).getMethod());

        Log.d(MyFoodIngredientsFragment.class.getSimpleName(), "onRecipeClick: you clicked: " + recipes.get(position).toString());
        if (recipeInstructions == null) {

            recipeInstructions.setText("No method available for this recipe!");
        }

        recipeTimeTextView.setText("Time: " + String.valueOf(recipes.get(position).getTime()) + " mins");

        servingsTextView.setText("Serves " + (String.valueOf(recipes.get(position).getServings())));


        bapsChangeServingsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeServingsRework(position);
            }
        });

        bapsViewFullRecipeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recipeViewerIntent = new Intent(getContext(), RecipeViewerActivity.class);

                recipeViewerIntent.putExtra("WhereFrom", "RECIPEFROMLIKEDRECIPES");
                recipeViewerIntent.putExtra(RECIPE_ID, recipes.get(position).getRecipeID());
                recipeViewerIntent.putExtra(RECIPE_IMAGEURI, recipes.get(position).getImageURI());

                startActivity(recipeViewerIntent);
            }
        });
    }


    @Override
    public void onIngredientClick(Food ingredient) {

        Toast.makeText(getContext(), ingredient.getName() + " clicked!", Toast.LENGTH_SHORT).show();

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_new_shop_list_item, null);
        builder.setView(dialogView);
        TextView dialogTextName = dialogView.findViewById(R.id.add_to_shop_list_tview);
        dialogTextName.setText(String.format("Add %s to Shopping list?", ingredient.getName()));
        builder.setTitle("Add to shopping list");
        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                addIngredientToShopList(ingredient);
            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
    }

    private void addIngredientToShopList(Food ingredient) {


    }

    public int getServingsTextView(TextView textView) {

        String textViewText = textView.getText().toString();

        String servings = textViewText.substring(7).trim();

        int serve = Integer.parseInt(servings);

        return serve;
    }


    public void changeServingsRework(int pos) {

        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_change_servings, null);
        builder.setView(dialogView);
        builder.setTitle("Change Servings");
        numberPicker = dialogView.findViewById(R.id.change_servings_no_picker);
        numberPicker.setMaxValue(100);
        numberPicker.setMinValue(1);
        //change this to get the value in the textview
        numberPicker.setValue(getServingsTextView(servingsTextView));
        builder.setPositiveButton("Change", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                ArrayList<Food> recipeIngredients = new ArrayList<>();

                int newServe = numberPicker.getValue();
                servingsTextView.setText("Serves: " + newServe);

                double multiplyBy = (double) newServe / recipes.get(pos).getServings();

                for (Food food : ingredients) {

                    double newFoodQuant = (Double.parseDouble(food.getQuantity()) * multiplyBy);

                    Food newFood = new Food(food.getName(), FormatDouble.format2DecimalPlaces(newFoodQuant), food.getUnit());

                    recipeIngredients.add(newFood);

                }

                ingredientsAdapter.setMyIngredientsData(recipeIngredients);
                ingredientsAdapter.notifyDataSetChanged();


            }


        });

        AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }


}


