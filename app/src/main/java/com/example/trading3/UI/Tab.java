package com.example.trading3.UI;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trading3.DataProcess.CombineData;
import com.example.trading3.ProfitAdapter;
import com.example.trading3.ProfitItem;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

public class Tab {
    private Button btLongFuture,btTest,btClosePos;
    private TextView tvAvailableBlanceValue,tvPLNValue,tvTimeLongLive,tvTimeStopLive;
    private EditText edAmountFuture,edTimeStopConfigValue,edTimeFutureConfig,edtvStrategy,edTakeProfit,edStopLoss;
    private Runnable[] runnable;
    private RecyclerView rvProfitList;
    private ProfitAdapter adapter;
    private List<ProfitItem> data;
    private List<String> symbols = new ArrayList<>();

    public Tab(
            Button btLongFuture,
            Button btTest,
            Button btClosePos,
            TextView tvAvailableBlanceValue,
            TextView tvPLNValue,
            TextView tvTimeLongLive,
            TextView tvTimeStopLive,
            EditText edAmountFuture,
            EditText edTimeStopConfigValue,
            EditText edTimeFutureConfig,
            EditText edtvStrategy,
            EditText edTakeProfit,
            EditText edStopLoss,
            RecyclerView rvProfitList) {

        this.btLongFuture = btLongFuture;
        this.btTest = btTest;
        this.btClosePos = btClosePos;

        this.tvAvailableBlanceValue = tvAvailableBlanceValue;
        this.tvPLNValue = tvPLNValue;
        this.tvTimeLongLive = tvTimeLongLive;
        this.tvTimeStopLive = tvTimeStopLive;
        this.edAmountFuture = edAmountFuture;
        this.edTimeStopConfigValue = edTimeStopConfigValue;
        this.edTimeFutureConfig = edTimeFutureConfig;
        this.edtvStrategy = edtvStrategy;
        this.edTakeProfit = edTakeProfit;
        this.edStopLoss = edStopLoss;
        this.rvProfitList  =rvProfitList;

        Handler handler = new Handler();
        runnable = new Runnable[1]; // trick nhỏ để truy cập chính runnable bên trong

        // Lấy danh sách cặp
        AtomicInteger index = new AtomicInteger(0);    // Dùng để thay đổi symbol sau mỗi vòng
        index.getAndIncrement();

        runnable[0] = () -> {
            try {
                String symbol = symbols.get(index.getAndIncrement() % symbols.size());

                combineData.futureTrade.changeToIsolatedMargin(symbol);
                // 1. Đặt lệnh LONG
                combineData.futureTrade.LongFuture(symbol);
                Log.d("AUTO", "✅ Đã đặt lệnh long");

                combineData.futureTrade.closePositionIfMatch(symbol,
                        combineData.futureTrade.getMax(), -combineData.futureTrade.getMin());

                startCountdown(300, handler); // Đếm 5 phút

                // 2. Sau 5 phút đóng lệnh
                handler.postDelayed(() -> {
                    combineData.futureTrade.closeLongOrder(symbol);
                    Log.d("AUTO", "❌ Đã đóng lệnh long");

                    new Handler(Looper.getMainLooper()).post(() -> {
                        data.add(0, new ProfitItem("profit", combineData.futureTrade.RealPLN));
                        adapter.notifyDataSetChanged(); // thử thay notifyItemInserted(0)
                    });

                    combineData.futureTrade.getProfit();

                    startCountdown(60 , handler); // Đếm 1 phút nghỉ

                    // 3. Sau đó lặp lại
                    handler.postDelayed(runnable[0], 60*1000); // dùng đúng runnable
                }, 300*1000);

            } catch (Exception e) {
                e.printStackTrace();
            }
        };

        // 👉 Gắn vào click
        btLongFuture.setOnClickListener(v -> {
            runnable[0].run(); // bắt đầu
        });

        data = new ArrayList<>();
        data.add(new ProfitItem("profit start", "+0.00"));

        adapter = new ProfitAdapter(data);
        rvProfitList.setAdapter(adapter);
    }

    private void startCountdown(int seconds, Handler handler) {
        final int[] timeLeft = {seconds};

        Runnable countdownRunnable = new Runnable() {
            @Override
            public void run() {
                int minutes = timeLeft[0] / 60;
                int secs = timeLeft[0] % 60;
                String timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, secs);
                //textView.setText("⏳ " + timeFormatted);
                combineData.futureTrade.updateTime(timeFormatted);
                timeLeft[0]--;
                if (timeLeft[0] >= 0) {
                    handler.postDelayed(this, 1000); // lặp mỗi 1 giây
                }
            }
        };

        handler.post(countdownRunnable);
    }

    CombineData combineData;
    public void update( CombineData combineData, String coin) throws JSONException {
        this.combineData = combineData;

        tvAvailableBlanceValue.setText(combineData.futureTrade.getAvailableBalance2());
        tvPLNValue.setText(combineData.futureTrade.closePos);
        tvTimeStopLive.setText(combineData.futureTrade.getLongTime());
        tvTimeStopLive.setText("");
    }
}
