package com.niall.eazyeatsfyp.adapters;

import android.content.Intent;
import android.graphics.Movie;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.niall.eazyeatsfyp.ProductSelectorActivity;
import com.niall.eazyeatsfyp.R;
import com.niall.eazyeatsfyp.RecipeViewerActivity;
import com.niall.eazyeatsfyp.entities.Food;
import com.niall.eazyeatsfyp.entities.Recipe;
import com.niall.eazyeatsfyp.entities.ShopListCategory;
import com.niall.eazyeatsfyp.entities.ShoppingListItem;
import com.niall.eazyeatsfyp.fragments.ShoppingListFragment;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import static com.niall.eazyeatsfyp.ProductSelectorActivity.SHOPLISTITEMNAME;

public class MainShopListTestAdapter extends RecyclerView.Adapter<MainShopListTestAdapter.ViewHolder> implements ChildShopListRecyclerAdapter.ViewHolder.OnShopListener{


    ArrayList<ShopListCategory> shopListCategories;


    public void setShopListCategories(ArrayList<ShopListCategory> shopListCategories) {
        this.shopListCategories = shopListCategories;
    }

    public ArrayList<ShopListCategory> getShopListCategories() {
        return shopListCategories;
    }

    public MainShopListTestAdapter(ArrayList<ShopListCategory> cats){

        this.shopListCategories = cats;

    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.section_shop_list_row, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        ShopListCategory shopListCategory = shopListCategories.get(position);

        String categoryName = shopListCategory.getName();

        ArrayList<ShoppingListItem> shoppingListItems = shopListCategory.getItems();

        holder.categoryNameTextView.setText(categoryName);

        ChildShopListRecyclerAdapter childShopListRecyclerAdapter = new ChildShopListRecyclerAdapter(shoppingListItems);
        childShopListRecyclerAdapter.setOnShopListener(this);

        holder.childRecycler.setAdapter(childShopListRecyclerAdapter);



    }

    @Override
    public int getItemCount() {
        return shopListCategories.size();
    }


    @Override
    public void onShopListItemClick(View v, ShoppingListItem item) {

        Toast.makeText(v.getContext(),  item.getName() + " clicked!", Toast.LENGTH_SHORT).show();
        Log.d("TAG", "onShopListItemClick: item: " + item.toString());



        //TODO: need the barcode for this....May not work the same way as it did in ElectronicsStore
        v.getContext().startActivity(ProductSelectorActivity.getIntent(v.getContext(), item.getName(), item.getBarcodeUPC()));
    }

    class ViewHolder extends RecyclerView.ViewHolder{


        TextView categoryNameTextView;
        RecyclerView childRecycler;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            categoryNameTextView = itemView.findViewById(R.id.shop_list_category_name_txt);
            childRecycler = itemView.findViewById(R.id.shop_list_items_child_rcv);
        }
    }

    private void getRidOfDuplicates(ArrayList<String> shopListCategoryNames) {
        if(shopListCategoryNames != null) {
            LinkedHashSet<String> set = new LinkedHashSet<String>(shopListCategoryNames);
            shopListCategoryNames.clear();
            shopListCategoryNames.addAll(set);
        }


    }
}
