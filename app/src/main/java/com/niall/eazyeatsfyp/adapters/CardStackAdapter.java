package com.niall.eazyeatsfyp.adapters;



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.niall.eazyeatsfyp.R;
import com.niall.eazyeatsfyp.entities.Recipe;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class CardStackAdapter extends RecyclerView.Adapter<CardStackAdapter.ViewHolder>{

    private List<Recipe> recipes;
    private ViewHolder.OnCardClickListener mOnCardLickListener;

    public CardStackAdapter(){
        recipes = new ArrayList<>();

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater= LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.recipe_card, parent, false);
        return new ViewHolder(view, mOnCardLickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.setData(recipes.get(position));
    }

    @Override
    public int getItemCount() {
        return recipes.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        ImageView image;
        TextView name, time, servings;

        OnCardClickListener mOnCardClickListener;


        public ViewHolder(@NonNull View itemView, OnCardClickListener mOnCardClickListener) {
            super(itemView);
            image = itemView.findViewById(R.id.recipe_image_swiper);
            name = itemView.findViewById(R.id.recipe_name_swiper);
            time = itemView.findViewById(R.id.recipe_time_swiper);
            servings = itemView.findViewById(R.id.recipe_servings_swiper);
            this.mOnCardClickListener = mOnCardClickListener;

            itemView.setOnClickListener(this);
        }

        public void setData(Recipe data) {

            Picasso.get()
                    .load(data.getImageURI())
                    .fit()
                    .centerCrop()
                    .into(image);
            name.setText(data.getName());
            time.setText(data.getTime() + " mins.");
            servings.setText("Serves " + data.getServings());

        }

        @Override
        public void onClick(View v) {
            mOnCardClickListener.onCardClick(getAdapterPosition());
        }

        public interface OnCardClickListener{

            public void onCardClick(int position);
        }
    }


    public List<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }

    public void addRecipes(List<Recipe> recipes) {
        this.recipes.addAll(recipes);
    }

    public ViewHolder.OnCardClickListener getmOnCardLickListener() {
        return mOnCardLickListener;
    }

    public void setmOnCardLickListener(ViewHolder.OnCardClickListener mOnCardLickListener) {
        this.mOnCardLickListener = mOnCardLickListener;
    }
}
