package com.niall.eazyeatsfyp.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;
import com.niall.eazyeatsfyp.R;
import com.niall.eazyeatsfyp.adapters.ViewPagerAdapter;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyFoodFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyFoodFragment extends Fragment {




    private TabLayout tabLayout;
    private ViewPager viewPager;

    private Fragment myIngredientsFragment;
    private Fragment myRecipesFragment;

    public MyFoodFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyFoodFragment.
     */
    public static MyFoodFragment newInstance(String param1, String param2) {
        MyFoodFragment fragment = new MyFoodFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // toolbar = getView().findViewById(R.id.myFoodToolbar);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_my_food, container, false);
    }



    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setUpView();

    }

    @Override
    public void onResume() {
        super.onResume();
        //setUpView();
    }

    public void setUpView(){

        myIngredientsFragment = new MyFoodIngredientsFragment();
        myRecipesFragment = new MyFoodRecipesFragment();


            tabLayout = getView().findViewById(R.id.myFoodTabLayout);
            viewPager  = getView().findViewById(R.id.myFoodViewPager);

        tabLayout.setupWithViewPager(viewPager);

        ViewPagerAdapter viewPagerAdapter = new ViewPagerAdapter(getChildFragmentManager(), 0);

        viewPagerAdapter.addFragment(myIngredientsFragment, "My Ingredients");
        viewPagerAdapter.addFragment(myRecipesFragment, "My Recipes");
        viewPager.setAdapter(viewPagerAdapter);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        //TODO: figure this out at some point
    }
}