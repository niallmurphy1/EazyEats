package com.niall.eazyeatsfyp.zincActivities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.niall.eazyeatsfyp.BNavigationActivity;
import com.niall.eazyeatsfyp.Callback;
import com.niall.eazyeatsfyp.R;
import com.niall.eazyeatsfyp.fragments.ShoppingListFragment;
import com.niall.eazyeatsfyp.zincEntities.Address;
import com.niall.eazyeatsfyp.zincEntities.Order;
import com.niall.eazyeatsfyp.zincEntities.OrderRepo;
import com.niall.eazyeatsfyp.zincEntities.OrderResponse;
import com.niall.eazyeatsfyp.zincEntities.PaymentMethod;
import com.niall.eazyeatsfyp.zincEntities.ProductObject;
import com.niall.eazyeatsfyp.zincEntities.RetailerCreds;
import com.niall.eazyeatsfyp.zincEntities.Shipping;
import com.niall.eazyeatsfyp.zincEntities.UserAmazonDetailsWriter;
import com.squareup.picasso.Picasso;

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

    private View root;

    private OrderRepo orderRepo = new OrderRepo();

    private UserAmazonDetailsWriter userAmazonDetailsWriter = new UserAmazonDetailsWriter();

    private List<ProductObject> productObjects;

    private Address addressFromFirebase;
    boolean useFirebaseAddress;

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

    //private MaterialCheckBox sameAsShippingCheckbox;

    //private MaterialCheckBox useSavedAddressCheckBox;

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

    private DatabaseReference userAddressRef;

    private OrderStatusViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        //initialise variables
        initFields();

        initViewModel();
        observeOrderStatus();
        observeLoadingStatus();

        root = findViewById(R.id.order_details_layout);

        //assign required variables to create an order object
        userAmazonCartRef = FirebaseDatabase.getInstance().getReference().child("User").child(userId).child("user-amazonShoppingCart");
        userAmazonCreds = FirebaseDatabase.getInstance().getReference().child("User").child(userId).child("user-amazonCreds");

        userAddressRef = FirebaseDatabase.getInstance().getReference("User").child(userId).child("user-shippingAddress");
        addressFromFirebase = new Address();

        getAddressFromFirebase();

        userAmazonCartRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                productObjects = new ArrayList<>();

                for (DataSnapshot keyNode : snapshot.getChildren()) {

                    ProductObject productObject = keyNode.getValue(ProductObject.class);

                    productObjects.add(productObject);
                }

                Log.d("TAG", "onDataChange: Products:::: " + productObjects.toString());

                confirmAndPayButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //sameAsBilling();
                        assignVariables();
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    public void initFields() {

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

        //sameAsShippingCheckbox = findViewById(R.id.order_details_same_as_shipping_checkbox);

        //useSavedAddressCheckBox = findViewById(R.id.use_saved_shipping_details_checkbox);


        //initPaymentMethod
        nameOnCardEdit = findViewById(R.id.order_details_payment_name_on_card_text);
        cardNumberEdit = findViewById(R.id.oder_details_card_number_text);
        securityCodeEdit = findViewById(R.id.oder_details_security_code_text);
        expirationMonthEdit = findViewById(R.id.oder_details_expiration_month_text);
        expirationYearEdit = findViewById(R.id.oder_details_expiration_year_text);
        useGiftCheckbox = findViewById(R.id.order_details_use_gift_checkbox);


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
        shipping_object = new Shipping("price", 10, 0);

        is_gift = useGiftCheckbox.isChecked();

        max_price = 0;

        List<ProductObject> amazonProducts = new ArrayList<>();

        //Convert objects to objects for the api to use
        for (ProductObject productObject : productObjects) {

            ProductObject product = new ProductObject(productObject.getProduct_id(), productObject.getQuantity());

            amazonProducts.add(product);
        }

        if (useFirebaseAddress) {
            order = new Order("amazon_uk"
                    , amazonProducts
                    , addressFromFirebase
                    , shipping_object
                    , addressFromFirebase
                    , payment_method
                    , retailer_creds
                    , is_gift
                    , max_price);

        } else {
            order = new Order("amazon_uk"
                    , amazonProducts
                    , shipping_address
                    , shipping_object
                    , billing_address
                    , payment_method
                    , retailer_creds
                    , is_gift
                    , max_price);
        }

        Log.d("TAG", "assignVariables: Here is the Order: " + order.toString());


        Log.d(OrderDetailsActivity.class.getSimpleName()
                , "assignVariables: user address ref: " + userAddressRef.toString()
                        + " user order shipping address: " + order.getShipping_address());

        userAmazonDetailsWriter.writeAddress(userAddressRef, order.getShipping_address());


        orderRepo.createOrder(order, new Callback<OrderResponse>() {
            @Override
            public void onSuccess(OrderResponse orderResponse) {
                Log.d("TAG", "onSuccess: The string for order confirmation " + orderResponse);
                viewModel.pollOrderStatus(orderResponse.request_id);
            }

            @Override
            public void onError(Throwable error) {

                Log.e("TAG", "onError: There was an error: " + error);
            }
        });

    }

