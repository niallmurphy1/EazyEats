package com.niall.eazyeatsfyp.zincEntities;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.niall.eazyeatsfyp.Callback;

public class UserAmazonDetailsWriter {

    public void writeAddress(DatabaseReference addressRef, Address address){

     addressRef.setValue(address).addOnSuccessListener(new OnSuccessListener<Void>() {
         @Override
         public void onSuccess(Void aVoid) {

             Log.d(UserAmazonDetailsWriter.class.getSimpleName(), "onSuccess: successfully wrote address: " + address.toString());
         }
     }).addOnFailureListener(new OnFailureListener() {
         @Override
         public void onFailure(@NonNull Exception e) {
             Log.e(UserAmazonDetailsWriter.class.getSimpleName(), "onFailure: " + e.getLocalizedMessage());
         }
     });

    }
}
