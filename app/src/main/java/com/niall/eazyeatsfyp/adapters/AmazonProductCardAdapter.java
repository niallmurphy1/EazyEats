package com.niall.eazyeatsfyp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.niall.eazyeatsfyp.R;
import com.niall.eazyeatsfyp.zincEntities.ProductObject;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AmazonProductCardAdapter extends RecyclerView.Adapter<AmazonProductCardAdapter.ViewHolder> {

    private LayoutInflater layoutInflater;
    private List<ProductObject> productObjects;
    private ViewHolder.OnProductClickListener onProductClickListener;

    public AmazonProductCardAdapter(){

    }

    public AmazonProductCardAdapter(Context context){
        this.layoutInflater = LayoutInflater.from(context);
        productObjects = new ArrayList<>();

    }

    public List<ProductObject> getProductObjects() {
        return productObjects;
    }

    public void setProductObjects(List<ProductObject> productObjects) {
        this.productObjects = productObjects;
    }

    public ViewHolder.OnProductClickListener getOnProductClickListener() {
        return onProductClickListener;
    }

    public void setOnProductClickListener(ViewHolder.OnProductClickListener onProductClickListener) {
        this.onProductClickListener = onProductClickListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());

        View v = layoutInflater.inflate(R.layout.amazon_product_card, parent, false);

        return new ViewHolder(v, onProductClickListener);


    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.setData(productObjects.get(position));
    }

    @Override
    public int getItemCount() {
        return productObjects.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{



        private TextView productTitle;
        private TextView price;
        private ImageView productImage;
        OnProductClickListener mOnProductClickListener;


        public ViewHolder(@NonNull View itemView, OnProductClickListener mOnProductClickListener) {
            super(itemView);
            productTitle = itemView.findViewById(R.id.amazon_title_textview);
            price = itemView.findViewById(R.id.amazon_price_textview);
            productImage = itemView.findViewById(R.id.amazon_image_cardview);
            this.mOnProductClickListener = mOnProductClickListener;
            itemView.setOnClickListener(this);

        }
        public String formatPricePounds(double price){
            NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.UK);
            return formatter.format(price);

        }

        public void setData(ProductObject productObject){


            Picasso.get()
                    .load(productObject.getImage())
                    .fit()
                    .centerCrop()
                    .into(productImage);

            productTitle.setText(productObject.getTitle());

            price.setText(formatPricePounds((productObject.getPrice())/100.00));

        }
        @Override
        public void onClick(View v) {

            mOnProductClickListener.onProductClick(getAdapterPosition());
        }


        public interface OnProductClickListener{

            public void onProductClick(int position);
        }
    }
}
