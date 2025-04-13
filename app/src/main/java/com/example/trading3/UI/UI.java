package com.example.trading3.UI;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trading3.DataProcess.DataProcessing;
import com.example.trading3.ProfitAdapter;
import com.example.trading3.ProfitItem;
import com.example.trading3.R;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UI {

    //private Button btLongFuture;
    /*private TextView
            tvCoinPride,tvCoinPrideValue,
            tvAvailableBlance, tvAvailableBlanceValue,
            tvOpenPosition,tvOpenPositionValue,
            tvMCFB,tvMCFBValue,
            tvFBMF,tvFBMFValue,
            tvMLF,tvMLFValue,
            tvTakeProfit,tvTakeProfitValuePrice,tvTakeProfitValueUSDT,
            tvStoploss,tvStoplossValuePrice, tvStoplossValueUSDT;
*/
            //tvCoinPride1,tvCoinPrideValue1,
            //tvCoinPride1Sell,tvCoinPrideValue1Sell,tvProfit1Value,

            //tvOpenPosition1,tvOpenPositionValue1,
            //tvMCFB1,tvMCFBValue1,
           // tvFBMF1,tvFBMFValue1,

            //tvTakeProfit1,tvTakeProfitValuePrice1,tvTakeProfitValueUSDT1,

    private Button btLongFuture,btTest,btClosePos;
    private TextView
            tvAvailableBlanceValue,
            tvPLNValue,
            tvTimeLongLive,
            tvTimeStopLive,
            tvInfo;
            //tvStoploss1,tvStoplossValuePrice1, tvStoplossValueUSDT1,tvFBMFValue2;
    private EditText edAmountFuture,
            edTimeStopConfigValue,
            edTimeFutureConfig,
            edtvStrategy,
            edTakeProfit,
            edStopLoss;
    private Tab tab1;
    //boolean mode;
    private TabLayout tabLayout;
    private LinearLayout tab1Content, tab2Content;
    private RecyclerView rvProfitList;

    public UI( Context context) {
       // tabLayout = ((Activity)context).findViewById(R.id.tabLayout);

        tabLayout.addTab(tabLayout.newTab().setText("Mode No API"));
        tabLayout.addTab(tabLayout.newTab().setText("Mode API"));

        //rvProfitList = ((Activity)context).findViewById(R.id.rvProfitList);
        rvProfitList.setLayoutManager(new LinearLayoutManager(((Activity)context)));

        tab1 = new Tab(btLongFuture,btTest,btClosePos,tvAvailableBlanceValue,
                tvPLNValue,tvTimeLongLive,tvTimeStopLive,edAmountFuture,edTimeStopConfigValue,
                edTimeFutureConfig,edtvStrategy,edTakeProfit,edStopLoss,rvProfitList);

        tabLayout.post(() -> {
            TabLayout.Tab tab = tabLayout.getTabAt(1);
            if (tab != null) {
                tab.select();
            }
        });

    }

    public void switchTab(DataProcessing dataProcessing, String coin) {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();

                dataProcessing.stopUpdating();

                if (position == 0) {
                    tab1Content.setVisibility(View.VISIBLE);
                    tab2Content.setVisibility(View.GONE);
                    dataProcessing.startApp2(tvInfo);
                } else if (position == 1) {
                    tab1Content.setVisibility(View.GONE);
                    tab2Content.setVisibility(View.VISIBLE);
                    dataProcessing.startApp(tab1,coin);
                }
            }
            @Override public void onTabUnselected(TabLayout.Tab tab) {}
            @Override public void onTabReselected(TabLayout.Tab tab) {}
        });


    }
}
