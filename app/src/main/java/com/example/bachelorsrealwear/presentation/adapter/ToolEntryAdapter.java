/*
package com.example.bachelorsrealwear.presentation.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.bachelorsrealwear.R;
import com.example.bachelorsrealwear.domain.model.ToolEntry;

import java.util.List;

public class ToolEntryAdapter extends RecyclerView.Adapter<ToolEntryAdapter.ToolViewHolder> {
    private List<ToolEntry> toolList;

    public ToolEntryAdapter(List<ToolEntry> tools) {
        this.toolList = tools;
    }

    @NonNull
    @Override
    public ToolViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_tool_row, parent, false);
        return new ToolViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ToolViewHolder holder, int position) {
        ToolEntry tool = toolList.get(position);
        holder.desc.setText(tool.description);
        holder.num.setText(tool.toolNumber);
        holder.date.setText(tool.expiryDate);
    }

    @Override
    public int getItemCount() {
        return Math.min(toolList.size(), 10); // Show max 10 tools
    }

    public void updateTools(List<ToolEntry> newTools) {
        this.toolList = newTools;
        notifyDataSetChanged();
    }

    public static class ToolViewHolder extends RecyclerView.ViewHolder {
        TextView desc, num, date;

        public ToolViewHolder(@NonNull View itemView) {
            super(itemView);
            desc = itemView.findViewById(R.id.tv_tool_description);
            num = itemView.findViewById(R.id.tv_tool_number);
            date = itemView.findViewById(R.id.tv_expiry_date);
        }
    }
}
*/
