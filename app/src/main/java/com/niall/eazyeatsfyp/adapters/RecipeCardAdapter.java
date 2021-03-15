package com.niall.eazyeatsfyp.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.niall.eazyeatsfyp.R;
import com.niall.eazyeatsfyp.entities.Recipe;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class RecipeCardAdapter extends RecyclerView.Adapter<RecipeCardAdapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    private ArrayList<Recipe> recipeData;
    private ViewHolder.OnRecipeListener mOnRecipeListener;






    public RecipeCardAdapter(Context context, ArrayList<Recipe> recipeData, ViewHolder.OnRecipeListener onRecipeListener){
        this.layoutInflater = LayoutInflater.from(context);
        this.recipeData = recipeData;
        this.mOnRecipeListener = onRecipeListener;


    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView recipeImage;
        public TextView recipeTitleText, recipeServingsText, recipeTimeText;
        OnRecipeListener onRecipeListener;


        public ViewHolder(@NonNull View itemView, OnRecipeListener onRecipeListener){
            super(itemView);
            recipeImage = itemView.findViewById(R.id.recipe_image_cardview);
            recipeTitleText = itemView.findViewById(R.id.recipeTextTitle);
            recipeServingsText = itemView.findViewById(R.id.servings_textview);
            recipeTimeText  = itemView.findViewById(R.id.time_textview);
            this.onRecipeListener = onRecipeListener;


            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {

            onRecipeListener.onRecipeClick(getAdapterPosition());
        }


        public interface OnRecipeListener{

            public void onRecipeClick(int position);



        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.recipe_view_rcv, parent, false);

        return new ViewHolder(view,mOnRecipeListener);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Recipe currentRecipe = recipeData.get(position);
       //holder.recipeImage.getDrawable();
        Picasso.get()
                .load(currentRecipe.getImageURI())
                .fit()
                .centerCrop()
                .into(holder.recipeImage);
        //Glide.with(holder.recipeImage.getContext()).load(currentRecipe.getImageURI()).into(holder.recipeImage);
        holder.recipeTitleText.setText(currentRecipe.getName());
        holder.recipeTimeText.setText(currentRecipe.getTime() + "mins");
        holder.recipeServingsText.setText("Servings: "+ currentRecipe.getServings());



    }



    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    public void glideImage(ImageView imageView){
         Glide.with(imageView.getContext()).load("https://spoonacular.com/recipeImages/648279-312x231.jpg").into(imageView);

    }

    @Override
    public int getItemCount() {
        return recipeData.size();
    }





}
