package com.niall.eazyeatsfyp.adapterEntities;

import com.niall.eazyeatsfyp.R;

public class ShoppingListCategoryItem extends ShoppingListAdapterItem {
    private String category;
    public ShoppingListCategoryItem(String category) {
        super(R.layout.shop_list_category_item_rcv);
        this.category = category;
    }
    public String getCategory() {
        return category;
    }
}