// ChecklistPageActivity.java
package com.example.bachelorsrealwear.presentation.ui.pages;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bachelorsrealwear.R;
import com.example.bachelorsrealwear.data.repository.ChecklistRepositoryImpl;
import com.example.bachelorsrealwear.data.storage.ChecklistFormState;
import com.example.bachelorsrealwear.data.storage.ToolDataStore;
import com.example.bachelorsrealwear.domain.model.ChecklistField;
import com.example.bachelorsrealwear.domain.model.ChecklistPage;
import com.example.bachelorsrealwear.domain.model.ToolEntry;
import com.example.bachelorsrealwear.domain.usecase.LoadChecklistTemplateUseCase;
import com.example.bachelorsrealwear.presentation.adapter.ToolListAdapter;
import com.example.bachelorsrealwear.presentation.ui.viewModel.ChecklistPageViewModel;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChecklistPageActivity extends AppCompatActivity {
    private static final int CREATE_TOOL_REQUEST = 101;

    private TextView pageTitleView;
    private LinearLayout questionContainer;
    private ListView toolListView;
    private ChecklistPageViewModel viewModel;
    private int templateIndex;
    private int pageIndex;
    private final Map<String, View> inputViews = new HashMap<>();
    private List<ChecklistField> currentFields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        templateIndex = getIntent().getIntExtra("template_index", 0);
        pageIndex = getIntent().getIntExtra("page_index", 0);

        ChecklistRepositoryImpl repo = new ChecklistRepositoryImpl(getApplicationContext());
        LoadChecklistTemplateUseCase useCase = new LoadChecklistTemplateUseCase(repo);
        viewModel = new ChecklistPageViewModel(useCase);

        int layoutId = getLayoutIdForPage(pageIndex);
        setContentView(layoutId);

        pageTitleView = findViewById(R.id.tv_page_title);
        questionContainer = findViewById(R.id.questions_container);

        viewModel.loadTemplate("all_checklists.json", templateIndex);

        viewModel.getTemplate().observe(this, template -> {
            if (template != null && pageIndex < template.pages.size()) {
                ChecklistPage page = template.pages.get(pageIndex);
                pageTitleView.setText(page.title);
                currentFields = page.fields;
                renderFields(page.fields);
            }
        });

        Button nextButton = findViewById(R.id.nextStep);
        if (nextButton != null) {
            nextButton.setOnClickListener(view -> {
                if (currentFields != null) {
                    viewModel.saveAllAnswers(currentFields, inputViews);
                }
                Log.d("DEBUG_FORM", "--- Saved answers on ChecklistPageActivity ---");
                for (Map.Entry<String, Object> entry : ChecklistFormState.getInstance().getAllAnswers().entrySet()) {
                    Log.d("DEBUG_FORM", entry.getKey() + " = " + entry.getValue());
                }

                if (viewModel.getTemplate().getValue() != null) {
                    int totalPages = viewModel.getTemplate().getValue().pages.size();

                    if (pageIndex + 1 < totalPages) {
                        Intent intent = new Intent(this, ChecklistPageActivity.class);
                        intent.putExtra("template_index", templateIndex);
                        intent.putExtra("page_index", pageIndex + 1);
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(this, PhotoCaptureActivity.class);
                        intent.putExtra("template_index", templateIndex);
                        startActivity(intent);
                        finish();
                    }
                }
            });
        }

        Button backButton = findViewById(R.id.backCheck);
        if (backButton != null) {
            backButton.setOnClickListener(view -> {
                if (currentFields != null) {
                    viewModel.saveAllAnswers(currentFields, inputViews);
                }
                finish();
            });
        }

        if (pageIndex == 1) {
            Button createToolButton = findViewById(R.id.btn_create_tool);
            toolListView = findViewById(R.id.lv_tool_table);

            createToolButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, CreateToolActivity.class);
                startActivityForResult(intent, CREATE_TOOL_REQUEST);
            });

            refreshToolList();
        }
    }

    private void refreshToolList() {
        List<ToolEntry> tools = ToolDataStore.loadTools(this);
        ToolListAdapter adapter = new ToolListAdapter(this, tools);
        toolListView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_TOOL_REQUEST && resultCode == RESULT_OK) {
            refreshToolList();
        }
    }

    private void renderFields(List<ChecklistField> fields) {
        int childCount = questionContainer.getChildCount();
        if (childCount > 2) {
            questionContainer.removeViews(2, childCount - 2);
        }

        inputViews.clear();

        if (fields == null || fields.isEmpty()) return;

        for (ChecklistField field : fields) {
            Object savedValue = viewModel.getSavedAnswer(field.id);

            if ("checkbox".equalsIgnoreCase(field.type)) {
                CheckBox checkBox = new CheckBox(this);
                checkBox.setText(field.label);
                checkBox.setChecked(savedValue instanceof Boolean && (Boolean) savedValue);
                inputViews.put(field.id, checkBox);
                questionContainer.addView(checkBox);

            } else if ("yesno".equalsIgnoreCase(field.type)) {
                TextView label = new TextView(this);
                label.setText(field.label);
                label.setTextSize(16);
                questionContainer.addView(label);

                ChipGroup chipGroup = new ChipGroup(this);
                chipGroup.setSingleSelection(true);

                Chip yesChip = new Chip(this);
                yesChip.setText("Yes");
                yesChip.setCheckable(true);

                Chip noChip = new Chip(this);
                noChip.setText("No");
                noChip.setCheckable(true);

                chipGroup.addView(yesChip);
                chipGroup.addView(noChip);

                if ("Yes".equals(savedValue)) yesChip.setChecked(true);
                else if ("No".equals(savedValue)) noChip.setChecked(true);

                inputViews.put(field.id, chipGroup);
                questionContainer.addView(chipGroup);

            } else {
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
                    if (savedValue instanceof String) input.setText((String) savedValue);
                    input.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1.5f));
                    inputViews.put(field.id, input);
                    row.addView(input);

                } else if ("dropdown".equalsIgnoreCase(field.type)) {
                    Spinner spinner = new Spinner(this);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                            android.R.layout.simple_spinner_dropdown_item, field.options);
                    spinner.setAdapter(adapter);
                    if (savedValue instanceof String) {
                        int index = field.options.indexOf(savedValue);
                        if (index >= 0) spinner.setSelection(index);
                    }
                    inputViews.put(field.id, spinner);
                    row.addView(spinner);
                }
                questionContainer.addView(row);
            }
        }
    }

    private int getLayoutIdForPage(int pageIndex) {
        switch (pageIndex) {
            case 0:
                return R.layout.activity_questions_page;
            case 1:
                return R.layout.activity_questions_page2;
            case 2:
                return R.layout.activity_questions_page3;
            case 3:
                return R.layout.activity_questions_page4;
            case 4:
                return R.layout.activity_questions_page5;
            case 5:
                return R.layout.activity_questions_page6;
            default:
                return R.layout.activity_questions_page;
        }
    }
}
