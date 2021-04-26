package com.niall.eazyeatsfyp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.niall.eazyeatsfyp.BNavigationActivity;
import com.niall.eazyeatsfyp.R;
import com.niall.eazyeatsfyp.entities.Food;
import com.niall.eazyeatsfyp.entities.ShoppingListItem;
import com.niall.eazyeatsfyp.fragments.MyFoodIngredientsFragment;

import java.util.ArrayList;

public class MyIngredientsAdapter extends RecyclerView.Adapter<MyIngredientsAdapter.ViewHolder>{

    private LayoutInflater layoutInflater;
    private ArrayList<Food> myIngredientsData;
    private ViewHolder.OnMyIngredientListener mOnMyIngredientListener;

    private BNavigationActivity activity;


    public MyIngredientsAdapter(){

    }
    public MyIngredientsAdapter(Context context, ViewHolder.OnMyIngredientListener onMyIngredientListener){
        this.layoutInflater = LayoutInflater.from(context);
        this.mOnMyIngredientListener = onMyIngredientListener;
       // this.activity = (BNavigationActivity)(context);

    }

    public void setmOnMyIngredientListener(ViewHolder.OnMyIngredientListener mOnMyIngredientListener) {
        this.mOnMyIngredientListener = mOnMyIngredientListener;
    }

    public void setMyIngredientsData(ArrayList<Food> myIngredientsData) {
        this.myIngredientsData = myIngredientsData;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = layoutInflater.inflate(R.layout.ingredient_rcv_no_check, parent, false);

        return new ViewHolder(view, mOnMyIngredientListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(myIngredientsData.get(position));

    }

    @Override
    public int getItemCount() {
        return myIngredientsData.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{


        private TextView ingredientName, ingredientQuantUnit;
        OnMyIngredientListener myIngredientListener;

        public ViewHolder(@NonNull View itemView, OnMyIngredientListener onMyIngredientListener ) {
            super(itemView);
            this.ingredientName = itemView.findViewById(R.id.ingredient_name_rcv_no_check);
            this.ingredientQuantUnit = itemView.findViewById(R.id.ingredient_quant_units_rcv_no_check);
            this.myIngredientListener = onMyIngredientListener;
        }

        public void setData(Food foodItem){

            System.out.println("These are the ingredients::: "+ foodItem.toString());
            System.out.println("Food item name::: " + foodItem.getName());

            ingredientName.setText(foodItem.getName());

            ingredientQuantUnit.setText(foodItem.getQuantity() + " " + foodItem.getUnit());
            //TODO: get rid of listener, no use
            itemView.setOnClickListener(v -> myIngredientListener.onIngredientClick(foodItem));
        }

        public interface OnMyIngredientListener{

            public void onIngredientClick(Food ingredient);
        }
    }


}




