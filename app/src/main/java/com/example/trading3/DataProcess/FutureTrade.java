package com.example.trading3.DataProcess;

import android.util.Log;

import com.binance.connector.futures.client.impl.UMFuturesClientImpl;
import com.example.trading3.Initialize;
import com.example.trading3.OnPNLUpdateListener;
import com.example.trading3.ProfitItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicBoolean;

public class FutureTrade {

    public UMFuturesClientImpl client;
    private OnPNLUpdateListener listener;
    public List<String> symbols;
    private String rs;
    double squantity;
    public String configIsolar;

    public void setOnPNLUpdateListener(OnPNLUpdateListener listener) {
        this.listener = listener;
    }

    public boolean closeLongOrder(String iSymbol) {
        AtomicBoolean isError = new AtomicBoolean(false);
        StringBuilder log = new StringBuilder();
        new Thread(() -> {
            try {

                squantity = getQuantity(iSymbol);

                isError.set(false);
                LinkedHashMap<String, Object> closeOrderParams = new LinkedHashMap<>();
                closeOrderParams.put("symbol", iSymbol);
                closeOrderParams.put("side", "SELL"); // Nếu lệnh ban đầu là BUY, đóng bằng SELL
                closeOrderParams.put("type", "MARKET");
                closeOrderParams.put("quantity", squantity); // Đóng toàn bộ vị thế
                closeOrderParams.put("reduceOnly", true); // Chỉ đóng vị thế, không mở mới

                String response = client.account().newOrder(closeOrderParams);
                log.append("✅ Lệnh LONG đã STOP: ").append("\n");

                //data.add(0, new ProfitItem("profit", combineData.futureTrade.RealPLN)); // chèn vào đầu
                //adapter.notifyItemInserted(0); // thông báo dòng mới ở vị trí 0

                rs = log.toString();
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                rs = sw.toString();
                isError.set(true);
                //rs = e.printStackTrace();
                rs = "Error Close Long order: "+isError;
            }
        }).start();
        return isError.get();
    }


    public FutureTrade(){
        client = Initialize.getClient();
        rs = " not put Future yet";
        //quantity = 0.01;
        symbols = new ArrayList<>();
        //getAllFutureSymbols();
        infoPos = "NULL";
        RealPLN = "0";
    }

    public double choosenPrice;
    public double getCurrentPrice(String symbol) {
       // double bnbPrice = -1;
        new Thread(() -> {
           // try {
                LinkedHashMap<String, Object> params = new LinkedHashMap<>();
                params.put("symbol", symbol);
                String tickerResponse = client.market().tickerSymbol(params);

            JSONObject ticker = null;
            try {
                ticker = new JSONObject(tickerResponse);
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }
            try {
                choosenPrice = ticker.getDouble("price");
            } catch (JSONException e) {
                throw new RuntimeException(e);
            }

        }).start();
        //return bnbPrice;
        return  0;
    }

    public double getStepSize(String symbol) {
        try {
            String response = client.market().exchangeInfo();
            JSONObject jsonObject = new JSONObject(response);
            JSONArray symbolsArray = jsonObject.getJSONArray("symbols");

            for (int i = 0; i < symbolsArray.length(); i++) {
                JSONObject s = symbolsArray.getJSONObject(i);
                if (s.getString("symbol").equals(symbol)) {
                    JSONArray filters = s.getJSONArray("filters");
                    for (int j = 0; j < filters.length(); j++) {
                        JSONObject f = filters.getJSONObject(j);
                        if (f.getString("filterType").equals("LOT_SIZE")) {
                            return f.getDouble("stepSize");
                        }
                    }
                }
            }
        } catch (Exception e) {
            Log.e("Binance", "Lỗi lấy step size", e);
        }
        return 0;
    }


    public double roundQuantity(String symbol, double quantity) {
        double step = getStepSize(symbol); // ví dụ BNB là 0.01
        int decimals = (int) Math.round(-Math.log10(step));
        return Math.floor(quantity * Math.pow(10, decimals)) / Math.pow(10, decimals);
    }



