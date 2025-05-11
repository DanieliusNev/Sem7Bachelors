package com.example.bachelorsrealwear.data.repository;

import android.content.Context;
import com.example.bachelorsrealwear.domain.model.ChecklistLibrary;
import com.example.bachelorsrealwear.domain.model.ChecklistTemplate;
import com.example.bachelorsrealwear.domain.repository.ChecklistRepository;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

public class ChecklistRepositoryImpl implements ChecklistRepository {

    private final Context context;

    public ChecklistRepositoryImpl(Context context) {
        this.context = context;
    }

    @Override
    public ChecklistTemplate loadTemplate(String filename, int index) {
        try {
            InputStream inputStream = context.getAssets().open(filename);
            InputStreamReader reader = new InputStreamReader(inputStream);
            Type type = new TypeToken<ChecklistLibrary>() {}.getType();
            ChecklistLibrary library = new Gson().fromJson(reader, type);
            return library.templates.get(index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
