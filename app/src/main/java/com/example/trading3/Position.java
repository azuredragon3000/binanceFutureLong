package com.example.trading3;

public class Position {
    public boolean isOpen = false;
    public double entryPrice = 0;
    public double leverage;// = 20;
    public double margin;// = 5.0; // vốn ban đầu
    public double feeRate = 0.0005; // 0.05%
    public String symbol; // 👈 thêm symbol của coin đang long

    public Position(double margin, double leverage, String symbol){
        this.margin = margin;
        this.leverage = leverage;
        this.symbol = symbol;
    }
}