    public String changeToIsolatedMargin(String symbol) {
        StringBuilder log = new StringBuilder();

        new Thread(() -> {
            try {
                // 🔍 Gọi API đổi sang chế độ Isolated
                LinkedHashMap<String, Object> params = new LinkedHashMap<>();
                params.put("symbol", symbol);
                params.put("marginType", "ISOLATED");

                client.account().changeMarginType(params);
                log.append("✅ Đã chuyển chế độ margin của ").append(symbol).append(" sang ISOLATED.\n");

                LinkedHashMap<String, Object> leverageParams = new LinkedHashMap<>();
                leverageParams.put("symbol", symbol);
                leverageParams.put("leverage", 20);

                client.account().changeInitialLeverage(leverageParams);
                log.append("✅ Đã đặt đòn bẩy x20 cho ").append(symbol).append("\n");

                configIsolar = " thanh cong isolar 20x";
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);
                log.append("Lỗi chuyển chế độ margin của ").append(pw.toString());
                //isError.set(true);
                //rs = "Error Change Isolar order: " + " - " + squantity + "   "+sw.toString();
                configIsolar = " that bai isolar 20x";
            }
        }).start();
        return log.toString();
    }

    public boolean LongFuture(String iSymbol) {
        AtomicBoolean isError = new AtomicBoolean(false);
        StringBuilder log = new StringBuilder();
        new Thread(() -> {
            try {
                isError.set(false);

                squantity = getQuantity(iSymbol);

                // 🔹 Đặt lệnh LONG (BUY)
                LinkedHashMap<String, Object> orderParams = new LinkedHashMap<>();
                orderParams.put("symbol", iSymbol);
                orderParams.put("side", "BUY");
                orderParams.put("recvWindow", 5000);
                orderParams.put("type", "MARKET");
                orderParams.put("quantity", squantity);
                String orderResponse = client.account().newOrder(orderParams);
                log.append("✅ Lệnh LONG đã đặt: ").append("\n");

                //return log.toString();
                rs = log.toString();
            } catch (Exception e) {
                StringWriter sw = new StringWriter();
                PrintWriter pw = new PrintWriter(sw);
                e.printStackTrace(pw);

                isError.set(true);
                rs = "Error Long order: " + " - " + squantity + "   "+sw.toString();
            }
        }).start();

        return isError.get();

    }

    private double getQuantity(String iSymbol) {

        double price = getCurrentPrice(iSymbol); // ví dụ 580.23

        double quantity = 0;
        if (price <= 0) {
            System.out.println("⚠ Không lấy được giá của " + iSymbol);
            //return false;

        }else {
            double usdtAmount = getAmount();
            double notional = usdtAmount*20;
            quantity = notional / price;
        }
        // Làm tròn quantity theo bước lot size của từng cặp nếu cần
        quantity = roundQuantity(iSymbol, quantity); // viết thêm hàm này nếu muốn

        return quantity;
    }



    public String getLongResult() {
        return rs;
    }

    private String time;
    public String getLongTime() {
        return time;
    }

    public void updateTime(String timeFormatted) {
        time = timeFormatted;
    }

    public List<String> getAllGreenFutureSymbolsBlocking() {
        List<String> greenSymbols = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);

        new Thread(() -> {
            try {
                // 1. Lấy toàn bộ symbol futures loại PERPETUAL
                String exchangeInfoResponse = client.market().exchangeInfo();
                JSONObject exchangeInfoJson = new JSONObject(exchangeInfoResponse);
                JSONArray symbolsArray = exchangeInfoJson.getJSONArray("symbols");

                // Tạm lưu symbol PERPETUAL vào list
                List<String> perpetualSymbols = new ArrayList<>();
                for (int i = 0; i < symbolsArray.length(); i++) {
                    JSONObject symbolObj = symbolsArray.getJSONObject(i);
                    if (symbolObj.getString("contractType").equals("PERPETUAL")) {
                        perpetualSymbols.add(symbolObj.getString("symbol"));
                    }
                }

                // 2. Lấy thông tin thay đổi giá 24h
                //String priceChangeResponse = client.market().tickerPriceChangeStatistics(new HashMap<>());
                String priceChangeResponse = client.market().ticker24H(null);

                JSONArray priceChanges = new JSONArray(priceChangeResponse);

                // 3. Lọc những symbol có priceChangePercent > 0 và nằm trong PERPETUAL
                for (int i = 0; i < priceChanges.length(); i++) {
                    JSONObject stat = priceChanges.getJSONObject(i);
                    String symbol = stat.getString("symbol");
                    double priceChangePercent = stat.optDouble("priceChangePercent", 0.0);

                    if (priceChangePercent > 0 && perpetualSymbols.contains(symbol)) {
                        greenSymbols.add(symbol);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        }).start();

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return greenSymbols;
    }


    public List<String> getAllRedFutureSymbolsBlocking() {
        List<String> redSymbols = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1);

        new Thread(() -> {
            try {
                // 1. Lấy toàn bộ symbol PERPETUAL từ exchangeInfo
                String exchangeInfoResponse = client.market().exchangeInfo();
                JSONObject exchangeInfoJson = new JSONObject(exchangeInfoResponse);
                JSONArray symbolsArray = exchangeInfoJson.getJSONArray("symbols");

                List<String> perpetualSymbols = new ArrayList<>();
                for (int i = 0; i < symbolsArray.length(); i++) {
                    JSONObject symbolObj = symbolsArray.getJSONObject(i);
                    if (symbolObj.getString("contractType").equals("PERPETUAL")) {
                        perpetualSymbols.add(symbolObj.getString("symbol"));
                    }
                }

                // 2. Lấy thống kê 24h (price change)
                String priceChangeResponse = client.market().ticker24H(null); // bản 3.0.5 dùng null
                JSONArray priceChanges = new JSONArray(priceChangeResponse);

                // 3. Lọc các symbol có giá giảm (priceChangePercent < 0)
                for (int i = 0; i < priceChanges.length(); i++) {
                    JSONObject stat = priceChanges.getJSONObject(i);
                    String symbol = stat.getString("symbol");
                    double priceChangePercent = stat.optDouble("priceChangePercent", 0.0);

                    if (priceChangePercent < 0 && perpetualSymbols.contains(symbol)) {
                        redSymbols.add(symbol);
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                latch.countDown();
            }
        }).start();

        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return redSymbols;
    }


    public List<String> getAllFutureSymbolsBlocking() {
        List<String> symbols = new ArrayList<>();
        CountDownLatch latch = new CountDownLatch(1); // chỉ đợi 1 lần

        new Thread(() -> {
            try {
                String response = client.market().exchangeInfo();
                JSONObject json = new JSONObject(response);
                JSONArray symbolsArray = json.getJSONArray("symbols");

                for (int i = 0; i < symbolsArray.length(); i++) {
                    JSONObject symbolObj = symbolsArray.getJSONObject(i);
                    if (symbolObj.getString("contractType").equals("PERPETUAL")) {
                        symbols.add(symbolObj.getString("symbol"));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                latch.countDown(); // báo hiệu đã xong
            }
        }).start();

        try {
            latch.await(); // ❗ chờ cho đến khi xong
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return symbols;
    }


    public String resultIncome;
    public String getProfit() {
        new Thread(() -> {
            try {

                LinkedHashMap<String, Object> params = new LinkedHashMap<>();
                params.put("symbol", "BNBUSDT");
                params.put("incomeType", "REALIZED_PNL");
                params.put("limit", 1);
                params.put("timestamp", System.currentTimeMillis());
                params.put("recvWindow", 5000);

                //String result = client.futures().getIncomeHistory(params);


            } catch (Exception e) {
                //result = "Error";

            }
        }).start();

        resultIncome = " profit ";
        return resultIncome;
    }

    public String closePos,infoPos;
    public String closePositionIfMatch(String symbol, double profitTarget, double lossLimit) {
        final int[] secondsPassed = {0};
        final int checkInterval = 5;
        final int maxTime = 300;
        StringBuilder log = new StringBuilder();
        new Thread(() -> {
            try {
                while (secondsPassed[0] < maxTime) {
                    double pnl = getUnrealizedPNL(symbol);
                    Log.d("PNL", "📈 PNL hiện tại: " + pnl + " USDT");
                    log.append(" PNL hiện tại: ").append(pnl).append(" USDT");
                    closePos = "📈 PNL hiện tại: " + pnl + " USDT";
                    if (pnl >= profitTarget || pnl <= lossLimit) {

                        boolean closed = closeLongOrder(symbol);
                        Log.d("AUTO", "🚪 Đã đóng lệnh vì PNL đạt ngưỡng (" + pnl + ")");
                        log.append(" Đã đóng lệnh vì PNL đạt ngưỡng (").append(pnl).append(" )");
                        closePos = " Đã đóng lệnh vì PNL đạt ngưỡng (" + pnl + ")";


                        if (listener != null) {
                            getPNL();
                            listener.onPNLUpdate(RealPLN);
                       }

                        break;
                    }

                    secondsPassed[0] += checkInterval;
                    Thread.sleep(checkInterval * 1000);
                }

                if (secondsPassed[0] >= maxTime) {
                    double pnl = getUnrealizedPNL(symbol);
                    Log.d("AUTO", "⏳ Kết thúc theo dõi PNL sau 5 phút.");
                    log.append("Kết thúc theo dõi PNL sau 5 phút.");
                    closePos = "Kết thúc theo dõi PNL sau 5 phút " + pnl;


                    if (listener != null) {
                        getPNL();
                        listener.onPNLUpdate(RealPLN);
                   }

                }
            } catch (Exception e) {
                Log.e("PNL", "❌ Lỗi trong khi kiểm tra PNL: " + e.getMessage());
                log.append("Lỗi trong khi kiểm tra PNL: ").append(e.getMessage());
                closePos = "❌ Lỗi trong khi kiểm tra PNL: " + e.getMessage();
            }
        }).start();

        return log.toString();
    }

    public String RealPLN;
    public void getPNL() {
        new Thread(() -> {
            try {
                double pnlBuy = getHistoryTransaction("PNL_buy");
                double pnlSell = getHistoryTransaction("PNL_sell");
                double feeBuy = getHistoryTransaction("FeeBuy");
                double feeSell = getHistoryTransaction("FeeSell");
                double tong = pnlBuy + pnlSell - feeSell - feeBuy;

                RealPLN = String.format("%.5f", tong);
            }catch (Exception e){
                RealPLN = "0";
            }
        }).start();
       //return  formatted;
    }

    private double getHistoryTransaction(String key) throws JSONException {
        LinkedHashMap<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("symbol", "ETHUSDT");
        parameters.put("limit", 2); // chỉ lấy 2 lệnh gần nhất

       // String result = client.getAccountTrades(parameters);
        String result = client.account().accountTradeList(parameters);
        //client.account().accountTradeList(parameters);


        JSONArray trades = new JSONArray(result);

        double pnlBuy = 0;
        double pnlSell = 0;
        double feeBuy = 0;
        double feeSell = 0;

        for (int i = 0; i < trades.length(); i++) {
            JSONObject trade = trades.getJSONObject(i);
            boolean isBuyer = trade.getBoolean("buyer");
            double realizedPnl = trade.optDouble("realizedPnl", 0.0); // chỉ có khi là Sell
            double commission = trade.getDouble("commission");

            if (isBuyer) {
                pnlBuy += 0;
                feeBuy += commission;
            } else {
                pnlSell += realizedPnl;
                feeSell += commission;
            }
        }

        switch (key) {
            case "PNL_buy":
                return pnlBuy;
            case "PNL_sell":
                return pnlSell;
            case "FeeBuy":
                return feeBuy;
            case "FeeSell":
                return feeSell;
            default:
                return 0.0;
        }

    }



    private double getUnrealizedPNL(String symbol) {

        try {
            LinkedHashMap<String, Object> params = new LinkedHashMap<>();
            params.put("symbol", symbol);
            String result = client.account().positionInformation(params);
            JSONArray positions = new JSONArray(result);

            for (int i = 0; i < positions.length(); i++) {
                JSONObject pos = positions.getJSONObject(i);
                if (pos.getString("symbol").equals(symbol)) {
                    return pos.getDouble("unRealizedProfit");
                }
            }
        } catch (Exception e) {
            Log.e("PNL", "❌ Không thể lấy PNL: " + e.getMessage());
        }

        return 0.0;
    }


    public void testPLN() {
        //infoPos = "test";
        if (listener != null) {
            listener.onPNLUpdate(infoPos);
        }
    }

    public   double availableBalance = 0;
    public double getAvailableBalance() throws JSONException {
        new Thread(() -> {
            try {
                String accountInfo = client.account().accountInformation(new LinkedHashMap<>());
                JSONObject accountJson = new JSONObject(accountInfo);
                JSONArray assets = accountJson.getJSONArray("assets");
                for (int i = 0; i < assets.length(); i++) {
                    JSONObject asset = assets.getJSONObject(i);
                    if (asset.getString("asset").equals("USDT")) {
                        //return asset.getDouble("availableBalance");
                        availableBalance = asset.getDouble("availableBalance");
                    }
                }
            }catch (Exception e){
                Log.e("PNL", "❌ Không thể lấy available Balance: " + e.getMessage());
            }
            //return 0.0;

        }).start();
        return 0.0;
    }

    public String getAvailableBalance2() {

        String formatted = String.format("%.5f", availableBalance);

        return formatted;
    }


    double max;
    double min;
    double amount;
    public void setMax(String string) {
        double number = Double.parseDouble(string);
        max = number;
    }

    public void setMin(String string) {
        double number = Double.parseDouble(string);
        min = number;
    }

    public void setAmount(String string) {
        double number = Double.parseDouble(string);
        amount = number;
    }
    private double getAmount() {
        return amount;
    }
    public double getMin() {
        return min;
    }

    public double getMax() {
        return max;
    }
}
