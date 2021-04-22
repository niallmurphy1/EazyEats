package com.niall.eazyeatsfyp.adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.Layout;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.niall.eazyeatsfyp.BNavigationActivity;
import com.niall.eazyeatsfyp.R;
import com.niall.eazyeatsfyp.animations.CheckBoxAnimation;
import com.niall.eazyeatsfyp.entities.Food;
import com.niall.eazyeatsfyp.fragments.MyFoodIngredientsFragment;

import java.util.ArrayList;

import static android.content.ContentValues.TAG;

public class IngredientCardAdapter extends RecyclerView.Adapter<IngredientCardAdapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    private ArrayList<Food> ingredients;
    ViewHolder.OnIngredientListener mOnIngredientListener;
    IngredientViewModel ingredientViewModel;



    private boolean isEnabled = false;
    private boolean selectAll = false;

    ArrayList<Food> selectedItems = new ArrayList<>();

    MyFoodIngredientsFragment frag;


    private ImageView checkImage;

    public IngredientCardAdapter(){

    }

    public IngredientCardAdapter(Context context, ArrayList<Food> ingredients, ViewHolder.OnIngredientListener onIngredientListener, MyFoodIngredientsFragment frag){
        this.layoutInflater = LayoutInflater.from(context);
        this.ingredients = ingredients;
        this.mOnIngredientListener = onIngredientListener;
        this.frag = frag;

    }
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private TextView ingredientName, ingredientQuantUnits;
        private ImageView checkImage;
        IngredientViewModel ingredientViewModel;

        OnIngredientListener mOnIngredientListener;

        public ViewHolder(@NonNull View itemView, OnIngredientListener mOnIngredientListener) {
            super(itemView);
            this.ingredientName = itemView.findViewById(R.id.ingredient_name_rcv);
            this.ingredientQuantUnits = itemView.findViewById(R.id.ingredient_quant_units_rcv);
            this.checkImage = itemView.findViewById(R.id.my_ingredients_image_check);
            this.mOnIngredientListener = mOnIngredientListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {

            mOnIngredientListener.onIngredientClick(getAdapterPosition());

        }

        public void setData(Food ingredient){
            ingredientName.setText(ingredient.getName());
            ingredientQuantUnits.setText(ingredient.getQuantity() + " "+  ingredient.getUnit());

        }


        public interface OnIngredientListener{

            public void onIngredientClick(int position);
        }
    }

    @NonNull
    @Override
    public IngredientCardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        //View view1  = LayoutInflater.from(parent.getContext()).inflate(R.layout.recipe_ingredient_item_rcv, parent, false);
       View view = layoutInflater.inflate(R.layout.recipe_ingredient_item_rcv, parent, false);

       ingredientViewModel = new ViewModelProvider((FragmentActivity)parent.getContext()).get(IngredientViewModel.class);


        return new ViewHolder(view, mOnIngredientListener);
    }



    @Override
    public void onBindViewHolder(@NonNull IngredientCardAdapter.ViewHolder holder, int position) {

        holder.setData(ingredients.get(position));

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(!isEnabled){
                    ActionMode.Callback callback = new ActionMode.Callback() {
                        @Override
                        public boolean onCreateActionMode(ActionMode mode, Menu menu) {

                            MenuInflater menuInflater = mode.getMenuInflater();
                            menuInflater.inflate(R.menu.multi_select_menu, menu);
                            return true;
                        }

                        @Override
                        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                            isEnabled = true;
                            ClickItem(holder);
                            ingredientViewModel.getText().observe((LifecycleOwner) holder.itemView.getContext(), new Observer<String>() {
                                @Override
                                public void onChanged(String s) {

                                    mode.setTitle(String.format("%s selected",s));
                                }
                            });
                            return true;
                        }

                        @Override
                        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                            int id = item.getItemId();
                            switch(id){
                                case R.id.menu_add_ingredient_to_recipe:
                                    //figure this out

                                    frag.showMeTheMoney(selectedItems);

                                    break;
                                case R.id.menu_delete_ingredient:

                                    //ClickItem(holder);
                                    Log.d("DELETEACTIONBTN", "onActionItemClicked: seleceted items: " + selectedItems.toString());
                                    frag.deleteIngredientsFromFirebase(selectedItems);
                                    if(ingredients.size() == 0){

                                        //create text view empty, add in constructor of adapter
                                        Toast.makeText(frag.getContext(), "You have no ingredients your inventory", Toast.LENGTH_SHORT).show();
                                        Log.d(TAG, "onActionItemClicked: no ingredients in the RCV");
                                    }
                                    mode.finish();
                                    break;
                                case R.id.menu_ingredient_select_all:

                                    if(selectedItems.size() == ingredients.size()){

                                        selectAll =false;

                                        selectedItems.clear();
                                    }else{
                                        selectAll = true;
                                        selectedItems.clear();

                                        selectedItems.addAll(ingredients);
                                    }

                                    ingredientViewModel.setFoodMutableLiveData(String.valueOf(selectedItems.size()));

                                    notifyDataSetChanged();

                                    break;
                            }
                            return true;
                        }

                        @Override
                        public void onDestroyActionMode(ActionMode mode) {

                            isEnabled = false;

                            selectAll = false;

                            notifyDataSetChanged();

                        }
                    };

                     v.startActionMode(callback);
                }else {

                    ClickItem(holder);
                }
                return true;
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isEnabled){

                    ClickItem(holder);
                }else{

                    Log.d(TAG, "onClick: You clicked: " + ingredients.get(holder.getAdapterPosition()));
                }
            }
        });

        if(selectAll){

            holder.checkImage.setVisibility(View.VISIBLE);

            holder.itemView.setBackgroundColor(Color.LTGRAY);
        }
        else{
            holder.checkImage.setVisibility(View.GONE);

            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
        }


    }

    private void ClickItem(ViewHolder holder) {

        Log.d("CLICKITEMMETHOD", "ClickItem: this item has been clicked: " + ingredients.get(holder.getAdapterPosition()));

        Food food = ingredients.get(holder.getAdapterPosition());


        if(holder.checkImage.getVisibility() == View.GONE){

            holder.checkImage.setVisibility(View.VISIBLE);

            holder.itemView.setBackgroundColor(Color.LTGRAY);

            selectedItems.add(food);

            Log.d(TAG, "ClickItem: selected items: " + selectedItems.toString() );
        }
        else{

            holder.checkImage.setVisibility(View.GONE);
            holder.itemView.setBackgroundColor(Color.TRANSPARENT);
            selectedItems.remove(food);
        }
        ingredientViewModel.setFoodMutableLiveData(String.valueOf(selectedItems.size()));
    }

    public ArrayList<Food> getSelectedItems() {
        return selectedItems;
    }

    public void setSelectedItems(ArrayList<Food> selectedItems) {
        this.selectedItems = selectedItems;
    }

    public void setIngredients(ArrayList<Food> ingredients) {
        this.ingredients = ingredients;
    }

    @Override
    public int getItemCount() {
        return ingredients.size();
    }
}
