package com.example.trading3.DataProcess;

import android.os.Handler;
import android.widget.TextView;

import com.binance.connector.futures.client.impl.UMFuturesClientImpl;
import com.example.trading3.UI.Tab;

import org.json.JSONException;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DataProcessing {

    private CombineData combineData;
    private final Handler handler;
    private Runnable updateRunnable;
    private boolean isUpdating = false;
    private TextView resultTextView;

    private UMFuturesClientImpl client;

    public DataProcessing() throws JSONException {
        handler = new Handler();
        combineData = new CombineData();
    }

    public void startApp( Tab tab, String coin) {
        isUpdating = true;

        updateRunnable = () -> {
            if (!isUpdating) return;

            try {
                tab.update(combineData,coin);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

            // Lặp lại sau 1000ms
            handler.postDelayed(updateRunnable, 1000);
        };

        handler.post(updateRunnable);
    }

    public void stopUpdating() {
        isUpdating = false;
        if (updateRunnable != null) {
            handler.removeCallbacks(updateRunnable);
        }
    }

    public void startApp2(TextView tvInfo) {
        resultTextView = tvInfo;
        showText("hello ");
    }

    private void showText(String text) {
        handler.post(() -> resultTextView.setText(text));
    }
}