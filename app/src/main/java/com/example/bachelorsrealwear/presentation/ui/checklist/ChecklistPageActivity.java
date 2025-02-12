package com.example.bachelorsrealwear.presentation.ui.checklist;

import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bachelorsrealwear.R;
import com.example.bachelorsrealwear.domain.model.*;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.List;

public class ChecklistPageActivity extends AppCompatActivity {

    private LinearLayout dynamicLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions_page);

        dynamicLayout = findViewById(R.id.ll_dynamic_fields);
        int templateIndex = getIntent().getIntExtra("template_index", 0);

        ChecklistTemplate template = loadTemplateFromAssets("all_checklists.json", templateIndex);
        if (template != null && template.pages != null && !template.pages.isEmpty()) {
            ChecklistPage page1 = template.pages.get(0);
            renderFields(page1.fields);
        }
    }

    private ChecklistTemplate loadTemplateFromAssets(String filename, int index) {
        try {
            InputStream inputStream = getAssets().open(filename);
            InputStreamReader reader = new InputStreamReader(inputStream);
            Type type = new TypeToken<ChecklistLibrary>() {}.getType();
            ChecklistLibrary library = new Gson().fromJson(reader, type);
            return library.templates.get(index);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void renderFields(List<ChecklistField> fields) {
        for (ChecklistField field : fields) {
            TextView label = new TextView(this);
            label.setText(field.label);
            label.setTextSize(16);
            dynamicLayout.addView(label);

            if ("text".equalsIgnoreCase(field.type)) {
                EditText input = new EditText(this);
                input.setHint(field.placeholder);
                dynamicLayout.addView(input);
            } else if ("dropdown".equalsIgnoreCase(field.type)) {
                Spinner spinner = new Spinner(this);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(
                        this,
                        android.R.layout.simple_spinner_dropdown_item,
                        field.options
                );
                spinner.setAdapter(adapter);
                dynamicLayout.addView(spinner);
            }
        }
    }
}
