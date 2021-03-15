package com.niall.eazyeatsfyp.zincEntities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.niall.eazyeatsfyp.Callback;
import com.niall.eazyeatsfyp.R;

import java.util.ArrayList;
import java.util.List;

public class OrderDetailsActivity extends AppCompatActivity {


    //order variables
    private Order order;
    private RetailerCreds retailer_creds;
    private Shipping shipping_object;
    private Address shipping_address;
    private Address billing_address;
    private PaymentMethod payment_method;
    private boolean is_gift;
    private double max_price;

    private OrderRepo orderRepo = new OrderRepo();

    private List<ProductObject> productObjects;

    //Text fields etc.

    //shipping_address
    private EditText firstNameEdit;
    private EditText lastNameEdit;
    private EditText address1ShippingEdit;
    private EditText address2ShippingEdit;
    private EditText zipShippingEdit;
    private EditText cityShippingEdit;
    private EditText stateShippingEdit;
    private EditText countryCodeShippingEdit;
    private EditText phoneNoShippingEdit;

    private MaterialCheckBox sameAsShippingCheckbox;

    //billing_address
    private EditText firstNameBillingEdit;
    private EditText lastNameBillingEdit;
    private EditText address1BillingEdit;
    private EditText address2BillingEdit;
    private EditText zipBillingEdit;
    private EditText cityBillingEdit;
    private EditText stateBillingEdit;
    private EditText countryCodeBillingEdit;
    private EditText phoneNoBillingEdit;

    //shipping_method
    private EditText orderByShippingEdit;
    private EditText maxDaysShippingEdit;
    private EditText maxPriceShippingEdit;

    //payment_method
    private EditText nameOnCardEdit;
    private EditText cardNumberEdit;
    private EditText securityCodeEdit;
    private EditText expirationMonthEdit;
    private EditText expirationYearEdit;
    private MaterialCheckBox useGiftCheckbox;

    private EditText maxPriceEdit;
    private Button confirmAndPayButton;


    //Firebase variables
    public FirebaseAuth fAuth = FirebaseAuth.getInstance();
    public FirebaseUser fUser = fAuth.getCurrentUser();
    final String userId = fUser.getUid();
    private DatabaseReference userAmazonCartRef;
    private DatabaseReference userAmazonCreds;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        //initialise variables
        initFields();

        //assign required variables to create an order object

        userAmazonCartRef  = FirebaseDatabase.getInstance().getReference().child("User").child(userId).child("user-amazonShoppingCart");
        userAmazonCreds = FirebaseDatabase.getInstance().getReference().child("User").child(userId).child("user-amazonCreds");


        userAmazonCartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                productObjects = new ArrayList<>();

                for(DataSnapshot keyNode: snapshot.getChildren()){

                    ProductObject productObject = keyNode.getValue(ProductObject.class);

                    productObjects.add(productObject);
                }

                Log.d("TAG", "onDataChange: Products:::: " + productObjects.toString());

                confirmAndPayButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        sameAsBilling();
                        assignVariables();


                    }
                });


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void initFields(){

        //initShipping
        firstNameEdit = findViewById(R.id.order_details_first_name_text);
        lastNameEdit = findViewById(R.id.order_details_last_name_text);
        address1ShippingEdit = findViewById(R.id.order_details_address1_text);
        address2ShippingEdit = findViewById(R.id.order_details_address2_text);
        zipShippingEdit = findViewById(R.id.order_details_zip_text);
        cityShippingEdit = findViewById(R.id.order_details_city_text);
        stateShippingEdit = findViewById(R.id.order_details_state_text);
        countryCodeShippingEdit = findViewById(R.id.order_details_country_iso_text);
        phoneNoShippingEdit = findViewById(R.id.oder_details_phone_number_text);

        //initBilling
        firstNameBillingEdit = findViewById(R.id.order_details_first_name_billing_text);
        lastNameBillingEdit = findViewById(R.id.order_details_last_name_billing_text);
        address1BillingEdit = findViewById(R.id.order_details_address1_billing_text);
        address2BillingEdit = findViewById(R.id.order_details_address2_billing_text);
        zipBillingEdit = findViewById(R.id.order_details_zip_billing_text);
        cityBillingEdit = findViewById(R.id.order_details_city_billing_text);
        stateBillingEdit = findViewById(R.id.order_details_state_billing_text);
        countryCodeBillingEdit = findViewById(R.id.order_details_country_iso_billing_text);
        phoneNoBillingEdit = findViewById(R.id.oder_details_phone_number_billing_text);

        sameAsShippingCheckbox = findViewById(R.id.order_details_same_as_shipping_checkbox);

        //shippingPrefs
        orderByShippingEdit = findViewById(R.id.order_details_order_by_text);
        maxDaysShippingEdit = findViewById(R.id.order_details_max_days_text);
        maxPriceShippingEdit = findViewById(R.id.order_details_max_price_shipping_text);

        //initPaymentMethod
        nameOnCardEdit = findViewById(R.id.order_details_payment_name_on_card_text);
        cardNumberEdit = findViewById(R.id.oder_details_card_number_text);
        securityCodeEdit = findViewById(R.id.oder_details_security_code_text);
        expirationMonthEdit = findViewById(R.id.oder_details_expiration_month_text);
        expirationYearEdit = findViewById(R.id.oder_details_expiration_year_text);
        useGiftCheckbox = findViewById(R.id.order_details_use_gift_checkbox);

        maxPriceEdit = findViewById(R.id.order_details_max_price_text);

        confirmAndPayButton = findViewById(R.id.order_details_confirm_and_pay_button);


    }

    public void assignVariables() {

        Intent intent = getIntent();

        //retailer_creds
        String retailerEmail = intent.getStringExtra(AmazonCredsActivity.AMAZONCREDSEMAIL);
        String retailerPassword = intent.getStringExtra(AmazonCredsActivity.AMAZONCREDSPASSWORD);
        retailer_creds = new RetailerCreds(retailerEmail, retailerPassword);

        //shipping address object
        String firstNameShip = firstNameEdit.getText().toString().trim();
        String lastNameShip = lastNameEdit.getText().toString().trim();
        String address1Ship = address1ShippingEdit.getText().toString().trim();
        String address2Ship = address2ShippingEdit.getText().toString().trim();
        String zipShipping = zipShippingEdit.getText().toString().trim();
        String cityShipping = cityShippingEdit.getText().toString().trim();
        String stateShipping = stateShippingEdit.getText().toString().trim();
        String isoCountryCode = countryCodeShippingEdit.getText().toString().trim();
        String phoneNoShipping = phoneNoShippingEdit.getText().toString().trim();
        shipping_address = new Address(firstNameShip
                , lastNameShip
                , address1Ship
                , address2Ship
                , zipShipping
                , cityShipping
                , stateShipping
                , isoCountryCode
                , phoneNoShipping);


        //billing address object
        String firstNameBill = firstNameBillingEdit.getText().toString().trim();
        String lastNameBill = lastNameBillingEdit.getText().toString().trim();
        String address1Bill = address1BillingEdit.getText().toString().trim();
        String address2Bill = address2BillingEdit.getText().toString().trim();
        String zipBill = zipBillingEdit.getText().toString().trim();
        String cityBill = cityBillingEdit.getText().toString().trim();
        String stateBill = stateBillingEdit.getText().toString().trim();
        String isoCountryCodeBill = countryCodeBillingEdit.getText().toString().trim();
        String phoneNoBill = phoneNoBillingEdit.getText().toString().trim();
         billing_address = new Address(firstNameBill
                , lastNameBill
                , address1Bill
                , address2Bill
                , zipBill
                , cityBill
                , stateBill
                , isoCountryCodeBill
                , phoneNoBill);


         //payment_method
        payment_method = new PaymentMethod(nameOnCardEdit.getText().toString().trim()
                , cardNumberEdit.getText().toString().trim()
                , securityCodeEdit.getText().toString().trim()
                , Integer.parseInt(expirationMonthEdit.getText().toString().trim())
                , Integer.parseInt(expirationYearEdit.getText().toString().trim())
                , useGiftCheckbox.isChecked());

        //shipping
        shipping_object = new Shipping();
        shipping_object.setOrder_by(orderByShippingEdit.getText().toString().trim());
        shipping_object.setMax_days(Integer.parseInt(maxDaysShippingEdit.getText().toString().trim()));
        shipping_object.setMax_price(Double.parseDouble(maxPriceShippingEdit.getText().toString().trim()));

        is_gift = useGiftCheckbox.isChecked();

        max_price = Double.parseDouble(maxPriceEdit.getText().toString().trim());

        List<ProductObject> amazonProducts = new ArrayList<>();

        //Convert objects to objects for the api to use
        for(ProductObject productObject: productObjects){

            ProductObject product = new ProductObject(productObject.getProduct_id(), productObject.getQuantity());

            amazonProducts.add(product);
        }

        order = new Order("amazon_uk"
                , amazonProducts
                , shipping_address
                , shipping_object
                , billing_address
                , payment_method
                , retailer_creds
                , is_gift
                , max_price);


        Log.d("TAG", "assignVariables: Here is the Order: " + order.toString());


        orderRepo.createOrder(order, new Callback<String>() {
            @Override
            public void onSuccess(String data) {

                //TODO: get order working
                Log.d("TAG", "onSuccess: The string for order confirmation " + data);


            }

            @Override
            public void onError(Throwable error) {


                Log.e("TAG", "onError: There was an error: "+ error);
            }
        });



    }

    public void sameAsBilling(){

        if(sameAsShippingCheckbox.isChecked()){

            firstNameBillingEdit.setText(firstNameEdit.getText().toString());
            lastNameBillingEdit.setText(lastNameEdit.getText().toString());
            address1BillingEdit.setText(address1ShippingEdit.getText().toString());
            address2BillingEdit.setText(address2ShippingEdit.getText().toString());
            zipBillingEdit.setText(zipShippingEdit.getText().toString());
            cityBillingEdit.setText(cityShippingEdit.getText().toString());
            stateBillingEdit.setText(stateShippingEdit.getText().toString());
            countryCodeBillingEdit.setText(countryCodeShippingEdit.getText().toString());
            phoneNoBillingEdit.setText(phoneNoShippingEdit.getText().toString());

        }
        else {
            firstNameBillingEdit.setText("");
            lastNameBillingEdit.setText("");
            address1BillingEdit.setText("");
            address2BillingEdit.setText("");
            zipBillingEdit.setText("");
            cityBillingEdit.setText("");
            stateBillingEdit.setText("");
            countryCodeBillingEdit.setText("");
            phoneNoBillingEdit.setText("");
        }
    }


}