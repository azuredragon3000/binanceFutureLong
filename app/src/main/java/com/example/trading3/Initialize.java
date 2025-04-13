package com.example.trading3;

import com.binance.connector.futures.client.impl.UMFuturesClientImpl;
import java.util.LinkedHashMap;
public class Initialize {

    private static final String API_KEY = "sfmD4FgplXdx5mnvhVgJgWYh7ps1yztZ5wdZAVyf8KXlafqXXnMzEuzFU8UHHEud";
    private static final String API_SECRET = "Movg480Jc5GZkaPt8RuKEWlKhSYK1wvvijetOGmqSEDOMyFlhN5WQWVDhLm4WKII";
    // Thay thế bằng API_KEY & API_SECRET thực của bạn

    private static UMFuturesClientImpl client;

    public static void initBinance() {
        if (client == null) {
            client = new UMFuturesClientImpl(API_KEY, API_SECRET);
        }
    }

    public static UMFuturesClientImpl getClient() {
        if (client == null) {
            initBinance();
        }
        return client;
    }

}

