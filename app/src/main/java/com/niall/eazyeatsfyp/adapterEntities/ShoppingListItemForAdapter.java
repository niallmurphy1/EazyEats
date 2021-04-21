package com.niall.eazyeatsfyp.adapterEntities;

import com.niall.eazyeatsfyp.R;
import com.niall.eazyeatsfyp.entities.ShoppingListItem;

public class ShoppingListItemForAdapter extends ShoppingListAdapterItem {

    private String sId;
    private String name;
    private String category;
    private String barcodeUPC;

    public ShoppingListItemForAdapter(String sId, String name, String category, String barcodeUPC) {
        super(R.layout.section_shop_list_item_view);
        this.sId = sId;
        this.name = name;
        this.category = category;
        this.barcodeUPC = barcodeUPC;
    }

    public ShoppingListItemForAdapter(){
        super(R.layout.section_shop_list_item_view);

    }

    public ShoppingListItemForAdapter createFrom(ShoppingListItem shopListItem) {
        return new ShoppingListItemForAdapter(shopListItem.getsId()
                , shopListItem.getName()
                , shopListItem.getCategory()
                , shopListItem.getBarcodeUPC());
    }

    public String getsId() {
        return sId;
    }

    public void setsId(String sId) {
        this.sId = sId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getBarcodeUPC() {
        return barcodeUPC;
    }

    public void setBarcodeUPC(String barcodeUPC) {
        this.barcodeUPC = barcodeUPC;
    }

    public interface OnShopListItemListener {
        void onShopListItemClick(ShoppingListItemForAdapter shoppingListItemForAdapter);
    }
}



