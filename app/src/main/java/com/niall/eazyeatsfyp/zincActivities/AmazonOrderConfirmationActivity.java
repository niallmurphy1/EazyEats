package com.niall.eazyeatsfyp.zincActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.niall.eazyeatsfyp.R;

public class AmazonOrderConfirmationActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amazon_order_confirmation);


        //TODO: every 5/10 seconds, search for a confirmation with the string from amazon api order confirmation code


        new Handler(Looper.getMainLooper()).postDelayed(() -> {

        }, 7500);

    }
}

