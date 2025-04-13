package com.example.trading3.DataProcess;

public class Calculate {
    public double number;
    Balance balance;
    CoinPrice coinPrice;

    Fee fee;
    public Calculate(Balance balance, CoinPrice coinPrice,Fee fee) {
        this.balance = balance;
        this.coinPrice =coinPrice;
        this.fee = fee;
    }

    public String getMinimumCFB(boolean mode) {
        //double number = 0;
        if(mode){
            number = balance.number / coinPrice.numberBuy;
        }else {
            number = 3.1415926535;
        }
        String formatted = String.format("%.5f", number);

        return  formatted + " USDT";
    }

    public String getMinimumLF(boolean mode) {
        //double number = 0;
        if(mode){
            number = balance.number / coinPrice.numberBuy;
        }else {
            number = 3.1415926535;
        }
        String formatted = String.format("%.5f", number);

        return formatted+" Long Future";
    }

    public String getProfit(boolean mode, String coin) {
        if(mode){
            double load = balance.number*20 / coinPrice.numberBuy;
            //double number1 = coinPrice.numberBuy * ( 1 - fee.feeNumberBuy ) - coinPrice.numberSell* ( 1+ fee.feeNumberSell);
            double number1 = (coinPrice.numberSell - coinPrice.numberBuy)*load;
            //double number2 = number1*load ;
            number = number1 - (fee.feeNumberSell+fee.feeNumberBuy);
        }else {
            number = 3.1415926535;
        }
        String formatted = String.format("%.5f", number);

        return formatted+" Long Future";
    }
}
