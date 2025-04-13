package com.example.trading3.DataProcess;

import android.util.Log;

import org.json.JSONObject;

import java.util.function.Consumer;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

public class RealTimeCoinWatcher {
    private String symbol;
    private TrendDetector detector;
    private WebSocket webSocket;
    private Consumer<String> priceCallback;

    public RealTimeCoinWatcher(String symbol, TrendDetector detector, Consumer<String> priceCallback) {
        this.symbol = symbol;
        this.detector = detector;
        this.priceCallback = priceCallback;
    }

    public void start() {
        OkHttpClient client = new OkHttpClient();
        String wsUrl = "wss://fstream.binance.com/ws/" + symbol + "@markPrice";

        Request request = new Request.Builder().url(wsUrl).build();
        webSocket = client.newWebSocket(request, new WebSocketListener() {
            @Override
            public void onMessage(WebSocket webSocket, String text) {
                try {
                    JSONObject obj = new JSONObject(text);
                    double price = obj.getDouble("p");
                    long time = obj.getLong("E");

                    detector.onNewPrice(price);
                    priceCallback.accept(String.valueOf(price));
                } catch (Exception e) {
                    Log.e("WebSocket", "Parse error: " + e.getMessage());
                }
            }
        });
    }

    public void stop() {
        if (webSocket != null) {
            webSocket.cancel();
        }
    }
}
