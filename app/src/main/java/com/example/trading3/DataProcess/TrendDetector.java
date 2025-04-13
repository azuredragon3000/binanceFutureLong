package com.example.trading3.DataProcess;

import java.util.LinkedList;
import java.util.List;

public class TrendDetector {



    public interface TrendListener {
        void onTrendStarted(String type,double price);
        void onTrendEnded(String type, double durationSeconds, double percentChange);
        void onNewPrice(double price);
        void onUpdateCount(int count,int countAm);
        void onTrendProgress(boolean updown, double cumulativeDelta);


    }

    private enum State { WAITING, MEASURING_UP, MEASURING_DOWN }

    private double waitingStartPrice = 0;
    private int correctDirectionCount = 0;
    private double cumulativeDelta = 0;

    private int windowSize;// = 10; // 5s nếu mỗi giá cách nhau 0.5s
    private final LinkedList<Double> priceWindow = new LinkedList<>();
    private State state = State.WAITING;
    private long trendStartTime = 0;

    private final TrendListener listener;
    private double trendStartPrice = 0;
    private int updateCounter = 0;
    private int updateCounterAm = 0;

    public TrendDetector(double valueTime, TrendListener listener) {
        this.listener = listener;
        windowSize =  (int)(valueTime/0.5);
    }

    public void onNewPrice(double price) {

        listener.onNewPrice(price); // 👈 luôn báo về giá

        priceWindow.add(price);
        if (priceWindow.size() > windowSize) {
            priceWindow.removeFirst();
        }


        switch (state) {
            case WAITING:

                if (priceWindow.size() == 1) {
                    // Bắt đầu chờ trend
                    waitingStartPrice = price;
                   // updateCounter = 0;
                   // cumulativeDelta = 0;
                }

                if (priceWindow.size() >= 2) {
                    double last = priceWindow.getLast();
                    double prev = priceWindow.get(priceWindow.size() - 2);

                    if (last > prev) {
                        updateCounter++;
                        cumulativeDelta += last - prev;
                        listener.onTrendProgress(true, cumulativeDelta);
                    } else if (last < prev) {
                        updateCounterAm++;
                        cumulativeDelta += prev - last;  // lấy trị tuyệt đối cho down
                        listener.onTrendProgress(false, cumulativeDelta); // hoặc gửi dấu âm
                    } else {
                       // updateCounter = 0;
                       // cumulativeDelta = 0;
                        waitingStartPrice = price;
                    }

                    listener.onUpdateCount(updateCounter,updateCounterAm);
                    //listener.onTrendProgress(updateCounter, cumulativeDelta);
                }

                if (priceWindow.size() == windowSize) {
                    if (isStrictlyIncreasing(priceWindow)) {
                        state = State.MEASURING_UP;
                        trendStartTime = System.currentTimeMillis();
                        trendStartPrice = priceWindow.getFirst();
                        //updateCounter = 0;
                        //cumulativeDelta = 0;
                        listener.onTrendStarted("UP",price);
                    } else if (isStrictlyDecreasing(priceWindow)) {
                        state = State.MEASURING_DOWN;
                        trendStartTime = System.currentTimeMillis();
                        trendStartPrice = priceWindow.getFirst();
                        //updateCounter = 0;
                        //cumulativeDelta = 0;
                        listener.onTrendStarted("DOWN",price);
                    }
                }


                break;

            case MEASURING_UP:
                if (priceWindow.getLast() < priceWindow.get(priceWindow.size() - 2)) {
                    endTrend("UP");
                }
                break;

            case MEASURING_DOWN:
                if (priceWindow.getLast() > priceWindow.get(priceWindow.size() - 2)) {
                    endTrend("DOWN");
                }
                break;
        }
    }



    private void endTrend(String type) {
        double endPrice = priceWindow.getLast();
        double percentChange = ((endPrice - trendStartPrice) / trendStartPrice) * 100;

        double duration = (System.currentTimeMillis() - trendStartTime) / 1000.0;
        listener.onTrendEnded(type, duration + 5.0, percentChange); // 👈 thêm phần trăm

        //state = State.WAITING;
        //updateCounter = 0; // reset lại khi kết thúc trend
        priceWindow.clear();
    }


    private boolean isStrictlyIncreasing(List<Double> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i) >= list.get(i + 1)) return false;
        }
        return true;
    }

    private boolean isStrictlyDecreasing(List<Double> list) {
        for (int i = 0; i < list.size() - 1; i++) {
            if (list.get(i) <= list.get(i + 1)) return false;
        }
        return true;
    }
}
