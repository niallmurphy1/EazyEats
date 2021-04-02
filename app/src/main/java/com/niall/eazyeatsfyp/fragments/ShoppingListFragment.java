package com.niall.eazyeatsfyp.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.StringRequestListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.niall.eazyeatsfyp.R;
import com.niall.eazyeatsfyp.adapters.AmazonCardViewBSheetAdapter;
import com.niall.eazyeatsfyp.adapters.ChildShopListRecyclerAdapter;
import com.niall.eazyeatsfyp.adapters.MainShopListTestAdapter;
import com.niall.eazyeatsfyp.adapters.ShoppingListItemAdapter;
import com.niall.eazyeatsfyp.barcode.Scanner;
import com.niall.eazyeatsfyp.entities.ShopListCategory;
import com.niall.eazyeatsfyp.entities.ShoppingListItem;
import com.niall.eazyeatsfyp.zincEntities.AmazonCredsActivity;
import com.niall.eazyeatsfyp.zincEntities.ProductObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ShoppingListFragment extends Fragment implements ChildShopListRecyclerAdapter.ViewHolder.OnShopListener {


    public MainShopListTestAdapter mainAdapter;
    public RecyclerView mainRecycler;
    ArrayList<ShopListCategory> shopListCategories = new ArrayList<>();


    public RecyclerView productRecycler;
    public AmazonCardViewBSheetAdapter productViewAdapter;

    ShoppingListItemAdapter adapter = new ShoppingListItemAdapter();
    public ArrayList<ShoppingListItem> shoppingListItems = new ArrayList<>();
    public ArrayList<ShopListCategory> categories;
    private List<ProductObject> productObjects = new ArrayList<>();
    private ArrayList<String> foodIds = new ArrayList<>();
    private Intent scanIntent;

    private LinearLayout headerLayout;
    private ConstraintLayout bottomSheetConstraint;
    private ImageView headerArrowImage;
    private BottomSheetBehavior bottomSheetBehavior;
    private Button proceedToCheckoutButton;

    private TextView quantTotalText;
    private TextView subtotalText;

    private Intent checkoutIntent;

    public FirebaseAuth fAuth = FirebaseAuth.getInstance();
    public FirebaseUser fUser = fAuth.getCurrentUser();
    final String userId = fUser.getUid();
    private DatabaseReference userShopList;
    private DatabaseReference userAmazonCartRef;

    public Map newShopListItem = new HashMap();


    private FloatingActionButton addItem;
    private FloatingActionButton scanBtn;


    private EditText dialogTextName;

    private ShoppingListItem shopListAddedItem;

    private boolean duplicate;



    public ShoppingListFragment() {
        // Required empty public constructor
    }


    public static ShoppingListFragment newInstance(String param1, String param2) {
        ShoppingListFragment fragment = new ShoppingListFragment();
        Bundle args = new Bundle();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        scanIntent = new Intent(getContext(), Scanner.class);
        userShopList = FirebaseDatabase.getInstance().getReference("User").child(userId).child("user-shoppinglist");
        userAmazonCartRef = FirebaseDatabase.getInstance().getReference("User").child(userId).child("user-amazonShoppingCart");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        shoppingListItems.clear();

        return inflater.inflate(R.layout.fragment_shopping_list, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        quantTotalText = view.findViewById(R.id.bap_sheet_total_items_text_view);
        subtotalText = view.findViewById(R.id.bap_subtotal_textview);

        proceedToCheckoutButton = view.findViewById(R.id.bap_proceed_to_checkout_button);

        checkoutIntent = new Intent(getContext(), AmazonCredsActivity.class);


        proceedToCheckoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                onCheckoutClick();
            }
        });

        //set up bottom sheet
        bottomSheetConstraint = view.findViewById(R.id.bottom_sheet_product_cart);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetConstraint);
        proceedToCheckoutButton = view.findViewById(R.id.bap_proceed_to_checkout_button);
        headerLayout = view.findViewById(R.id.header_layout_shop_cart);
        headerArrowImage = view.findViewById(R.id.bap_sheet_shop_cart_arrow);

        bottomSheetBehavior.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                //do stuff
                switch(newState){


                    case BottomSheetBehavior.STATE_EXPANDED:
                        scanBtn.setVisibility(View.INVISIBLE);
                        addItem.setVisibility(View.INVISIBLE);
                        break;


                    case BottomSheetBehavior.STATE_COLLAPSED:
                        scanBtn.setVisibility(View.VISIBLE);
                        addItem.setVisibility(View.VISIBLE);
                        break;
                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                headerArrowImage.setRotation(180 * slideOffset);

            }
        });

        headerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(bottomSheetBehavior.getState() != BottomSheetBehavior.STATE_EXPANDED){
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                }else{
                    bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

        scanBtn = view.findViewById(R.id.fabScan);
        addItem = view.findViewById(R.id.addToShopListBtn);

        scanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onScanClick();
            }
        });

        addItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onAddShopListItemClick();
            }
        });

        shoppingListItems.clear();

        getShoppingListFromFirebase();

        retrieveProductsFromFirebase();

        productRecycler = view.findViewById(R.id.bap_amazon_product_cart_rcv);
        productViewAdapter = new AmazonCardViewBSheetAdapter(getContext());
        productRecycler.setAdapter(productViewAdapter);
        productRecycler.setLayoutManager(new LinearLayoutManager(getContext()));


        mainRecycler = view.findViewById(R.id.shopList_rcv);
        mainAdapter = new MainShopListTestAdapter(shopListCategories);
        mainRecycler.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        mainRecycler.setAdapter(mainAdapter);



    }

    public void onCheckoutClick(){

        startActivity(checkoutIntent);
    }

    public void setProductTotals(ArrayList<ProductObject> productObjects){

        int quantCount = 0;
        double subtotal = 0.00;
        for(ProductObject product: productObjects){

            quantCount += product.getQuantity();
            subtotal += (product.getPrice() * product.getQuantity());

        }

        quantTotalText.setText("Total item(s): " + quantCount);

        subtotal = subtotal/100.00;

        subtotalText.setText("Subtotal: " + formatPricePounds(subtotal));


    }

    public String formatPricePounds(double price){
        NumberFormat formatter = NumberFormat.getCurrencyInstance(Locale.UK);
        return formatter.format(price);

    }

    public void retrieveProductsFromFirebase(){

       //productObjects.clear();

        userAmazonCartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                productObjects.clear();
                for(DataSnapshot keyNode: snapshot.getChildren()){
                    ProductObject productObject = keyNode.getValue(ProductObject.class);

                    Log.d("retrieveProductsFromFirebase", "onDataChange: keyNode.toString():  " + keyNode.toString());

                    productObjects.add(productObject);

                }
                Log.d("TAG", "onDataChange: product objects::: " + productObjects.toString());


                productViewAdapter.setProductObjects(productObjects);
                productViewAdapter.notifyDataSetChanged();
                setProductTotals((ArrayList<ProductObject>) productObjects);

                Log.d("TAG", "onDataChange: the products from adapter: " + productViewAdapter.getProductObjects().toString());


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    //TODO: figure out how to access delete/add/remove buttons etc.



    public void getShoppingListFromFirebase(){

        shoppingListItems.clear();

        ArrayList<String> categoryNames = new ArrayList<>();

        userShopList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                shoppingListItems.clear();
                shopListCategories.clear();

                for(DataSnapshot keyNode: snapshot.getChildren()){
                    foodIds.add(keyNode.getKey());
                    ShoppingListItem shoppingListItem = keyNode.getValue(ShoppingListItem.class);
                    shoppingListItems.add(shoppingListItem);

                    String categoryName = shoppingListItem.getCategory();
                    categoryNames.add(categoryName);
                }

                removeDuplicates(categoryNames);
                System.out.println("Cat Names w/out doops : " + categoryNames);





                for(String catName: categoryNames){

                    ShopListCategory sListCat = new ShopListCategory(catName);
                    shopListCategories.add(sListCat);

                }

                for(ShopListCategory shopListCategory: shopListCategories){

                    ArrayList<ShoppingListItem> items = new ArrayList<>();

                    shopListCategory.setItems(items);

                    for(ShoppingListItem shoppingListItem: shoppingListItems){

                        if(shopListCategory.getName().equalsIgnoreCase(shoppingListItem.getCategory())){

                           items.add(shoppingListItem);

                        }
                    }
                }

                Log.d("TAG", "onDataChange: These are the shopping list category names and their item: " + shopListCategories.toString());


                System.out.println("Shopping list items: but some are null for no reason "+shoppingListItems.toString());

                //TODO: items added from dialog are messed up


                mainAdapter.setShopListCategories(shopListCategories);

                mainAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }


    private void removeDuplicates(ArrayList<String> strings) {


        if(strings != null) {
            LinkedHashSet<String> set = new LinkedHashSet<>(strings);
            strings.clear();
            strings.addAll(set);
        }


    }


    public void onScanClick(){
        startActivity(scanIntent);
    }


    public void onAddShopListItemClick(){


        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_new_shop_list_item,null);
        builder.setView(dialogView);
        builder.setTitle("New Shopping list item");
        builder.setPositiveButton("Add", (dialog, which) -> {


            dialogTextName = dialogView.findViewById(R.id.dialog_shop_list_item_name);

            System.out.println("Dialog edit text name: " + dialogTextName.getText());


            AndroidNetworking.get("https://api.spoonacular.com/food/ingredients/search?query="+dialogTextName.getText().toString().trim()+"&apiKey=c029b15f6c654e36beba722a71295883&metaInformation=true&number=1")
                    .build().getAsString(new StringRequestListener() {
                @Override
                public void onResponse(String response) {

                    Log.d("TAG", "API works:" + response);
                    try {

                        Log.d("TAG", "API working : " );
                        JSONObject results = new JSONObject(response);

                        JSONArray arr = results.getJSONArray("results");

                        String id = arr.getJSONObject(0).getString("id");
                        String name = arr.getJSONObject(0).getString("name");
                        String category = arr.getJSONObject(0).getString("aisle");


                        Log.d("API shopListItem details ", "ID : " + id + " name: " + name + " aisle: " + category);

                        shopListAddedItem = new ShoppingListItem(id, name, category);

                        for(int i = 0; i < shoppingListItems.size(); i++){

                            if( shopListAddedItem.getName().equalsIgnoreCase(shoppingListItems.get(i).getName())){

                                Toast.makeText(getActivity(), shopListAddedItem.getName()+" is already in your Shopping List!", Toast.LENGTH_SHORT).show();

                                duplicate = true;
                            }
                        }

                        if(!duplicate) {

                            Log.d("TAG", "SHOPLISTADDEDITEM: " + shopListAddedItem.getName() +shopListAddedItem.getCategory()+ shopListAddedItem.getsId());

                            Map<String, Object> shoppingListItemMap = shopListAddedItem.toMap();
                            newShopListItem.put(shopListAddedItem.getsId(), shoppingListItemMap);
                            userShopList.updateChildren(newShopListItem).addOnSuccessListener(new OnSuccessListener() {
                                @Override
                                public void onSuccess(Object o) {
                                    Toast.makeText(getContext(), shopListAddedItem.getName().toString()+ " added to list", Toast.LENGTH_SHORT).show();
                                    //shoppingListItems.clear();
                                    //getShoppingListFromFirebase();

                                }
                            });



                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

                @Override
                public void onError(ANError anError) {

                    Log.d("TAG", "API dialog error : " + anError);
                }
            });


        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        AlertDialog alertDialog = builder.create();

        alertDialog.show();

    }




    @Override
    public void onShopListItemClick(View v, ShoppingListItem item) {

    }


}