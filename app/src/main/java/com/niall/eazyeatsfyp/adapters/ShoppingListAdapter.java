package com.niall.eazyeatsfyp.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.niall.eazyeatsfyp.R;
import com.niall.eazyeatsfyp.adapterEntities.ShoppingListAdapterItem;
import com.niall.eazyeatsfyp.adapterEntities.ShoppingListCategoryItem;
import com.niall.eazyeatsfyp.adapterEntities.ShoppingListItemForAdapter;

import java.util.ArrayList;
import java.util.List;

//TODO: try to migrate to the better way of doing this rcv

public class ShoppingListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final ArrayList<ShoppingListAdapterItem> shoppingListAdapterItems = new ArrayList<>();
    private final ShoppingListItemForAdapter.OnShopListItemListener onShopListItemListener;


    public ShoppingListAdapter(ShoppingListItemForAdapter.OnShopListItemListener onShopListItemListener) {
        this.onShopListItemListener = onShopListItemListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == R.layout.shop_list_category_item_rcv) {
            return new CategoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
        } else  {

            //TODO: find out why this returns a Resources$NotFoundException

            Log.d(ShoppingListAdapter.class.getSimpleName(), "onCreateViewHolder: The view type : " + viewType);
            return new ShopListItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false), onShopListItemListener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if(holder instanceof CategoryViewHolder){
            ((CategoryViewHolder) holder).bind((ShoppingListCategoryItem) shoppingListAdapterItems.get(position));
        }else if (holder instanceof ShopListItemViewHolder){
            ((ShopListItemViewHolder) holder).bind((ShoppingListItemForAdapter) shoppingListAdapterItems.get(position)) ;
        }
    }

    @Override
    public int getItemCount() {
        return shoppingListAdapterItems.size();
    }


    @Override
    public int getItemViewType(int position) {
        ShoppingListAdapterItem item = shoppingListAdapterItems.get(position);
        return item.getType();

    }

    public void fillItems(ArrayList<ShoppingListAdapterItem> shoppingListAdapterItems) {
        this.shoppingListAdapterItems.clear();
        this.shoppingListAdapterItems.addAll(shoppingListAdapterItems);
        notifyDataSetChanged();
    }

    class CategoryViewHolder extends RecyclerView.ViewHolder{

        private TextView categoryTextView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTextView = itemView.findViewById(R.id.category_name_text);
        }

        public void bind(ShoppingListCategoryItem item) {
            categoryTextView.setText(String.valueOf(item.getCategory()));
        }
    }

    class ShopListItemViewHolder extends RecyclerView.ViewHolder{

        private TextView nameText;
        private ShoppingListItemForAdapter.OnShopListItemListener shopListItemListener;
        public ShopListItemViewHolder(@NonNull View itemView, ShoppingListItemForAdapter.OnShopListItemListener listener) {
            super(itemView);
            nameText = itemView.findViewById(R.id.shop_list_item_text_view);
            shopListItemListener = listener;
        }

        public void bind(ShoppingListItemForAdapter itemForAdapter){
            nameText.setText(String.valueOf(itemForAdapter.getName()));
            itemView.setOnClickListener(v -> shopListItemListener.onShopListItemClick(itemForAdapter));
            itemView.setOnClickListener(view -> shopListItemListener.onShopListItemClick(itemForAdapter));
        }
    }
}


