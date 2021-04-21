package com.niall.eazyeatsfyp.barcode;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.niall.eazyeatsfyp.R;
import com.niall.eazyeatsfyp.RecipeViewerActivity;
import com.niall.eazyeatsfyp.entities.ShoppingListItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.niall.eazyeatsfyp.util.Constants.BARCODE_SEARCH;
import static com.niall.eazyeatsfyp.util.Constants.SP_APIKEY;

public class BarcodeResultActivity extends AppCompatActivity {


    private TextView barcodeTextView;
    ShoppingListItem scannedItem;

    public FirebaseAuth fAuth = FirebaseAuth.getInstance();
    public FirebaseUser fUser = fAuth.getCurrentUser();
    final String userId = fUser.getUid();

    private ArrayList<ShoppingListItem> shoppingListItems = new ArrayList<>();
    private DatabaseReference userShopList  = FirebaseDatabase.getInstance().getReference("User").child(userId).child("user-shoppinglist");

    private boolean duplicate = false;

    private View root;

    public Map newShopListItem = new HashMap();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_barcode_result);


        getShoppingListItemsFromFirebase();

        root = findViewById(R.id.barcode_result_activity_layout);

        barcodeTextView = findViewById(R.id.textView_barcode);
        Intent intent = getIntent();
        String upcBarcode = intent.getStringExtra(CameraActivity.UPC_BARCODE);

        barcodeTextView.setText(upcBarcode);





        Toast.makeText(this, "this barcode " + upcBarcode, Toast.LENGTH_SHORT).show();

        AndroidNetworking.get("https://api.spoonacular.com/food/products/upc/" + upcBarcode + "?apiKey=c029b15f6c654e36beba722a71295883").build().getAsString(new StringRequestListener() {
            @Override
            public void onResponse(String response) {

                Log.d("BarcodeResultActivityAPI", "onResponse: " + response);
                System.out.println(response);

                Toast.makeText(BarcodeResultActivity.this, response, Toast.LENGTH_SHORT).show();


                String jSonString  = response;

                try {
                    JSONObject obj = new JSONObject(jSonString);


                    String name = obj.getString("title");

                    String id = obj.getString("id");

                    String  cat;

                    if(obj.getString("aisle") != null || !obj.getString("aisle").equalsIgnoreCase("null")){

                         cat = obj.getString("aisle");

                        Log.d("TAG", "onResponse: string from aisle !null or !.equalsIgnoreCase(null) : " + cat);
                    }
                    else if(obj.getString("aisle").equalsIgnoreCase("null") || obj.getString("aisle") == null){
                         cat = obj.getJSONArray("breadcrumbs").get(0).toString();
                        Log.d("TAG", "onResponse: string from breadcrumbs !null or !.equalsIgnoreCase(null) : " + cat);

                    }
                    else{
                        cat = "Other";

                        Log.d("TAG", "onResponse: cat is other: " + cat);
                    }

                    if(cat.equalsIgnoreCase("null")){
                        cat = "Other";
                    }


                    Snackbar.make(root, "Name of grocery " + name, Snackbar.LENGTH_SHORT).show();

                    scannedItem = new ShoppingListItem(id, name, cat);
                    scannedItem.setBarcodeUPC(upcBarcode);



                    newShopListItem.put(scannedItem.getsId(), scannedItem);


                    checkDuplicateAndAdd();



                } catch (JSONException e) {
                    e.printStackTrace();
                }




            }

            @Override
            public void onError(ANError anError) {

                Log.d("BarcodeResultActivityAPI", "onError: " + anError);
            }
        });
    }


    private void checkDuplicateAndAdd(){


        for(ShoppingListItem shopListItem : shoppingListItems){


            //Method works, but for some null items that are added with manual FAB button in ShoppingListFragment

            if(shopListItem.getsId() == null){
                Log.d("NULL ITEM", "checkDuplicateAndAdd: Null item name " + shopListItem.getName());
            }
            else if(shopListItem.getsId().equalsIgnoreCase(scannedItem.getsId())){

                duplicate=true;
            }


        }

        //TODO: figure out why adding works here and not in dialog

        if(!duplicate) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("User");
            userRef.child(userId).child("user-shoppinglist").updateChildren(newShopListItem).addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {

                    Snackbar.make(root, scannedItem.getName() + " added to shopping list!", Snackbar.LENGTH_SHORT).show();

                    finish();
                }
            });
        }else{
            Toast.makeText(BarcodeResultActivity.this, scannedItem.getName() + " is already in your shopping list!", Toast.LENGTH_SHORT).show();
        }
    }

    private void getShoppingListItemsFromFirebase() {

        userShopList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                shoppingListItems.clear();

                for(DataSnapshot keyNode: snapshot.getChildren()){

                    ShoppingListItem shoppingListItem = keyNode.getValue(ShoppingListItem.class);
                    shoppingListItems.add(shoppingListItem);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

                Log.d("GetShoppingListItemsFromFirebase", "onCancelled: " + error);
            }
        });
    }
}