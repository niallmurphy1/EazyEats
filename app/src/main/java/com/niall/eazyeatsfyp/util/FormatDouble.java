package com.niall.eazyeatsfyp.util;

import java.text.DecimalFormat;

public class FormatDouble {


    public String format2DecimalPlaces(double num){

         DecimalFormat df2 = new DecimalFormat("#.##");

         return df2.format(num);


    }
}
