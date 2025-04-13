package com.example.trading3.DataProcess;

public class CoinPrice {

    public double numberBuy;
    public double numberSell;
    public String getCoinPriceBuy(boolean mode,String coin) {
        //number = 0;
        if(mode){
            numberBuy = 597.29;
        }else {
            numberBuy = 3.1415926535;
        }
        String formatted = String.format("%.5f", numberBuy);

        return formatted + " USDT/"+coin;
    }

    public String getCoinpriceSell(boolean mode,String coin) {
        //number = 0;
        if(mode){
            numberSell = 598.56;
        }else {
            numberSell = 3.1415926535;
        }
        String formatted = String.format("%.5f", numberSell);

        return formatted + " USDT/"+coin;
    }


}
