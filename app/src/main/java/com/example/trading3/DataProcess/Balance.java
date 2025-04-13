package com.example.trading3.DataProcess;

import org.json.JSONException;

public class Balance {

    //private int inumber;
    public double number = 0;
    //private CombineData combineData;
    public Balance(){
        //inumber = 0;
        //this.combineData = combineData;
    }
    public String getAvailableBalance(boolean mode,CombineData combineData) throws JSONException {

        if(mode)
        {
            //number = 4.8;
            number = combineData.futureTrade.getAvailableBalance();
        }
        else
        {
            number = 3.1415926535;
        }
        String formatted = String.format("%.5f", number);

        return formatted + " USDT";
    }

}