//    public void sameAsBilling() {
//
//
//        if (useSavedAddressCheckBox.isChecked()) {
//            sameAsShippingCheckbox.setClickable(false);
//            useFirebaseAddress = true;
//
//        } else if (!useSavedAddressCheckBox.isChecked()) {
//            sameAsShippingCheckbox.setClickable(true);
//            useFirebaseAddress = false;
//
//        } else if (!useSavedAddressCheckBox.isChecked() && sameAsShippingCheckbox.isChecked()) {
//
//            useFirebaseAddress = false;
//            firstNameBillingEdit.setText(firstNameEdit.getText().toString());
//            lastNameBillingEdit.setText(lastNameEdit.getText().toString());
//            address1BillingEdit.setText(address1ShippingEdit.getText().toString());
//            address2BillingEdit.setText(address2ShippingEdit.getText().toString());
//            zipBillingEdit.setText(zipShippingEdit.getText().toString());
//            cityBillingEdit.setText(cityShippingEdit.getText().toString());
//            stateBillingEdit.setText(stateShippingEdit.getText().toString());
//            countryCodeBillingEdit.setText(countryCodeShippingEdit.getText().toString());
//            phoneNoBillingEdit.setText(phoneNoShippingEdit.getText().toString());
//
//        } else {
//            useFirebaseAddress = false;
//            firstNameBillingEdit.setText("");
//            lastNameBillingEdit.setText("");
//            address1BillingEdit.setText("");
//            address2BillingEdit.setText("");
//            zipBillingEdit.setText("");
//            cityBillingEdit.setText("");
//            stateBillingEdit.setText("");
//            countryCodeBillingEdit.setText("");
//            phoneNoBillingEdit.setText("");
//        }
//    }

    public void getAddressFromFirebase() {

        userAddressRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                addressFromFirebase = snapshot.getValue(Address.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void observeOrderStatus() {
        viewModel.orderResponse.observe(this, this::showUiForStatus);
    }

    private void observeLoadingStatus() {
        viewModel.loading.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean loading) {
                if (loading) {
                    //show loading dialog (if not already shown)
                    showLoadingState();
                } else {
                    //hide loading if shown
                    hideLoadingState();
                }
            }
        });
    }

    private void hideLoadingState() {
        findViewById(R.id.loading_view).setVisibility(View.GONE);
    }

    private void showLoadingState() {
        findViewById(R.id.loading_view).setVisibility(View.VISIBLE);
    }

    private void showUiForStatus(OrderResponse orderResponse) {
        if (orderResponse.code.equals(OrderResponse.CODE_PROCESSING)) {
            // Toast.makeText(this, "Order processing", Toast.LENGTH_SHORT).show();
            //show processing view
        } else if (orderResponse.code.equals(OrderResponse.CODE_MAX_PRICE_EXCEEDED)) {
            Toast.makeText(this, "Order max price exceeded", Toast.LENGTH_SHORT).show();
            Log.d(OrderDetailsActivity.class.getSimpleName(), "showUiForStatus: This is the order: " + orderResponse.toString());
            Log.d(OrderDetailsActivity.class.getSimpleName(), "showUiForStatus: order screenshots" + orderResponse.screenshot_urls);
            showSuccessDialog(orderResponse);
            // show max price exceeded (successful test order) view
        } else {
            Snackbar.make(root, "Order error result: " + orderResponse.code, Snackbar.LENGTH_SHORT).show();

            // unknown/other result; show error view
        }
    }

    public void showSuccessDialog(OrderResponse orderResponse){

        String imageSuccess = orderResponse.screenshot_urls.get(orderResponse.screenshot_urls.size() -1);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_order_success, null);
        ImageView imageView = dialogView.findViewById(R.id.order_screenshot_image);
        Picasso.get()
                .load(imageSuccess)
                .fit()
                .centerCrop()
                .into(imageView);
        builder.setView(dialogView);
        builder.setTitle("Order Success!");
        builder.setPositiveButton("Done", (dialog, which) -> finish());

        AlertDialog alertDialog = builder.create();

        alertDialog.show();

        //TODO: maybe get rid of shopping cart?

    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(this, new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) new OrderStatusViewModel(new OrderRepo());
            }
        }).get(OrderStatusViewModel.class);
    }

}