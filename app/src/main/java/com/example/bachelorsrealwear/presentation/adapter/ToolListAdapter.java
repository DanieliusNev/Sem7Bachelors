package com.example.bachelorsrealwear.presentation.adapter;

import android.content.Context;
import android.view.*;
import android.widget.*;
import com.example.bachelorsrealwear.R;
import com.example.bachelorsrealwear.domain.model.ToolEntry;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ToolListAdapter extends ArrayAdapter<ToolEntry> {
    private final List<ToolEntry> selectedItems = new ArrayList<>();

    public ToolListAdapter(Context context, List<ToolEntry> tools) {
        super(context, 0, tools);
    }

    public List<ToolEntry> getSelectedItems() {
        return selectedItems;
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
        CheckBox checkBox = convertView.findViewById(R.id.cb_select_tool);

        tvDesc.setText(tool.description);
        tvNum.setText(tool.toolNumber);
        tvExp.setText(tool.expiryDate);

        checkBox.setOnCheckedChangeListener(null); // reset listener
        checkBox.setChecked(selectedItems.contains(tool));

        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                selectedItems.add(tool);
            } else {
                selectedItems.remove(tool);
            }
        });

        return convertView;
    }
    public List<ToolEntry> getAllItems() {
        List<ToolEntry> tools = new ArrayList<>();
        for (int i = 0; i < getCount(); i++) {
            tools.add(getItem(i));
        }
        return tools;
    }

}
