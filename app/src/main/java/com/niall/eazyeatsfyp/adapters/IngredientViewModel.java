package com.niall.eazyeatsfyp.adapters;


import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.niall.eazyeatsfyp.entities.Food;

public class IngredientViewModel extends ViewModel {

    //initialise variables
    MutableLiveData<String> mutableLiveData = new MutableLiveData<>();

    public void setFoodMutableLiveData(String s){
        mutableLiveData.setValue(s);
    }

    public MutableLiveData<String> getText() {
        return mutableLiveData;
    }
}
