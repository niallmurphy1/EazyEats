package com.niall.eazyeatsfyp.zincEntities;

import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.google.gson.Gson;
import com.niall.eazyeatsfyp.Callback;
import com.niall.eazyeatsfyp.util.Constants;

public class OrderRepo {

    private static OrderRepo instance;

    public static OrderRepo getInstance() {
        if (instance == null) {
            instance = new OrderRepo();
        }
        return instance;
    }

    private static final String URL = "https://api.zinc.io/v1/orders";

    public void createOrder(Order order, Callback<String> callback) {
        String jsonOrder = new Gson().toJson(order);
        AndroidNetworking.post(URL)
                .addHeaders("Authorization", Constants.ZINC_AUTH)
                .addStringBody(jsonOrder)
                .build()
                .getAsObject(OrderResponse.class, new ParsedRequestListener<OrderResponse>() {
                    @Override
                    public void onResponse(OrderResponse response) {

                        Log.d(OrderRepo.class.getSimpleName(),"Create order method, response request ID:" + response.request_id);
                        callback.onSuccess(response.request_id);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(OrderRepo.class.getSimpleName(), anError.getMessage());

                    }
                });
    }

    public void retrieveOrder(String orderRequest){


    }

}
