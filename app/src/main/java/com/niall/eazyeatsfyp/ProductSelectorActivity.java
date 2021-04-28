package com.niall.eazyeatsfyp;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.niall.eazyeatsfyp.adapters.AmazonProductCardAdapter;
import com.niall.eazyeatsfyp.zincEntities.ProductObject;
import com.niall.eazyeatsfyp.zincEntities.ProductRepo;
import com.squareup.picasso.Picasso;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ProductSelectorActivity extends AppCompatActivity implements AmazonProductCardAdapter.ViewHolder.OnProductClickListener{


    public FirebaseAuth fAuth = FirebaseAuth.getInstance();
    public FirebaseUser fUser = fAuth.getCurrentUser();
    final String userId = fUser.getUid();
    private DatabaseReference userAmazonProductsRef;

    private final ProductRepo productRepo = new ProductRepo();

    public static final String TAG = "ProductSelectorActivity ---->";
    public static final String SHOPLISTITEMNAME = "shopListItemName";
    public static final String EXTRA_UPC_CODE = "EXTRA_UPC_CODE";


    private RecyclerView recyclerView;
    private AmazonProductCardAdapter adapter;


    Intent intent;
    String productName;
    private View root;


    private ArrayList<ProductObject> products;

    public static Intent getIntent(Context context, String productName, String upcCode) {
        Intent intent = new Intent(context, ProductSelectorActivity.class);
        intent.putExtra(SHOPLISTITEMNAME, productName);
        intent.putExtra(EXTRA_UPC_CODE, upcCode);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_selector);

        root = findViewById(R.id.product_selector_activity_layout);

        intent = getIntent();
        productName = intent.getStringExtra(SHOPLISTITEMNAME);

        products = new ArrayList<>();

        setUpRCV();
        //products = getAmazonProduct(productName);

        userAmazonProductsRef = FirebaseDatabase.getInstance().getReference("User").child(userId).child("user-amazonShoppingCart");

        System.out.println(products.toString());

        String upc = intent.getStringExtra(EXTRA_UPC_CODE);
        if (upc != null && !upc.isEmpty()) {
            productRepo.getAmazonProductByAsin(upc, new Callback<ProductObject>() {
                @Override
                public void onSuccess(ProductObject data) {
                    if (data == null) {
                        getAmazonProductsByName();
                        return;
                    }
                    ArrayList<ProductObject> list = new ArrayList<>();
                    list.add(data);
                    adapter.setProductObjects(list);
                    adapter.notifyDataSetChanged();
                }
                @Override
                public void onError(Throwable error) {
                    Log.e(TAG, "onError: No product found using UPC code");
                    getAmazonProductsByName();
                }
            });
        } else {
            getAmazonProductsByName();
        }

    }

    private void getAmazonProductsByName() {
        productRepo.getAmazonProducts(productName, new Callback<List<ProductObject>>() {
            @Override
            public void onSuccess(List<ProductObject> data) {

                if(data == null){
                    Toast.makeText(ProductSelectorActivity.this, "No products were found matching the selected item", Toast.LENGTH_SHORT).show();
                }
                adapter.setProductObjects(data);
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onError(Throwable error) {
                //show user error message; no product found mathching shopping list item
                Toast.makeText(ProductSelectorActivity.this, "No products were found matching the selected item", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    public void setUpRCV() {

        recyclerView = findViewById(R.id.amazon_products_rcv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AmazonProductCardAdapter(this);
        adapter.setOnProductClickListener(this);
        recyclerView.setAdapter(adapter);



    }



    @Override
    public void onProductClick(int position) {
        ProductObject selectedProduct = adapter.getProductObjects().get(position);
        System.out.println("ONPRODUCTCLICK METHOD");

        //dialogue here

        showDialog(adapter.getProductObjects().get(position));

    }

    private void showDialog(ProductObject productObject) {


        Log.d(TAG, "showDialog: product_id in showDialog" + productObject.getProduct_id());
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_amazon_confirm_item,null);
        builder.setView(dialogView);
        builder.setTitle("Product details");
        NumberPicker numberPicker = dialogView.findViewById(R.id.product_quant_number_picker);
        numberPicker.setMaxValue(99);
        numberPicker.setValue(1);
        //RatingBar ratingBar = dialogView.findViewById(R.id.rating_bar_dialog_amazon_product);
        //ratingBar.setNumStars(5);
        MaterialTextView numRatingsText = dialogView.findViewById(R.id.amazon_num_ratings_text_view);
        ImageView productImage = dialogView.findViewById(R.id.product_image_dialog_image_view);
        MaterialTextView titleText = dialogView.findViewById(R.id.product_name_dialog_text_view);
        MaterialTextView starsText = dialogView.findViewById(R.id.product_stars_dialog_text_view);
        MaterialTextView priceText = dialogView.findViewById(R.id.product_price_dialog_text_view);
        if(productObject.getImage() != null){
        Picasso.get()
                .load(productObject.getImage())
                .fit()
                .centerCrop()
                .into(productImage);
        }else {
            Picasso.get()
                    .load(getString(R.string.noImageAvailableImage))
                    .fit()
                    .centerCrop()
                    .into(productImage);
        }
        if (productObject.getTitle() != null) {
            titleText.setText(productObject.getTitle());
        }
        else{
            titleText.setText("No title available");
        }

        numRatingsText.setText("(" + productObject.getNum_reviews() + " reviews)");
        starsText.setText(productObject.getStars() + " stars");
        //ratingBar.setRating((float) productObject.getStars());

        double price = (productObject.getPrice()/100.00);

        priceText.setText("Price: " + formatPricePounds(price));

        builder.setPositiveButton("Add to cart", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                productObject.setQuantity(numberPicker.getValue());
                addToFirebase(productObject);

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();

        alertDialog.show();

    }

    public void addToFirebase(ProductObject productObject){

        HashMap newProductMap = new HashMap();
        Map<String, Object> productMap = productObject.toMap();

        Log.d(TAG, "onClick: product_id: " + productObject.getProduct_id());
        newProductMap.put(productObject.getProduct_id().toString(), productMap);
        userAmazonProductsRef.updateChildren(newProductMap).addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object o) {

                Log.d(TAG, "onSuccess: " + newProductMap.toString() + " added!");
                Snackbar.make(root, productObject.getTitle() + " added to cart!", Snackbar.LENGTH_SHORT).show();
            }
        });

    }


    public String formatPricePounds(double price){
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.UK);
         return formatter.format(price);

    }


}