package com.niall.eazyeatsfyp.zincEntities;

import java.util.ArrayList;

public class OrderResponse {
    public static final String CODE_PROCESSING = "request_processing";
    public static final String CODE_MAX_PRICE_EXCEEDED = "max_price_exceeded";
    public String request_id;
    public String _type;
    public String code;
    public ArrayList<String> screenshot_urls;

    @Override
    public String toString() {
        return "OrderResponse{" +
                "request_id='" + request_id + '\'' +
                ", _type='" + _type + '\'' +
                ", code='" + code + '\'' +
                ", screenshot_urls=" + screenshot_urls +
                '}';
    }
}
