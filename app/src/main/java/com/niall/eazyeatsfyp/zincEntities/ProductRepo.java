package com.niall.eazyeatsfyp.zincEntities;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.niall.eazyeatsfyp.Callback;
import com.niall.eazyeatsfyp.util.Constants;

import java.util.List;

public class ProductRepo {


    private static ProductRepo productRepoInstance = null;


    public ProductRepo() {
    }

    public static ProductRepo getInstance(){

        if (productRepoInstance == null) {

            productRepoInstance = new ProductRepo();
        }

        return productRepoInstance;
    }

    public void getAmazonProductByAsin(String asin, Callback<ProductObject> callback) {
        getAmazonProducts(asin, new Callback<List<ProductObject>>() {
            @Override
            public void onSuccess(List<ProductObject> data) {
                if (!data.isEmpty()) {
                    callback.onSuccess(data.get(0));
                } else {
                    callback.onError(new Throwable("Product not found"));
                }
            }
            @Override
            public void onError(Throwable error) {
                callback.onError(error);
            }
        });
    }


    public void getAmazonProducts(String productName, Callback<List<ProductObject>> callback){

        AndroidNetworking.get(Constants.AMAZON_PRODUCT_SEARCH + productName + Constants.AMAZON_PRODUCT_SEARCH2)
                .addHeaders("Authorization", Constants.ZINC_AUTH)
                .build()
                .getAsObject(AmazonProductsResponse.class, new ParsedRequestListener<AmazonProductsResponse>() {
                    @Override
                    public void onResponse(AmazonProductsResponse response) {
                        callback.onSuccess(response.results);
                    }

                    @Override
                    public void onError(ANError anError) {
                        callback.onError(anError);
                    }
                });

    }

}
