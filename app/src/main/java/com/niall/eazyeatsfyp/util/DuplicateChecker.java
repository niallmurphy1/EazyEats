package com.niall.eazyeatsfyp.util;

import java.util.ArrayList;
import java.util.LinkedHashSet;

public class DuplicateChecker {


    public static ArrayList<String> getRidOfDuplicates(ArrayList<String> ingredientNames){

        if(ingredientNames != null) {
            LinkedHashSet<String> set = new LinkedHashSet<String>(ingredientNames);
            ingredientNames.clear();
            ingredientNames.addAll(set);
        }

        return ingredientNames;
    }
}
