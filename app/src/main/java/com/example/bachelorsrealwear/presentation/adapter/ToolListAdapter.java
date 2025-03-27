package com.example.bachelorsrealwear.presentation.adapter;

import android.content.Context;
import android.view.*;
import android.widget.*;
import com.example.bachelorsrealwear.R;
import com.example.bachelorsrealwear.domain.model.ToolEntry;

import java.util.List;

public class ToolListAdapter extends ArrayAdapter<ToolEntry> {
    public ToolListAdapter(Context context, List<ToolEntry> tools) {
        super(context, 0, tools);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ToolEntry tool = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_tool_row, parent, false);
        }

        TextView tvDesc = convertView.findViewById(R.id.tv_description);
        TextView tvNum = convertView.findViewById(R.id.tv_tool_number);
        TextView tvExp = convertView.findViewById(R.id.tv_expiry);

        tvDesc.setText(tool.description);
        tvNum.setText(tool.toolNumber);
        tvExp.setText(tool.expiryDate);

        return convertView;
    }
}
