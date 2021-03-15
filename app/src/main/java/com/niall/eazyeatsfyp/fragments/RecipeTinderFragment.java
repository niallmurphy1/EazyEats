package com.niall.eazyeatsfyp.fragments;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;

import android.text.Html;
import android.util.Log;
import android.view.Choreographer;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.niall.eazyeatsfyp.R;
import com.niall.eazyeatsfyp.adapters.CardStackAdapter;
import com.niall.eazyeatsfyp.entities.Food;
import com.niall.eazyeatsfyp.entities.Recipe;
import com.niall.eazyeatsfyp.entities.ShoppingListItem;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;
import com.yuyakaido.android.cardstackview.StackFrom;
import com.yuyakaido.android.cardstackview.SwipeableMethod;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static com.niall.eazyeatsfyp.util.Constants.RECIPE_SEARCH;
import static com.niall.eazyeatsfyp.util.Constants.SP_APIKEY;


public class RecipeTinderFragment extends Fragment implements CardStackAdapter.ViewHolder.OnCardClickListener{

    public FirebaseAuth fAuth = FirebaseAuth.getInstance();
    public FirebaseUser fUser = fAuth.getCurrentUser();
    final String userId = fUser.getUid();

    private Toolbar toolbar;

    //Bottom sheet components
    private ConstraintLayout bottomSheetConstraint;
    private LinearLayout headerLayout;
    private ImageView headerArrowImage;
    private BottomSheetBehavior bottomSheetBehavior;
    private Button applyFiltersBtn;

    private ChipGroup cuisineChips;
    private ChipGroup mealTypeChips;
    private ChipGroup allergenChips;


    private DatabaseReference userFavRecipesRef;

    private CardStackLayoutManager manager;
    private CardStackAdapter adapter;

    public Map newLikedRecipe = new HashMap();

    private static final String TAG = "RECIPE_TINDER";

    //private ArrayList<Recipe> tinderRecipes = new ArrayList<>();
    private ArrayList<Recipe> favRecipes = new ArrayList<>();
    private ArrayList<Food> ingredients = new ArrayList<>();


    MaterialAlertDialogBuilder materialAlertDialogBuilder;


    public RecipeTinderFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        setHasOptionsMenu(true);


        userFavRecipesRef = FirebaseDatabase.getInstance().getReference("User").child(userId).child("user-favRecipes");


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment


