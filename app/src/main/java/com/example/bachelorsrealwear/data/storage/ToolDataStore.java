package com.example.bachelorsrealwear.data.storage;

import android.content.Context;
import android.content.SharedPreferences;
import com.example.bachelorsrealwear.domain.model.ToolEntry;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class ToolDataStore {
    private static final String PREF_NAME = "ToolPrefs";
    private static final String KEY_TOOLS = "tool_list";

    public static void addTool(Context context, ToolEntry tool) {
        List<ToolEntry> tools = loadTools(context);
        tools.add(tool);
        saveTools(context, tools);
    }

    public static List<ToolEntry> loadTools(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_TOOLS, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<List<ToolEntry>>() {}.getType();
        return new Gson().fromJson(json, type);
    }

    public static void saveTools(Context context, List<ToolEntry> tools) {
        SharedPreferences prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_TOOLS, new Gson().toJson(tools)).apply();
    }
}

