package com.niall.eazyeatsfyp.zincEntities;

import android.util.Log;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.ParsedRequestListener;
import com.androidnetworking.interfaces.StringRequestListener;
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
        AndroidNetworking.post(URL)
                .addHeaders("Authorization", Constants.ZINC_AUTH)
                .addBodyParameter(order)
                .build()
                .getAsObject(OrderResponse.class, new ParsedRequestListener<OrderResponse>() {
                    @Override
                    public void onResponse(OrderResponse response) {

                        Log.d(OrderRepo.class.getSimpleName(), response.requestId);
                        callback.onSuccess(response.requestId);
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e(OrderRepo.class.getSimpleName(), anError.getMessage());

                    }
                });
    }

}