        return inflater.inflate(R.layout.fragment_recipe_tinder, container, false);
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.recipe_tinder_refine_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }


    //TODO: find a new use for options menu or get rid of it completely; log out perhaps
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){

            case R.id.random_menu_item:
                fetchRecipes("&sort=random&fillIngredients=true&number=10");
                return true;

            case R.id.by_type_breakfast_option:
                fetchRecipes("&type=breakfast&sort=random&fillIngredients=true&number=10");
                return true;

            case R.id.by_type_main_course_option:
                fetchRecipes("&type=main%20course&sort=random&fillIngredients=true&number=10");
                return true;

            case R.id.by_type_dessert_option:
                fetchRecipes("&type=dessert&sort=random&fillIngredients=true&number=10");
                return true;

            case R.id.by_type_side_dish_option:
                fetchRecipes("&type=side%20dish&sort=random&fillIngredients=true&number=10");
                return true;


            case R.id.by_type_beverage_option:
                fetchRecipes("&type=beverage&sort=random&fillIngredients=true&number=10");
                return true;

            case R.id.by_cuisine_french:
                fetchRecipes("&cuisine=french&sort=random&fillIngredients=true&number=10");
                return true;

            case R.id.by_cuisine_italian:
                fetchRecipes("&cuisine=italian&sort=random&fillIngredients=true&number=10");
                return true;

            case R.id.by_cuisine_chinese:
                fetchRecipes("&cuisine=chinese&sort=random&fillIngredients=true&number=10");
                return true;

            case R.id.by_cuisine_mexican:
                fetchRecipes("&cuisine=mexican&sort=random&fillIngredients=true&number=10");
                return true;


            case R.id.like_mine_options_menu:

                Log.d(TAG, "onOptionsItemSelected: hellooooo");
                Log.d(TAG, "Similar to response Clicked:  " + favRecipes.size());

                ArrayList<String> allIngredients = new ArrayList<>();

                for(int i =0; i < favRecipes.size(); i++){

                    for(int p = 0; p < favRecipes.get(i).getIngredients().size(); p++){

                        allIngredients.add(favRecipes.get(i).getIngredients().get(p).getName().toLowerCase());
                    }

                    Log.d(TAG, "Fav ingredients names: " + allIngredients.get(i));
                }

                countFrequencies(allIngredients);

                String mostCommon = mostCommon(allIngredients).replace(" ", "%20");

                allIngredients.remove(mostCommon.replace("%20", " "));

                String secondMostCommon = mostCommon(allIngredients).replace(" ", "%20");

                Log.d(TAG, "onOptionsItemSelected: Most common item: "
                        + mostCommon + " \n second most common: " + secondMostCommon);


                ArrayList<String> allCuisines = new ArrayList<>();

                for(int i =0; i < favRecipes.size(); i++) {

                    if (favRecipes.get(i).getCuisines() != null) {
                        for (int p = 0; p < favRecipes.get(i).getCuisines().size(); p++) {

                            allCuisines.add(favRecipes.get(i).getCuisines().get(p));
                        }

                        Log.d(TAG, "all cuisines names: " + allCuisines.get(i));
                    }
                    else{
                        Log.d(TAG, "null cuisines on recipe: "+ favRecipes.get(i).getName());
                    }
                }

                String mostCommonCuisine = mostCommon(allCuisines).toLowerCase();

                Log.d(TAG, "onOptionsItemSelected: Most common cuisine " + mostCommonCuisine);

                Log.d(TAG, "onOptionsItemSelected: most common ingredient: " + mostCommon);

                String query = "&includeIngredients="+secondMostCommon+"&cuisine="+ mostCommonCuisine+"&sort=random&fillIngredients=true&number=10";

                fetchRecipes(query);


        }
        return super.onOptionsItemSelected(item);
    }

    public static <T> T mostCommon(List<T> list) {
        Map<T, Integer> map = new HashMap<>();

        for (T t : list) {
            Integer val = map.get(t);
            map.put(t, val == null ? 1 : val + 1);
        }

        Map.Entry<T, Integer> max = null;

        for (Map.Entry<T, Integer> e : map.entrySet()) {
            if (max == null || e.getValue() > max.getValue())
                max = e;
        }

        return max.getKey();
    }


    public static void countFrequencies(ArrayList<String> list)
    {
        // hashmap to store the frequency of element
        Map<String, Integer> hm = new HashMap<String, Integer>();

        for (String i : list) {
            Integer j = hm.get(i);
            hm.put(i, (j == null) ? 1 : j + 1);
        }

        // displaying the occurrence of elements in the arraylist
        for (Map.Entry<String, Integer> val : hm.entrySet()) {
            System.out.println("Element " + val.getKey() + " "
                    + "occurs"
                    + ": " + val.getValue() + " times");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //((AppCompatActivity) getActivity()).getSupportActionBar().hide();
        toolbar = getView().findViewById(R.id.toolbar_recipeTinder);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        CardStackView cardStackView = view.findViewById(R.id.card_stack_view);

        bottomSheetConstraint = view.findViewById(R.id.bottom_sheet_filter);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetConstraint);
        applyFiltersBtn = view.findViewById(R.id.apply_filters_button);

        cuisineChips = view.findViewById(R.id.cuisines_chipgroup);
        mealTypeChips = view.findViewById(R.id.mealtype_chipgroup);
        allergenChips = view.findViewById(R.id.allergens_chipgroup);

        headerLayout = view.findViewById(R.id.header_layout);
        headerArrowImage = view.findViewById(R.id.header_arrow);

        headerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }else{
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                switch(newState){

                    case BottomSheetBehavior.STATE_COLLAPSED:
                        //do something

                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                headerArrowImage.setRotation(180 * slideOffset);
            }
        });

        applyFiltersBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                    //do logic for filtering

                    Log.d(TAG, "onClick: checked cuisines: " + getChecked(cuisineChips));
                    Log.d(TAG, "onClick: checked mealtypes" + getChecked(mealTypeChips));
                    Log.d(TAG, "onClick: checked allergens" + getChecked(allergenChips));

                   // Log.d(TAG, "onClick: List of checked chip IDs: " + getChecked(cuisineChips));

                    if(getChecked(cuisineChips) == null && getChecked(mealTypeChips) == null && getChecked(allergenChips) == null){

                        //no filters applied
                        fetchRecipes("&sort=random&fillIngredients=true&instructionsRequired=true&number=10");


                    }else if(getChecked(cuisineChips) == null && getChecked(mealTypeChips) == null && getChecked(allergenChips) != null ){

                        //everything but allergens is null
                        fetchRecipes("&intolerances="+getChecked(allergenChips)+"&sort=random&fillIngredients=true&instructionsRequired=true&number=10");


                    }else if(getChecked(cuisineChips) == null && getChecked(mealTypeChips) != null && getChecked(allergenChips) == null){

                        //everything but mealtypes is null

                        fetchRecipes("&type="+getChecked(mealTypeChips)+"&sort=random&fillIngredients=true&instructionsRequired=true&number=10");


                    }else if(getChecked(cuisineChips) != null && getChecked(mealTypeChips) == null && getChecked(allergenChips) == null){

                        //everything but cuisines is null
                        fetchRecipes("&cuisine="+getChecked(cuisineChips)+"&sort=random&fillIngredients=true&instructionsRequired=true&number=10");

                    }else if(getChecked(cuisineChips) == null && getChecked(mealTypeChips) != null && getChecked(allergenChips) != null){

                        //only cuisines is null
                        fetchRecipes("&intolerances="+getChecked(allergenChips)+"&type="+getChecked(mealTypeChips)+"&sort=random&fillIngredients=true&instructionsRequired=true&number=10");


                    }else if(getChecked(cuisineChips) != null  && getChecked(mealTypeChips) == null && getChecked(allergenChips) != null){

                        //only mealtypes is null
                        fetchRecipes("&intolerances="+getChecked(allergenChips)+"&cuisine="+getChecked(cuisineChips)+"&sort=random&fillIngredients=true&instructionsRequired=true&number=10");


                    }else if(getChecked(cuisineChips) != null  && getChecked(mealTypeChips) != null && getChecked(allergenChips) == null){

                        //only allergens is null
                        fetchRecipes("&type="+getChecked(mealTypeChips)+"&cuisine="+getChecked(cuisineChips)+"&sort=random&fillIngredients=true&instructionsRequired=true&number=10");


                    }else if(getChecked(cuisineChips) != null  && getChecked(mealTypeChips) != null && getChecked(allergenChips) != null){

                        //nothing is null
                        fetchRecipes("&type="+getChecked(mealTypeChips)+"&intolerances="+getChecked(allergenChips)+"&cuisine="+getChecked(cuisineChips)+"&sort=random&fillIngredients=true&instructionsRequired=true&number=10");

                    }

                }
            }
        });



        manager = new CardStackLayoutManager(getContext(), new CardStackListener() {
            @Override
            public void onCardDragging(Direction direction, float ratio) {
                Log.d(TAG, "onCardDragging: d=" + direction.name() + " ratio=" + ratio);


            }

            @Override
            public void onCardSwiped(Direction direction) {

                if (direction == Direction.Right){

                    Recipe recipeSwiped = adapter.getRecipes().get(manager.getTopPosition()-1);

                    //TODO: allow user to rate recipe, save rating to user-likedRecipes in FB with recipe key and rating

                    DatabaseReference recipeRef = FirebaseDatabase.getInstance().getReference("Recipe");

                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User");

                    String key = recipeRef.push().getKey();

                    Map<String, Object> recipeValues = recipeSwiped.toMap();

                    Map<String, Object> childUpdates = new HashMap<>();

                    childUpdates.put(key, recipeValues);

                    userRef.child(userId).child("user-favRecipes").updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            Toast.makeText(getContext(), recipeSwiped.getName() + " Added to favourites!", Toast.LENGTH_SHORT).show();

                            System.out.println("Recipe:" + recipeSwiped.toString() + " Added to favourites");
                        }
                    });

                    recipeRef.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "onSuccess: RECIPE ADDED" );
                        }
                    });



                }
                if (direction == Direction.Top){




                }
                if (direction == Direction.Left){
                    //dismiss
                    //Toast.makeText(getContext(), "Direction Left", Toast.LENGTH_SHORT).show();
                }
                if (direction == Direction.Bottom){
                    //dismiss
                    Toast.makeText(getContext(), "Direction Bottom", Toast.LENGTH_SHORT).show();
                }

                if (manager.getTopPosition() == adapter.getItemCount() - 5){
                    //paginate();
                    //fetchRecipes();
                }
            }

            @Override
            public void onCardRewound() {

            }

            @Override
            public void onCardCanceled() {

            }

            @Override
            public void onCardAppeared(View view, int position) {

            }

            @Override
            public void onCardDisappeared(View view, int position) {

            }
        });

        manager.setStackFrom(StackFrom.None);
        manager.setVisibleCount(3);
        manager.setTranslationInterval(8.0f);
        manager.setScaleInterval(0.95f);
        manager.setSwipeThreshold(0.3f);
        manager.setMaxDegree(20.0f);
        manager.setDirections(Direction.FREEDOM);
        manager.setCanScrollHorizontal(true);
        manager.setSwipeableMethod(SwipeableMethod.Manual);
        manager.setOverlayInterpolator(new LinearInterpolator());
        adapter = new CardStackAdapter();
        cardStackView.setLayoutManager(manager);
        adapter.setmOnCardLickListener(this);
        cardStackView.setAdapter(adapter);
        cardStackView.setItemAnimator(new DefaultItemAnimator());
        getRecipesFromFirebase();

        fetchRecipes("&sort=random&&fillIngredients=true&number=10");


    }


    public String getChecked(ChipGroup chipGroup){


        int chipGroupCount = chipGroup.getChildCount();

        String checkedChipsString = null;


        for(int i = 0 ; i < chipGroupCount; i++){

            Chip child = (Chip) chipGroup.getChildAt(i);

            if(!child.isChecked()) {
                continue;
            }

            if(checkedChipsString == null){
                checkedChipsString = child.getText().toString().toLowerCase();

            }else{

                checkedChipsString += "," + child.getText().toString().toLowerCase();
            }
        }


        //Log.d(TAG, "getCheckedCusines: Cuisines Checked: " + checkedChipsString);

        return checkedChipsString;
    }


    private int pageNum = 0;

    private void paginate() {
    }


    //TODO: add bottom sheet for recipe preview like in MyFoodRecipesFragment


    private void fetchRecipes(String query) {
        pageNum++;
        ArrayList<Recipe> tinderRecipes = new ArrayList<>();

//        tinderRecipes.clear();
//        adapter.notifyDataSetChanged();
        adapter.setRecipes(tinderRecipes);

        AndroidNetworking.get(RECIPE_SEARCH+SP_APIKEY+query)
                .addQueryParameter("limit", "1")
                .addHeaders("token", "1234")
                .setTag("test")
                .setPriority(Priority.LOW)
                .build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, "onResponse: api works...");

               System.out.println("API RESPONSE: "+ response);


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





                        JSONArray extIngredients = arr.getJSONObject(i).getJSONArray("extendedIngredients");

                        ArrayList<Food> ingredients = new ArrayList<>();
                        ArrayList<ShoppingListItem> shoppingListItems = new ArrayList<>();

                        for(int f = 0; f < extIngredients.length(); f++){



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


                        //TODO: Add recipe analyzedInstructions

                        String method ="";


                        JSONArray analyzedInstructionsArray = arr.getJSONObject(i).getJSONArray("analyzedInstructions");

                      //  Log.d(TAG, "onResponse: Analyzed instructions: " + analyzedInstructionsArray);

                        for(int t = 0; t < analyzedInstructionsArray.length(); t++){

                            JSONArray stepsArray = analyzedInstructionsArray.getJSONObject(t).getJSONArray("steps");

                            System.out.println("this is the steps array: " + stepsArray.toString());

                            for(int s = 0; s < stepsArray.length(); s++){

                                method += stepsArray.getJSONObject(s).getString("step") + "\n";
                            }
                        }


                        Log.d("Recipe Tinder!!", "onResponse: recipe method: " + method);

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


                        Recipe recipe = new Recipe(title, dishTypes,ingredients, readyInMinutes, servings, image, recipeID, cuisines);

                        recipe.setMethod(method);

                        Log.d(TAG, "onResponse: Recipes.toString(): " + recipe.getMethod());



                        tinderRecipes.add(recipe);




                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                adapter.addRecipes(tinderRecipes);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onError(ANError anError) {

                System.out.println("Error "+ anError);
            }
        });
    }







    public void getRecipesFromFirebase(){

        userFavRecipesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                favRecipes.clear();
                List<String> keys = new ArrayList<>();

                    for(DataSnapshot keyNode: snapshot.getChildren()) {
                        keys.add(keyNode.getKey());

                        Recipe recipe = keyNode.getValue(Recipe.class);

                                favRecipes.add(recipe);
                                System.out.println("This is the recipe: "+ recipe.toString());

                        Log.d(TAG, "Recipe name: " + recipe.getName());

                        Log.d(TAG, "onDataChange: This is the recipe ingredients: " + recipe.getIngredients().toString());
                    }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.d(TAG, "onCancelled:  " + error);


            }
        });


    }

    @Override
    public void onCardClick(int position) {

        Log.d(TAG, "onCardClick: recipe: " + adapter.getRecipes().get(position).getName());


    }
}