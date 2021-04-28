package com.niall.eazyeatsfyp.util;

import com.google.firebase.database.DatabaseReference;
import com.niall.eazyeatsfyp.Callback;
import com.niall.eazyeatsfyp.adapterEntities.ShoppingListAdapterItem;
import com.niall.eazyeatsfyp.adapterEntities.ShoppingListProductAdapterItem;

import java.util.ArrayList;

public interface ShopListActionListener {

    public void deleteIngredientsFromFirebase(ArrayList<ShoppingListProductAdapterItem> selectedItems);

    public void onItemClick(ShoppingListProductAdapterItem shoppingListProductAdapterItem);


}
