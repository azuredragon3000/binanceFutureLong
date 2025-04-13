package com.example.trading3.DataProcess;

import org.json.JSONException;

public class CombineData {
    public Balance balance;
    public CoinPrice coinPrice;
    public FutureTrade futureTrade;
    //public Position position;
    public Fee fee;
    public TPSL tpsl;
    public Calculate calculate;

    public CombineData() throws JSONException {
        this.balance = new Balance();
        this.coinPrice = new CoinPrice();
        this.futureTrade = new FutureTrade();
        futureTrade.getAvailableBalance();
        this.fee = new Fee(balance,coinPrice);
        this.tpsl = new TPSL();
        this.calculate = new Calculate(balance,coinPrice,fee);
        //this.position = new Position();

    }
    public CombineData(Balance balance, CoinPrice coinPrice, FutureTrade futureTrade,
                       Fee fee, TPSL tpsl, Calculate calculate) {
        this.balance = balance;
        this.coinPrice = coinPrice;
        this.futureTrade = futureTrade;
        this.fee = fee;
        this.tpsl = tpsl;
        this.calculate = calculate;
       // this.position = position;
    }
}
