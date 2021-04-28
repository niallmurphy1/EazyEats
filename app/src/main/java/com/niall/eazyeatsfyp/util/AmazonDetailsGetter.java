package com.niall.eazyeatsfyp.util;

import android.renderscript.Sampler;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.niall.eazyeatsfyp.Callback;
import com.niall.eazyeatsfyp.zincEntities.RetailerCreds;

public class AmazonDetailsGetter {

    public static RetailerCreds getAmazonDetailsFromFirebase(DatabaseReference db, Callback<RetailerCreds> retailerCredsCallback){

        RetailerCreds retailerCreds = new RetailerCreds();

        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                RetailerCreds retailerCreds = snapshot.getValue(RetailerCreds.class);
                retailerCredsCallback.onSuccess(retailerCreds);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return retailerCreds;
    }


}
