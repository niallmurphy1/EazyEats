package com.niall.eazyeatsfyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.niall.eazyeatsfyp.entities.Food;
import com.niall.eazyeatsfyp.entities.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Dashboard extends AppCompatActivity {


    public DatabaseReference dataRef;
    public FirebaseAuth fAuth = FirebaseAuth.getInstance();
    public FirebaseUser fUser = fAuth.getCurrentUser();
    final String userId = fUser.getUid();
    public List<Food> inventory = new ArrayList<>();


    public HashMap newUserFood = new HashMap();
    public Intent recipeSearcher;

    public Intent foodSearch;

    public Intent navIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        dataRef = FirebaseDatabase.getInstance().getReference("User");

         //recipeSearcher = new Intent(this, RecipeSearcherActivity.class);

         navIntent = new Intent(this, BNavigationActivity.class);

    }

    public void onFoodSearchClick(View view){
        startActivity(foodSearch);

    }


    public void onBtnClick(View view){
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            Log.w("USER", "Nay user ");

        }

        String key = dataRef.push().getKey();
        final String uId = fUser.getUid();

        dataRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot userSnapshot: snapshot.getChildren()) {


                    User userObj = snapshot.child(uId).getValue(User.class);
                    String email = userObj.getEmail();


                    Log.w("USER", "Username " +  email);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("User", "USER NOT FOUND");
            }
        });
    }

    public void onRecipeBtnClick(View view){

        startActivity(recipeSearcher);
    }


    public void AddRecipe(View view){

        startActivity(navIntent);




    }



    public void addFoodBtn(View view){


        EditText foodName = findViewById(R.id.editTextFoodName);
        EditText foodQuant = findViewById(R.id.editTextQuantity);
        EditText foodMacro = findViewById(R.id.editTextMacro);
        EditText foodType = findViewById(R.id.editTextMealType);

        if (foodName.toString() ==null || foodQuant.toString() ==null || foodMacro.toString() ==null || foodType.toString() ==null){
            Toast.makeText(this, "You must enter valid values for each field!", Toast.LENGTH_SHORT).show();
        }
        else {


            int quant = Integer.parseInt(foodQuant.getText().toString());
            Food food = new Food();


            DatabaseReference foodRef = FirebaseDatabase.getInstance().getReference("Food");
            String key = foodRef.push().getKey();
            //  Food food = new Food(name, quantity, macro, healthy, preferred, mealType);
            inventory.add(food);

            Map<String, Object> foodValues = food.toMap();

            Map<String, Object> childUpdates = new HashMap<>();

            childUpdates.put(key, foodValues);

            newUserFood.put(key, food.getName());

            dataRef.child(userId).child("user-inventory").updateChildren(newUserFood).addOnSuccessListener(new OnSuccessListener() {
                @Override
                public void onSuccess(Object o) {
                    Log.w("UPDATE DB", "SUCCESS USER INVENTORY");

                }
            });




            foodRef.updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.w("UPDATE DB", "SUCCESS");
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Write failed
                            Log.w("UPDATE DB", "FAILURE");
                        }
                        // ...

                    });


        }

    }

}