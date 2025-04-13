package com.example.trading3.DataProcess;

public class Fee {

    Balance balance;
    CoinPrice coinPrice;
    double nFee = 0.0005;
    public double feeNumberBuy,feeNumberSell;
    public Fee(Balance balance, CoinPrice coinPrice) {
        this.balance = balance;
        this.coinPrice = coinPrice;
    }

    public String getFeeSell(boolean mode){
        // double number = 0;
        if(mode){
            // number of BNB
            double numberOfCoin = balance.number*20/coinPrice.numberBuy;
            feeNumberSell = numberOfCoin*coinPrice.numberSell*nFee;

        }else {
            feeNumberSell = 3.1415926535;
        }
        String formatted = String.format("%.5f", feeNumberSell);

        return formatted + " USDT";
    }
    public String getFeeCFB(boolean mode) {
       // double number = 0;
        if(mode){
            // number of BNB
            double numberOfCoin = balance.number*20/coinPrice.numberBuy;
            feeNumberBuy = numberOfCoin*coinPrice.numberBuy*nFee;

        }else {
            feeNumberBuy = 3.1415926535;
        }
        String formatted = String.format("%.5f", feeNumberBuy);

        return formatted + " USDT";
    }

}
