package com.example.trading3;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.binance.connector.futures.client.impl.UMFuturesClientImpl;
import com.example.trading3.DataProcess.CoinPriceAdapter;
import com.example.trading3.DataProcess.CoinPriceModel;
import com.example.trading3.DataProcess.FutureTrade;
import com.example.trading3.DataProcess.RealTimeBNBWatcher;
import com.example.trading3.DataProcess.RealTimeCoinWatcher;
import com.example.trading3.DataProcess.TrendDetector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MainActivity extends AppCompatActivity {

    private TextView tvPNL,tvWorking;
    private TrendDetector detector;
    private RealTimeBNBWatcher watcher;
    private Handler handler;
    private RecyclerView recyclerView;
    private CoinPriceAdapter adapter;
    private List<CoinPriceModel> coinList;
    private Map<String, TrendDetector> trendDetectors = new HashMap<>();
    private String[] coinSymbols;
    private boolean isCoinSymbolsUpdated = false; // Flag to check if coinSymbols is updated
    private double valueTime;
    private Button btStart,btLong,buttonClose;
    private Position myPosition;
    private String availableBalance;
    private EditText edAvailableBalance,edSymbol,edTimeLong,edTime;
    private FutureTrade futureTrade;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        coinSymbols = null;
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        handler = new Handler();
        coinList = new ArrayList<>();

        futureTrade = new FutureTrade();
        edSymbol = findViewById(R.id.edSymbol);
        adapter = new CoinPriceAdapter(coinList);
        recyclerView.setAdapter(adapter);
        btStart = findViewById(R.id.btStart);
        btLong = findViewById(R.id.btLong);
        coinList = new ArrayList<>();
        adapter = new CoinPriceAdapter(coinList);
        recyclerView.setAdapter(adapter); // Gắn adapter sớm
        tvWorking = findViewById(R.id.tvWorking);

        edTime= findViewById(R.id.edTime);
        edAvailableBalance = findViewById(R.id.edAvailableBalance);
        valueTime = Integer.parseInt(String.valueOf(edTime.getText()));
        availableBalance = String.valueOf(edAvailableBalance.getText());
        davailableBalance = Double.parseDouble(availableBalance);
        tvPNL = findViewById(R.id.tvPNL);
        try {
            getAllCoin(futureTrade.client); // Gọi API để lấy danh sách coin
        } catch (Exception e) {
            e.printStackTrace();
        }


        btLong.setOnClickListener(v -> {
            if (!myPosition.isOpen && model.price != null) {
                myPosition.entryPrice = futureTrade.choosenPrice;
                myPosition.isOpen = true;
                davailableBalance = davailableBalance - (myPosition.margin * myPosition.feeRate); // trừ phí
                tvWorking.setText("Long Future");
                tvPNL.setText("Entry: $ --"  +
                        "\nNow: $ --"  +
                        "\nPnL: $ --" +
                        "\nbalanceWithPnL: $ " + String.format("%.2f", davailableBalance) +
                        "\nQty: --" );
                new CountDownTimer(10_000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        tvWorking.setText("Long Future (" + millisUntilFinished / 1000 + "s)");
                    }

                    public void onFinish() {
                        // 👇 Tự đóng lệnh sau 10s
                        if (myPosition.isOpen) {
                            myPosition.isOpen = false;
                            tvWorking.setText("Stop");
                        }
                    }
                }.start();
            }
        });

        buttonClose = findViewById(R.id.btStop);
        buttonClose.setOnClickListener(v -> {
            if (myPosition.isOpen) {
                myPosition.isOpen = false;
                tvWorking.setText("Stop");
            }
        });

        btStart.setOnClickListener(v->{
            String iSymbol = String.valueOf(edSymbol.getText());

            edTimeLong = findViewById(R.id.edTimeLong);
            edTime= findViewById(R.id.edTime);
            edAvailableBalance = findViewById(R.id.edAvailableBalance);

            valueTime = Integer.parseInt(String.valueOf(edTime.getText()));
            availableBalance = String.valueOf(edAvailableBalance.getText());
            davailableBalance = Double.parseDouble(availableBalance);
            myPosition = new Position(Double.parseDouble(availableBalance),20,iSymbol);
            futureTrade.getCurrentPrice(myPosition.symbol);
            StartApp();
        });
    }

    private double davailableBalance;
    private CoinPriceModel model;
    private void StartApp() {


        // You can add a check to ensure coinSymbols is updated
        new Thread(() -> {
            while (!isCoinSymbolsUpdated) {
                try {
                    Thread.sleep(100); // Sleep for 100ms to avoid busy-waiting
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            runOnUiThread(() -> {
                if (coinSymbols != null && coinSymbols.length > 0) {
                    coinList.clear(); // Xóa dữ liệu cũ nếu có

                    for (int i = 0; i < coinSymbols.length; i++) {
                        final int position = i;
                        String symbol = coinSymbols[i];

                        CoinPriceModel model = new CoinPriceModel(symbol.toUpperCase(), "Loading...", "Đợi trend...", "0.0%");
                        this.model = model;

                        coinList.add(model);

                        TrendDetector detector = new TrendDetector(valueTime, new TrendDetector.TrendListener() {

                            @Override
                            public void onUpdateCount(int count,int countAm) {
                                runOnUiThread(() -> {
                                    model.priceUpdateCount = count;
                                    model.priceUpdateCountAm = countAm;
                                    adapter.notifyItemChanged(position);
                                });
                            }
                            @Override
                            public void onTrendStarted(String type) {
                                handler.post(() -> {
                                    model.trendStatus = "📈 Bắt đầu trend: " + type;
                                    adapter.notifyItemChanged(position);
                                    //  model.priceUpdateCount = 0;
                                    model.isInTrend = true; // 👉 coin này đang trend
                                    sortAndRefreshList();

                                });
                            }

                            @Override
                            public void onTrendProgress(boolean updown, double delta) {
                                runOnUiThread(() -> {
                                    if(updown) {
                                        model.trendProgress = String.format("%.5f", delta);
                                    }else{
                                        model.trendProgress = "-"+String.format("%.5f", delta);
                                    }
                                    adapter.notifyItemChanged(position);
                                });
                            }


                            @Override
                            public void onTrendEnded(String type, double durationSeconds, double percentChange) {
                                handler.post(() -> {
                                    model.trendStatus = "✅ Trend " + type + ": " + durationSeconds + "s";
                                    model.percentChange = String.format("%.2f%%", percentChange);
                                    adapter.notifyItemChanged(position);
                                    //  model.priceUpdateCount = 0;
                                    model.isInTrend = false; // 👉 kết thúc trend
                                    sortAndRefreshList();

                                });
                            }

                            @Override
                            public void onNewPrice(double price) {
                                handler.post(() -> {
                                    model.price = String.valueOf(price);
                                    adapter.notifyItemChanged(position);

                                    // 👇 Chỉ tính PnL nếu đang long đúng coin này
                                    if (myPosition.isOpen && model.symbol.equals(myPosition.symbol)) {

                                        double entry = myPosition.entryPrice;
                                        double current = price;
                                        futureTrade.choosenPrice = price;
                                        double pnl = (price - myPosition.entryPrice) * myPosition.leverage * (myPosition.margin / myPosition.entryPrice);
                                        double balanceWithPnL = myPosition.margin + pnl;
                                        davailableBalance = balanceWithPnL;
                                        double quantity = (myPosition.margin * myPosition.leverage) / entry;
                                        tvPNL.setText("Entry: $ " + String.format("%.2f", entry) +
                                                "\nNow: $ " + String.format("%.2f", current) +
                                                "\nPnL: $ " + String.format("%.2f", pnl) +
                                                "\nbalanceWithPnL: $ " + String.format("%.2f", balanceWithPnL) +
                                                "\nQty: " + String.format("%.4f", quantity) + " " + myPosition.symbol +" margin: "+myPosition.margin+" leverage: "+myPosition.leverage);
                                    }
                                });
                            }
                        });

                        trendDetectors.put(symbol, detector);

                        RealTimeCoinWatcher watcher = new RealTimeCoinWatcher(symbol, detector, newPrice -> {
                            handler.post(() -> {
                                model.price = newPrice;
                                adapter.notifyItemChanged(position);
                            });
                        });

                        watcher.start();
                    }

                    adapter.notifyDataSetChanged(); // Cập nhật lại toàn bộ danh sách
                }
            });
        }).start();

    }

    private void sortAndRefreshList() {
        Collections.sort(coinList, (a, b) -> Boolean.compare(b.isInTrend, a.isInTrend));
        adapter.notifyDataSetChanged();
    }

    public void getAllCoin(UMFuturesClientImpl client) throws JSONException {
        new Thread(() -> {
            try {
                String response = client.market().exchangeInfo();
                JSONObject json = new JSONObject(response);
                JSONArray symbolsArray = json.getJSONArray("symbols");

                List<String> usdtSymbols = new ArrayList<>();

                for (int i = 0; i < symbolsArray.length(); i++) {
                    JSONObject symbolObj = symbolsArray.getJSONObject(i);
                    String symbol = symbolObj.getString("symbol");
                    String quoteAsset = symbolObj.getString("quoteAsset");
                    String status = symbolObj.getString("status");

                    // Chỉ lấy các cặp USDT đang hoạt động
                    if ("USDT".equals(quoteAsset) && "TRADING".equals(status)) {
                        usdtSymbols.add(symbol.toLowerCase());
                    }
                }

                coinSymbols = usdtSymbols.toArray(new String[0]);
                isCoinSymbolsUpdated = true; // Set the flag to true once updated

            } catch (Exception e) {
                coinSymbols = new String[] {
                        "btcusdt", "ethusdt", "bnbusdt", "solusdt", "adausdt",
                        "xrpusdt", "dogeusdt", "maticusdt", "dotusdt", "ltcusdt"
                };
                isCoinSymbolsUpdated = true; // Set the flag to true even for default symbols
            }
        }).start();
    }

}