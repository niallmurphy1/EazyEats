package com.niall.eazyeatsfyp.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.niall.eazyeatsfyp.R;
import com.niall.eazyeatsfyp.entities.ShoppingListItem;

import java.util.ArrayList;

public class ChildShopListRecyclerAdapter extends RecyclerView.Adapter<ChildShopListRecyclerAdapter.ViewHolder>{

    ArrayList<ShoppingListItem> shoppingListItems;
    ViewHolder.OnShopListener onShopListener;

    public ChildShopListRecyclerAdapter(){

    }


    public ChildShopListRecyclerAdapter(ArrayList<ShoppingListItem> items){

        this.shoppingListItems = items;

    }

    public ViewHolder.OnShopListener getOnShopListener() {
        return onShopListener;
    }

    public void setOnShopListener(ViewHolder.OnShopListener onShopListener) {
        this.onShopListener = onShopListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View view = layoutInflater.inflate(R.layout.section_shop_list_item_view, parent, false);
        return new ViewHolder(view, onShopListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


       holder.setData(shoppingListItems.get(position));

    }

    @Override
    public int getItemCount() {
        return shoppingListItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{


        TextView shopListItemTextView;
        OnShopListener onShopListener;


        public ViewHolder(@NonNull View itemView, OnShopListener onShopListener) {
            super(itemView);

            this.shopListItemTextView = itemView.findViewById(R.id.shop_list_item_text_view);
            this.onShopListener = onShopListener;
        }

        public void setData(ShoppingListItem item){

            shopListItemTextView.setText(item.getName());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onShopListener.onShopListItemClick(v,item);
                }
            });

        }

        public interface OnShopListener{

            public void onShopListItemClick(View v,ShoppingListItem item);
        }
    }

}
