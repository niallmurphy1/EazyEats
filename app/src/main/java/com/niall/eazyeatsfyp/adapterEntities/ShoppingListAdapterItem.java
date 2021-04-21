package com.niall.eazyeatsfyp.adapterEntities;

public abstract class ShoppingListAdapterItem {

    private int type;
    protected ShoppingListAdapterItem(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
