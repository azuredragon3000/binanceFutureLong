package com.example.trading3.DataProcess;

public class CoinPriceModel {
    public String symbol;
    public String price;
    public String trendStatus;
    //public boolean isInTrend; // Thêm thuộc tính này để xác định coin có trong xu hướng hay không
    public String percentChange;
    public int priceUpdateCount; // 👈 mới
    public int priceUpdateCountAm; // 👈 mới

    public String trendProgress = ""; // mới: "+2 +0.05"
    public boolean isInTrend = false; // true nếu coin đang lên trend hoặc chuẩn bị lên trend


    public CoinPriceModel(String symbol, String price, String trendStatus, String percentChange) {
        this.symbol = symbol;
        this.price = price;
        this.trendStatus = trendStatus;
       // this.isInTrend = false; // Mặc định là không có xu hướng
        this.percentChange = percentChange;
        this.priceUpdateCount = 0;
        this.priceUpdateCountAm = 0;

    }
}





