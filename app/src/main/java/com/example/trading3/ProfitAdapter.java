package com.example.trading3;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class ProfitAdapter extends RecyclerView.Adapter<ProfitAdapter.ProfitViewHolder> {

    private List<ProfitItem> profitList;

    public ProfitAdapter(List<ProfitItem> profitList) {
        this.profitList = profitList;
    }

    public static class ProfitViewHolder extends RecyclerView.ViewHolder {
        TextView tvLabel, tvValue;

        public ProfitViewHolder(View itemView) {
            super(itemView);
           // tvLabel = itemView.findViewById(R.id.tvLabel);
           // tvValue = itemView.findViewById(R.id.tvValue);
        }
    }

    @NonNull
    @Override
    public ProfitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //View view = LayoutInflater.from(parent.getContext())
          //      .inflate(R.layout.item_profit, parent, false);
        return new ProfitViewHolder(null);
    }

    @Override
    public void onBindViewHolder(@NonNull ProfitViewHolder holder, int position) {
        ProfitItem item = profitList.get(position);
        holder.tvLabel.setText(item.getLabel());
        holder.tvValue.setText(item.getValue());
    }

    @Override
    public int getItemCount() {
        return profitList.size();
    }
}

