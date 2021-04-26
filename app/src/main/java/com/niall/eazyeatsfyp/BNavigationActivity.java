package com.niall.eazyeatsfyp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.niall.eazyeatsfyp.barcode.Scanner;
import com.niall.eazyeatsfyp.fragments.MyFoodFragment;
import com.niall.eazyeatsfyp.fragments.RecipeSearcherFragment;
import com.niall.eazyeatsfyp.fragments.RecipeTinderFragment;
import com.niall.eazyeatsfyp.fragments.ShoppingListFragment;

public class BNavigationActivity extends AppCompatActivity {

    final SparseArray<Fragment> fragments = new SparseArray<>();
    FirebaseAuth firebaseAuth;
    Intent cameraIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_dash);

        BottomNavigationView bNV = findViewById(R.id.bottom_nav_view);

        bNV.setSelectedItemId(R.id.myFoodTabLayout);

        firebaseAuth = FirebaseAuth.getInstance();

        if(savedInstanceState == null){
            setInitialFrag();
        }

        bNV.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment frag = fragments.get(item.getItemId());

                switch(item.getItemId()){
                    case R.id.shopping_list_nav:
                        if (frag == null) frag = new ShoppingListFragment();
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, frag)
                                .commit();
                        fragments.put(item.getItemId(), frag);
                        overridePendingTransition(0,0);
                        return true;

                    case R.id.inventory_nav:
                        //use tabs!!!!!
                        if (frag == null) frag = new MyFoodFragment();
                       getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, frag)
                                .commit();
                        fragments.put(item.getItemId(), frag);//overridePendingTransition(0,0);
                        return true;

                    case R.id.recipe_tinder_nav:
                        if (frag == null) frag = new RecipeTinderFragment();
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, frag)
                                .commit();
                        fragments.put(item.getItemId(), frag);
                        return true;


                    case R.id.recipes_nav:
                        if (frag == null) frag = new RecipeSearcherFragment();
                        getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.container, frag)
                                .commit();
                        fragments.put(item.getItemId(), frag);
                        return true;

                }
                return false;
            }

        });
    }

    public void setInitialFrag(){
        Fragment frag = new MyFoodFragment();
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.container, frag)
                .commit();
        fragments.put(R.id.myFoodTabLayout, frag);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId()){
            case R.id.item1:
                this.finish();
                return  true;

            case R.id.item2:
                cameraIntent = new Intent(this, Scanner.class);
               startActivity(cameraIntent);
                return  true;

            case R.id.item3:
                if (firebaseAuth.getCurrentUser() != null) {
                    firebaseAuth.signOut();
                    startActivity(new Intent(this, RegisterActivity.class));
                }

                return  true;
        }
        return super.onOptionsItemSelected(item);
    }
}