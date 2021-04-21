package com.niall.eazyeatsfyp.util;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.niall.eazyeatsfyp.entities.Recipe;

import java.util.ArrayList;
import java.util.List;

public class RecipeChecker {

    public RecipeChecker(){}


    public static ArrayList<Recipe> getRecipesFromFirebase(ArrayList<Recipe> favRecipes ,DatabaseReference userFavRecipesRef, String className){

        userFavRecipesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                favRecipes.clear();
                List<String> keys = new ArrayList<>();

                for(DataSnapshot keyNode: snapshot.getChildren()) {
                    keys.add(keyNode.getKey());

                    Recipe recipe = keyNode.getValue(Recipe.class);

                    favRecipes.add(recipe);
                    // System.out.println("This is the recipe: "+ recipe.toString());

                    //Log.d(TAG, "Favourite recipe : " + recipe.getName());

                }

                Log.d(className, "onDataChange: The favourite recipes: " + favRecipes.toString());

                Log.d(className, "onDataChange: Favourite recipes size: " + favRecipes.size());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.d(className, "onCancelled:  " + error);


            }
        });

        return favRecipes;
    }

    public static boolean isDuplicateFavourite(ArrayList<Recipe> recipes, Recipe swipedRecipe){

        boolean duplicate = false;
        for(Recipe recipe: recipes) {

            if (swipedRecipe.getRecipeID().equals(recipe.getRecipeID())) {
                duplicate = true;
                break;
            }
        }
        return duplicate;
    }




}
