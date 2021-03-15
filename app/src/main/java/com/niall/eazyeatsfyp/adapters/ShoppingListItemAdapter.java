package com.niall.eazyeatsfyp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.niall.eazyeatsfyp.R;
import com.niall.eazyeatsfyp.entities.ShoppingListItem;

import java.util.ArrayList;

public class ShoppingListItemAdapter extends RecyclerView.Adapter<ShoppingListItemAdapter.ViewHolder> implements Filterable {


    private LayoutInflater layoutInflater;
    private ArrayList<ShoppingListItem> shoppingListData;
    private ViewHolder.OnShopListItemListener mOnShopListener;

    private ArrayList<ShoppingListItem> filteredItems = new ArrayList<>();


    public ShoppingListItemAdapter(){

    }

    public ShoppingListItemAdapter(Context context, ViewHolder.OnShopListItemListener shopListItemListener){
        this.layoutInflater = LayoutInflater.from(context);
        this.mOnShopListener = shopListItemListener;

    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

              ArrayList<ShoppingListItem> filteredItems = new ArrayList<>();
                FilterResults filterResults = new FilterResults();


                if(constraint==null || constraint.length() == 0){
                    filteredItems.addAll(shoppingListData);
                    filterResults.values = shoppingListData;
                    filterResults.count = shoppingListData.size();

                }else{
                    filterResults.values=filteredItems;
                    filterResults.count=filteredItems.size();
                }

                for(ShoppingListItem shoppingListItem: shoppingListData){
                    if(shoppingListItem.getName().contains(constraint.toString().toLowerCase())){
                        filteredItems.add(shoppingListItem);
                    }
                }



                return filterResults;


            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

                ShoppingListItemAdapter.this.filteredItems = (ArrayList<ShoppingListItem>) results.values;
                notifyDataSetChanged();
            }
        };
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {


        private TextView shopListItemName, shopListItemCat;
        OnShopListItemListener mOnShopListItemListener;

        public ViewHolder(@NonNull View itemView, OnShopListItemListener onShopListItemListener) {
            super(itemView);
            this.shopListItemName = itemView.findViewById(R.id.shopListItemName);
            this.shopListItemCat = itemView.findViewById(R.id.shopListItemCat);
            this.mOnShopListItemListener = onShopListItemListener;


        }


        public void setData(ShoppingListItem dataItem){
            shopListItemName.setText(dataItem.getName());
            shopListItemCat.setText(dataItem.getCategory());
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnShopListItemListener.onShopListItemClick(dataItem);
                }
            });




        }


        public interface OnShopListItemListener{

            public void onShopListItemClick(ShoppingListItem item);

        }
    }


    @NonNull
    @Override
    public ShoppingListItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.shop_list_item_rcv, parent, false);

        return new ViewHolder(view, mOnShopListener);

    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingListItemAdapter.ViewHolder holder, int position) {
        holder.setData(filteredItems.get(position));
    }

    public void setShoppingListData(ArrayList<ShoppingListItem> shoppingListData){
        filteredItems = shoppingListData;
        this.shoppingListData = shoppingListData;
    }

    @Override
    public int getItemCount() {

        return filteredItems == null ? 0: filteredItems.size();
    }




}
