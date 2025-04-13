package com.example.trading3.DataProcess;

public class TPSL {

    public String getStopLoss(boolean mode) {
        double number = 0;
        if(mode){
            if(number > 10){
                number = 0;
            }
            number++;
        }else {
            number = 3.1415926535;
        }
        String formatted = String.format("%.5f", number);

        return formatted+" Price";
    }

    public String getTP(boolean mode) {
        double number = 0;
        if(mode){
            if(number > 10){
                number = 0;
            }
            number++;
        }else {
            number = 3.1415926535;
        }
        String formatted = String.format("%.5f", number);

        return formatted+ " Price";
    }

    public String getStopLossValue(boolean mode) {
        double number = 0;
        if(mode){
            if(number > 10){
                number = 0;
            }
            number++;
        }else {
            number = 3.1415926535;
        }
        String formatted = String.format("%.5f", number);
        return formatted + " USDT";
    }

    public String getTPValue(boolean mode) {
        double number = 0;
        if(mode){
            if(number > 10){
                number = 0;
            }
            number++;
        }else {
            number = 3.1415926535;
        }
        String formatted = String.format("%.5f", number);

        return formatted+" USDT";
    }
}
