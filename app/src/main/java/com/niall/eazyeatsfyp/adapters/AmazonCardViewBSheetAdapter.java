package com.niall.eazyeatsfyp.adapters;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.niall.eazyeatsfyp.R;
import com.niall.eazyeatsfyp.zincEntities.ProductObject;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class AmazonCardViewBSheetAdapter extends RecyclerView.Adapter<AmazonCardViewBSheetAdapter.ViewHolder>{

    private LayoutInflater layoutInflater;
    private List<ProductObject> productObjects;

    public FirebaseAuth fAuth = FirebaseAuth.getInstance();
    public FirebaseUser fUser = fAuth.getCurrentUser();
    final String userId = fUser.getUid();
    private DatabaseReference userAmazonCartRef;

    public AmazonCardViewBSheetAdapter(){

    }

    public AmazonCardViewBSheetAdapter(Context context){
        this.layoutInflater = LayoutInflater.from(context);
        productObjects = new ArrayList<>();

    }

    public List<ProductObject> getProductObjects() {
        return productObjects;
    }

    public void setProductObjects(List<ProductObject> productObjects) {
        this.productObjects = productObjects;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View v = layoutInflater.inflate(R.layout.amazon_product_bottom_sheet_card, parent, false);
        return new ViewHolder(v);
    }

    public void addQuant(ProductObject productObject){

        userAmazonCartRef = FirebaseDatabase.getInstance().getReference("User").child(userId).child("user-amazonShoppingCart");

        userAmazonCartRef.child(productObject.getProduct_id()).child("quantity").setValue(productObject.getQuantity() +1);


    }

    public void removeQuant(ProductObject productObject){

        userAmazonCartRef = FirebaseDatabase.getInstance().getReference("User").child(userId).child("user-amazonShoppingCart");

        if(productObject.getQuantity() >1){
            userAmazonCartRef.child(productObject.getProduct_id()).child("quantity").setValue(productObject.getQuantity() -1);

        }

    }

    public void removeFromFirebase(ProductObject productObject){

        userAmazonCartRef = FirebaseDatabase.getInstance().getReference("User").child(userId).child("user-amazonShoppingCart");


        userAmazonCartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                for(DataSnapshot keyNode: snapshot.getChildren()){


                    if (keyNode.getKey().equals(productObject.getProduct_id())){

                        Log.d("TAG", "onDataChange: product to be deleted: " + userAmazonCartRef.child(keyNode.getKey()));

                        userAmazonCartRef.child(keyNode.getKey()).removeValue();


                        Log.d("TAG", "onDataChange: Item removed: " + userAmazonCartRef.child(keyNode.getKey()) );
                    }
                }

                notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setData(productObjects.get(position));

        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //handle object removed from firebase and rcv, with confirmation dialog

                Toast.makeText(v.getContext(), "Delete button clicked on : " + productObjects.get(position).getTitle(), Toast.LENGTH_SHORT).show();

                removeFromFirebase(productObjects.get(position));


            }
        });

        holder.removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //handle 1 less quantity, except in the case of quantity = 1, then call on delete method, change text view for quant and subtotal

                removeQuant(productObjects.get(position));

            }
        });

        holder.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //handle one more quantity, update Firebase and textviews
                //OR handle it after proceedToCheckout btn is clicked

                addQuant(productObjects.get(position));
            }
        });
    }


    @Override
    public int getItemCount() {
        return productObjects.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView productTitle;
        TextView productPrice;
        ImageView productImage;

        ImageButton deleteButton;
        ImageButton removeButton;
        ImageButton addButton;

        TextView productQuantText;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            productTitle = itemView.findViewById(R.id.bap_amazon_product_title_textview);
            productImage = itemView.findViewById(R.id.bap_amazon_product_image);
            productPrice = itemView.findViewById(R.id.bap_amazon_product_price_textview);

            deleteButton = itemView.findViewById(R.id.bap_delete_button_amazon_product);
            removeButton = itemView.findViewById(R.id.bap_remove_quant_button_amazon_product);
            addButton = itemView.findViewById(R.id.bap_add_quant_button_amazon_product);
            productQuantText = itemView.findViewById(R.id.bap_num_quant_product_textview);
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
            productPrice.setText(formatPricePounds((productObject.getPrice())/100.00));
            productQuantText.setText(productObject.getQuantity()+"");


        }
    }
}
