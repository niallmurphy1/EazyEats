package com.niall.eazyeatsfyp.zincActivities;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.niall.eazyeatsfyp.Callback;
import com.niall.eazyeatsfyp.zincEntities.OrderRepo;
import com.niall.eazyeatsfyp.zincEntities.OrderResponse;
import java.util.concurrent.TimeUnit;
public class OrderStatusViewModel extends ViewModel {
    private static final long POLLING_INTERVAL = 5000L;
    private final OrderRepo repo;
    private String orderRequestId;
    private Handler handler;
    public MutableLiveData<OrderResponse> orderResponse = new MutableLiveData<>();
    public MutableLiveData<Boolean> loading = new MutableLiveData<>(false);
    public OrderStatusViewModel(OrderRepo repo) {
        this.repo = repo;
    }
    private final Runnable checkStatusRunnable = new Runnable() {
        @Override
        public void run() {
            try {
                Log.d(OrderStatusViewModel.class.getSimpleName(), "run");
                getOrderStatus();
            } finally {
                handler.postDelayed(checkStatusRunnable, POLLING_INTERVAL);
            }
        }
    };
    private void getOrderStatus() {
        loading.postValue(true);
        repo.retrieveOrder(orderRequestId, new Callback<OrderResponse>() {
            @Override
            public void onSuccess(OrderResponse data) {
                if (!data.code.equals(OrderResponse.CODE_PROCESSING)) {
                    stopPolling();
                }
                orderResponse.postValue(data);
            }
            @Override
            public void onError(Throwable error) {
                error.printStackTrace();
            }
        });
    }
    private void pollOrder() {
        checkStatusRunnable.run();
    }
    @Override
    protected void onCleared() {
        super.onCleared();
        stopPolling();
    }
    private void stopPolling() {
        handler.removeCallbacks(checkStatusRunnable);
        loading.postValue(false);
    }
    public void pollOrderStatus(String orderRequestId) {
        this.orderRequestId = orderRequestId;
        handler = new Handler(Looper.myLooper());
        pollOrder();
    }
}
