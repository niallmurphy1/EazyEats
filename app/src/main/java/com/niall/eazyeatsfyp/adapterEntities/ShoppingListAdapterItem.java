package com.niall.eazyeatsfyp.adapterEntities;

public abstract class ShoppingListAdapterItem {

    private int type;
    protected ShoppingListAdapterItem(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    @Override
    public String toString() {
        return "ShoppingListAdapterItem{" +
                "type=" + type +
                '}';
    }
}
