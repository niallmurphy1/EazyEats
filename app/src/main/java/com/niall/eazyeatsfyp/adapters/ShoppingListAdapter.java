package com.niall.eazyeatsfyp.adapters;

import android.graphics.Color;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.niall.eazyeatsfyp.ProductSelectorActivity;
import com.niall.eazyeatsfyp.R;
import com.niall.eazyeatsfyp.adapterEntities.ShoppingListAdapterItem;
import com.niall.eazyeatsfyp.adapterEntities.ShoppingListCategoryItem;
import com.niall.eazyeatsfyp.adapterEntities.ShoppingListProductAdapterItem;
import com.niall.eazyeatsfyp.fragments.ShoppingListFragment;
import com.niall.eazyeatsfyp.util.ShopListActionListener;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class ShoppingListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


    private boolean isEnabled = false;
    private boolean selectAll = false;

    ShopListItemViewModel shopListItemViewModel;

    ShopListActionListener shopListener;

    private final ArrayList<ShoppingListAdapterItem> shoppingListAdapterItems = new ArrayList<>();
    private final ShoppingListProductAdapterItem.OnShopListItemListener onShopListItemListener;


    ArrayList<ShoppingListProductAdapterItem> selectedItems = new ArrayList<>();


    public ShoppingListAdapter(ShoppingListProductAdapterItem.OnShopListItemListener onShopListItemListener, ShopListActionListener shopListActionListener) {
        this.onShopListItemListener = onShopListItemListener;
        this.shopListener = shopListActionListener;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == R.layout.shop_list_category_item_rcv) {
            return new CategoryViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false));
        } else {

            shopListItemViewModel = new ViewModelProvider((FragmentActivity)parent.getContext()).get(ShopListItemViewModel.class);

            Log.d(ShoppingListAdapter.class.getSimpleName(), "onCreateViewHolder: The view type : " + viewType);
            return new ShopListItemViewHolder(LayoutInflater.from(parent.getContext()).inflate(viewType, parent, false), onShopListItemListener);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (holder instanceof CategoryViewHolder) {
            ((CategoryViewHolder) holder).bind((ShoppingListCategoryItem) shoppingListAdapterItems.get(position));
        } else if (holder instanceof ShopListItemViewHolder) {
            ((ShopListItemViewHolder) holder).bind((ShoppingListProductAdapterItem) shoppingListAdapterItems.get(position));


            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    if (!isEnabled) {
                        ActionMode.Callback callback = new ActionMode.Callback() {

                            @Override
                            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                                MenuInflater menuInflater = mode.getMenuInflater();
                                menuInflater.inflate(R.menu.multi_select_shopping_menu, menu);
                                return true;
                            }

                            @Override
                            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                                isEnabled = true;
                                clickItem(((ShopListItemViewHolder) holder));

                                shopListItemViewModel.getText().observe((LifecycleOwner) holder.itemView.getContext(), new Observer<String>() {
                                    @Override
                                    public void onChanged(String s) {
                                        mode.setTitle(String.format("%s selected", s));
                                    }
                                });
                                return true;
                            }

                            @Override
                            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                                int id = item.getItemId();
                                switch (id) {

                                    case R.id.menu_shop_list_delete_item: Log.d("DELETEACTIONBTN", "onActionItemClicked: seleceted items: " + selectedItems.toString());
                                        shopListener.deleteIngredientsFromFirebase(selectedItems);

                                        if (shoppingListAdapterItems.size() == 0) {
                                            //create text view empty, add in constructor of adapter
                                            Log.d(TAG, "onActionItemClicked: no ingredients in the RCV");
                                        }
                                        mode.finish();
                                        break;

                                    case R.id.menu_shop_list_select_all:

                                        //TODO: check if this works (shoppingListAdapterItems has categories also)

                                        ArrayList<ShoppingListProductAdapterItem> allProductItems = new ArrayList<>();
                                        for(ShoppingListAdapterItem shoppingListAdapterItem: shoppingListAdapterItems){

                                            if(shoppingListAdapterItem instanceof ShoppingListProductAdapterItem){
                                                allProductItems.add((ShoppingListProductAdapterItem) shoppingListAdapterItem);
                                            }
                                        }
                                        if (selectedItems.size() == allProductItems.size()) {

                                            selectAll = false;

                                            selectedItems.clear();

                                        } else {
                                            selectAll = true;
                                            selectedItems.clear();

                                            selectedItems.addAll(getAllProductItems());
                                        }

                                        shopListItemViewModel.setFoodMutableLiveData(String.valueOf(selectedItems.size()));

                                        notifyDataSetChanged();
                                        break;
                                }
                                return true;
                            }

                            @Override
                            public void onDestroyActionMode(ActionMode mode) {

                                isEnabled = false;
                                selectAll = false;
                                selectedItems.clear();
                                notifyDataSetChanged();
                            }
                        };

                        v.startActionMode(callback);

                    }else {
                        clickItem((ShopListItemViewHolder) holder);
                    }

                    return true;
                }
            });

            holder.itemView.setOnClickListener(v -> {
                if(isEnabled){
                    clickItem((ShopListItemViewHolder) holder);

                }else {

                    shopListener.onItemClick((ShoppingListProductAdapterItem) shoppingListAdapterItems.get(holder.getAdapterPosition()));
                }
            });

            if(selectAll){
                ((ShopListItemViewHolder) holder).checkImage.setVisibility(View.VISIBLE);

                holder.itemView.setBackgroundColor(Color.LTGRAY);
            }else {

                ((ShopListItemViewHolder) holder).checkImage.setVisibility(View.GONE);
                holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            }
        }
    }

    private List<ShoppingListProductAdapterItem> getAllProductItems(){
        ArrayList<ShoppingListProductAdapterItem> productItems = new ArrayList<>();

        if(shoppingListAdapterItems == null){
            return productItems;
        }


        for(ShoppingListAdapterItem item: shoppingListAdapterItems){
            if(item instanceof ShoppingListProductAdapterItem){
                productItems.add((ShoppingListProductAdapterItem) item);
            }
        }
        return productItems;

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

    class CategoryViewHolder extends RecyclerView.ViewHolder {

        private TextView categoryTextView;

        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryTextView = itemView.findViewById(R.id.category_name_text);
        }

        public void bind(ShoppingListCategoryItem item) {
            categoryTextView.setText(String.valueOf(item.getCategory()));
        }
    }

    class ShopListItemViewHolder extends RecyclerView.ViewHolder {

        private TextView nameText;
        private ShoppingListProductAdapterItem.OnShopListItemListener shopListItemListener;
        private ImageView checkImage;

        public ShopListItemViewHolder(@NonNull View itemView, ShoppingListProductAdapterItem.OnShopListItemListener listener) {
            super(itemView);
            nameText = itemView.findViewById(R.id.shop_list_item_text_view);
            checkImage = itemView.findViewById(R.id.shop_list_image_check);
            shopListItemListener = listener;
        }

        public void bind(ShoppingListProductAdapterItem itemForAdapter) {
            nameText.setText(String.valueOf(itemForAdapter.getName()));
            itemView.setOnClickListener(v -> shopListItemListener.onShopListItemClick(itemForAdapter));
            itemView.setOnClickListener(view -> shopListItemListener.onShopListItemClick(itemForAdapter));
            itemView.setOnLongClickListener(v -> false);

        }
    }


    void clickItem(ShopListItemViewHolder holder) {

        Log.d("CLICKITEMMETHOD", "ClickItem: this item has been clicked: " + shoppingListAdapterItems.get(holder.getAdapterPosition()));

        ShoppingListProductAdapterItem shoppingListAdapterItem = (ShoppingListProductAdapterItem) shoppingListAdapterItems.get(holder.getAdapterPosition());


        if (holder.checkImage.getVisibility() == View.GONE) {
            holder.checkImage.setVisibility(View.VISIBLE);

            holder.itemView.setBackgroundColor(Color.LTGRAY);

            selectedItems.add(shoppingListAdapterItem);

            Log.d(TAG, "ClickItem: selected items: " + selectedItems.toString());

        }else{
            holder.checkImage.setVisibility(View.GONE);
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            selectedItems.remove(shoppingListAdapterItem);

        }

        shopListItemViewModel.setFoodMutableLiveData(String.valueOf(selectedItems.size()));


    }


}


