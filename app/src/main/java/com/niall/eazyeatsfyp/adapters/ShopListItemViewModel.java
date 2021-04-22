package com.niall.eazyeatsfyp.adapters;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ShopListItemViewModel extends ViewModel {

    //initialise variables
    MutableLiveData<String> mutableLiveData = new MutableLiveData<>();

    public void setFoodMutableLiveData(String s){
        mutableLiveData.setValue(s);
    }

    public MutableLiveData<String> getText() {
        return mutableLiveData;
    }
}