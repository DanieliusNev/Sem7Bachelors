package com.example.bachelorsrealwear.presentation.ui.checklist;

import android.content.Intent;
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

    private TextView pageTitleView;
    private LinearLayout questionContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Receive current page index and template
        int templateIndex = getIntent().getIntExtra("template_index", 0);
        int pageIndex = getIntent().getIntExtra("page_index", 0);

        // Load checklist data
        ChecklistTemplate template = loadTemplateFromAssets("all_checklists.json", templateIndex);

        // Determine which layout to use based on pageIndex
        int layoutId = getLayoutIdForPage(pageIndex);
        setContentView(layoutId);

        // Bind views
        pageTitleView = findViewById(R.id.tv_page_title);
        questionContainer = findViewById(R.id.questions_container);

        if (template != null && pageIndex < template.pages.size()) {
            ChecklistPage currentPage = template.pages.get(pageIndex);
            pageTitleView.setText(currentPage.title);
            renderFields(currentPage.fields);

            Button nextButton = findViewById(R.id.nextStep);
            nextButton.setOnClickListener(view -> {
                if (pageIndex + 1 < template.pages.size()) {
                    Intent intent = new Intent(ChecklistPageActivity.this, ChecklistPageActivity.class);
                    intent.putExtra("template_index", templateIndex);
                    intent.putExtra("page_index", pageIndex + 1);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "All pages completed", Toast.LENGTH_SHORT).show();
                }
            });

            Button backButton = findViewById(R.id.backCheck);
            backButton.setOnClickListener(view -> finish());
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
        questionContainer.removeAllViews();

        for (ChecklistField field : fields) {
            LinearLayout row = new LinearLayout(this);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
            row.setPadding(0, 8, 0, 8);

            TextView label = new TextView(this);
            label.setText(field.label);
            label.setTextSize(16);
            label.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1));
            row.addView(label);

            if ("text".equalsIgnoreCase(field.type)) {
                EditText input = new EditText(this);
                input.setHint(field.placeholder);
                input.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.5f));
                row.addView(input);
            } else if ("dropdown".equalsIgnoreCase(field.type)) {
                Spinner spinner = new Spinner(this);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                        android.R.layout.simple_spinner_dropdown_item, field.options);
                spinner.setAdapter(adapter);
                spinner.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.5f));
                row.addView(spinner);
            }

            questionContainer.addView(row);
        }
    }

    private int getLayoutIdForPage(int pageIndex) {
        switch (pageIndex) {
            case 0:
                return R.layout.activity_questions_page;
            case 1:
                return R.layout.activity_questions_page2;
            //case 2:
                //return R.layout.activity_questions_page3;
            // Add more pages as needed...
            default:
                return R.layout.activity_questions_page;
        }
    }
}
