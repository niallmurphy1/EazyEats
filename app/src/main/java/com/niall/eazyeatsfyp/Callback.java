package com.niall.eazyeatsfyp;

public interface Callback<T> {
    void onSuccess(T data);
    void onError(Throwable error);
}
