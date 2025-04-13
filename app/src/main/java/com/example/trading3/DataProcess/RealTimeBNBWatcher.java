package com.example.trading3.DataProcess;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class RealTimeBNBWatcher {

    private static final String TAG = "RealTimeBNBWatcher";

    private WebSocket webSocket;
    private TrendDetector trendDetector;

    public RealTimeBNBWatcher(TrendDetector trendDetector) {
        this.trendDetector = trendDetector;
    }

    public void start() {
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(0, TimeUnit.MILLISECONDS)
                .build();

        Request request = new Request.Builder()
                .url("wss://fstream.binance.com/ws/bnbusdt@markPrice")
                .build();

        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onMessage(WebSocket webSocket, String text) {
                try {
                    JSONObject obj = new JSONObject(text);
                    double markPrice = obj.getDouble("p");
                    long time = obj.getLong("E");

                    Log.d(TAG, "[" + time + "] Giá BNB: " + markPrice);

                    // Gửi dữ liệu giá mới vào trendDetector
                    trendDetector.onNewPrice(markPrice);

                } catch (JSONException e) {
                    Log.e(TAG, "Lỗi JSON: " + e.getMessage());
                }
            }

            @Override
            public void onFailure(WebSocket webSocket, Throwable t, Response response) {
                Log.e(TAG, "WebSocket thất bại: " + t.getMessage());
            }
        });
    }

    public void stop() {
        if (webSocket != null) {
            webSocket.cancel();
        }
    }
}
