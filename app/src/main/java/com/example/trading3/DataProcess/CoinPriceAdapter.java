package com.example.trading3.DataProcess;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.trading3.R;

import java.util.List;

public class CoinPriceAdapter extends RecyclerView.Adapter<CoinPriceAdapter.CoinViewHolder> {

    private List<CoinPriceModel> coinList;

    public CoinPriceAdapter(List<CoinPriceModel> coinList) {
        this.coinList = coinList;
    }

    @NonNull
    @Override
    public CoinViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_coin, parent, false);
        return new CoinViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CoinViewHolder holder, int position) {
        CoinPriceModel model = coinList.get(position);

        holder.tvIndex.setText(String.valueOf(position + 1));
        holder.tvSymbol.setText(model.symbol);
        holder.tvPrice.setText(model.price);
        holder.tvChange.setText(model.percentChange);
        holder.tvTrend.setText(model.trendStatus);
        holder.tvUpdateCount.setText(String.valueOf(model.priceUpdateCount));
        holder.tvUpdateCountAm.setText("-"+String.valueOf(model.priceUpdateCountAm));
        holder.tvTrendProgress.setText(model.trendProgress); // 👈 mới

        // Đổi màu % thay đổi nếu muốn
        try {
            double percent = Double.parseDouble(model.percentChange.replace("%", ""));
            if (percent > 0) {
                holder.tvChange.setTextColor(0xFF008000); // Xanh
            } else if (percent < 0) {
                holder.tvChange.setTextColor(0xFFFF0000); // Đỏ
            } else {
                holder.tvChange.setTextColor(0xFF666666); // Xám
            }
        } catch (Exception e) {
            holder.tvChange.setTextColor(0xFF666666); // fallback
        }
    }


    @Override
    public int getItemCount() {
        return coinList.size();
    }

    public static class CoinViewHolder extends RecyclerView.ViewHolder {
        TextView tvIndex, tvSymbol, tvPrice, tvChange, tvTrend,tvUpdateCount,tvTrendProgress,tvUpdateCountAm;

        public CoinViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIndex = itemView.findViewById(R.id.tvIndex);
            tvSymbol = itemView.findViewById(R.id.tvSymbol);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvChange = itemView.findViewById(R.id.tvChange);
            tvTrend = itemView.findViewById(R.id.tvTrend);
            tvUpdateCount = itemView.findViewById(R.id.tvUpdateCount); // 👈 mới
            tvUpdateCountAm = itemView.findViewById(R.id.tvUpdateCountAm); // 👈 mới

            tvTrendProgress = itemView.findViewById(R.id.tvTrendProgress);

        }
    }



}
