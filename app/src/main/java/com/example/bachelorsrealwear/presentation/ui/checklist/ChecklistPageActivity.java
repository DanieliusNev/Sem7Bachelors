package com.example.bachelorsrealwear.presentation.ui.checklist;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bachelorsrealwear.R;
import com.example.bachelorsrealwear.data.repository.ChecklistRepositoryImpl;
import com.example.bachelorsrealwear.domain.model.*;
import com.example.bachelorsrealwear.domain.usecase.LoadChecklistTemplateUseCase;

import java.util.List;

public class ChecklistPageActivity extends AppCompatActivity {

    private TextView pageTitleView;
    private LinearLayout questionContainer;
    private ChecklistPageViewModel viewModel;
    private int templateIndex;
    private int pageIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        templateIndex = getIntent().getIntExtra("template_index", 0);
        pageIndex = getIntent().getIntExtra("page_index", 0);

        // Inject repo -> usecase -> vm
        ChecklistRepositoryImpl repo = new ChecklistRepositoryImpl(getApplicationContext());
        LoadChecklistTemplateUseCase useCase = new LoadChecklistTemplateUseCase(repo);
        viewModel = new ChecklistPageViewModel(useCase);

        int layoutId = getLayoutIdForPage(pageIndex);
        setContentView(layoutId);

        pageTitleView = findViewById(R.id.tv_page_title);
        questionContainer = findViewById(R.id.questions_container);

        viewModel.getTemplate().observe(this, template -> {
            if (template != null && pageIndex < template.pages.size()) {
                ChecklistPage page = template.pages.get(pageIndex);
                pageTitleView.setText(page.title);
                renderFields(page.fields);
            }
        });

        viewModel.loadTemplate("all_checklists.json", templateIndex);

        Button nextButton = findViewById(R.id.nextStep);
        nextButton.setOnClickListener(view -> {
            if (viewModel.getTemplate().getValue() != null &&
                    pageIndex + 1 < viewModel.getTemplate().getValue().pages.size()) {
                Intent intent = new Intent(this, ChecklistPageActivity.class);
                intent.putExtra("template_index", templateIndex);
                intent.putExtra("page_index", pageIndex + 1);
                startActivity(intent);
            } else {
                Toast.makeText(this, "All pages completed", Toast.LENGTH_SHORT).show();
            }
        });

        Button backButton = findViewById(R.id.backCheck);
        backButton.setOnClickListener(view -> finish());

        if (pageIndex == 1) { // Only for the "Calibrated Tools" page
            Button createToolButton = findViewById(R.id.btn_create_tool);
            createToolButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, CreateToolActivity.class);
                startActivity(intent);
            });
        }

    }

    private void renderFields(List<ChecklistField> fields) {
        // Don't remove static views like title or create button
        // Instead, remove only dynamic field rows
        // We'll assume dynamic views are added after the static ones

        // Optional: if you want to remove previous dynamic views without affecting static layout
        // Remove all views *after* the first 2 children (title + button)
        int childCount = questionContainer.getChildCount();
        if (childCount > 2) {
            questionContainer.removeViews(2, childCount - 2);
        }

        if (fields == null || fields.isEmpty()) {
            // Skip rendering, static content will remain
            return;
        }

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
            default:
                return R.layout.activity_questions_page;
        }
    }
}
