package com.example.bachelorsrealwear.domain.repository;

import android.content.Context;
import com.example.bachelorsrealwear.domain.model.ToolEntry;

import java.util.List;

public interface ToolRepository {
    void addTool(Context context, ToolEntry tool);
    List<ToolEntry> loadTools(Context context);
    void saveTools(Context context, List<ToolEntry> tools);
}
