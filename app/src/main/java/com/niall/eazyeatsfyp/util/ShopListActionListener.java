package com.niall.eazyeatsfyp.util;

import com.niall.eazyeatsfyp.adapterEntities.ShoppingListAdapterItem;

import java.util.ArrayList;

public interface ShopListActionListener {

    public void deleteIngredientsFromFirebase(ArrayList<ShoppingListAdapterItem> selectedItems);

}
