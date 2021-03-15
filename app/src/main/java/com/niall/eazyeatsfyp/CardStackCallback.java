package com.niall.eazyeatsfyp;

import androidx.recyclerview.widget.DiffUtil;


import com.niall.eazyeatsfyp.entities.Recipe;

import java.util.List;

public class CardStackCallback extends DiffUtil.Callback {

    private List<Recipe> oldRecipes, newRecipes;

    public CardStackCallback(List<Recipe> oldRecipes, List<Recipe> newRecipes){
       this.oldRecipes = oldRecipes;
       this.newRecipes = newRecipes;
    }


    @Override
    public int getOldListSize() {
        return oldRecipes.size();
    }

    @Override
    public int getNewListSize() {
        return newRecipes.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldRecipes.get(oldItemPosition).getImageURI() == newRecipes.get(newItemPosition).getImageURI();
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldRecipes.get(oldItemPosition) == newRecipes.get(newItemPosition);
    }
}
