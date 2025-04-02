// ChecklistPageActivity.java
package com.example.bachelorsrealwear.presentation.ui.checklist;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bachelorsrealwear.R;
import com.example.bachelorsrealwear.data.storage.ToolDataStore;
import com.example.bachelorsrealwear.data.repository.ChecklistRepositoryImpl;
import com.example.bachelorsrealwear.domain.model.ChecklistField;
import com.example.bachelorsrealwear.domain.model.ChecklistPage;
import com.example.bachelorsrealwear.domain.model.ToolEntry;
import com.example.bachelorsrealwear.domain.usecase.LoadChecklistTemplateUseCase;
import com.example.bachelorsrealwear.presentation.adapter.ToolListAdapter;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class ChecklistPageActivity extends AppCompatActivity {
    private static final int CREATE_TOOL_REQUEST = 101;

    private TextView pageTitleView;
    private LinearLayout questionContainer;
    private ListView toolListView;
    private ArrayAdapter<String> listAdapter;

    private ChecklistPageViewModel viewModel;
    private int templateIndex;
    private int pageIndex;

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

        viewModel.getTemplate().observe(this, template -> {
            if (template != null && pageIndex < template.pages.size()) {
                ChecklistPage page = template.pages.get(pageIndex);
                pageTitleView.setText(page.title);
                renderFields(page.fields);
            }
        });

        viewModel.loadTemplate("all_checklists.json", templateIndex);

        Button nextButton = findViewById(R.id.nextStep);
        if (nextButton != null) {
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
        }

        Button backButton = findViewById(R.id.backCheck);
        if (backButton != null) {
            backButton.setOnClickListener(view -> finish());
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

        if (fields == null || fields.isEmpty()) return;

        for (ChecklistField field : fields) {
            if ("checkbox".equalsIgnoreCase(field.type)) {
                // For checkbox, only add the checkbox with inline text
                CheckBox checkBox = new CheckBox(this);
                checkBox.setText(field.label);
                checkBox.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT
                ));
                questionContainer.addView(checkBox);
            } else if ("yesno".equalsIgnoreCase(field.type)) {
                TextView label = new TextView(this);
                label.setText(field.label);
                label.setTextSize(16);
                questionContainer.addView(label);

                ChipGroup chipGroup = new ChipGroup(this);
                //chipGroup.setOrientation(ChipGroup.HORIZONTAL);
                chipGroup.setSingleSelection(true);

                Chip yesChip = new Chip(this);
                yesChip.setText("Yes");
                yesChip.setCheckable(true);

                Chip noChip = new Chip(this);
                noChip.setText("No");
                noChip.setCheckable(true);

                chipGroup.addView(yesChip);
                chipGroup.addView(noChip);
                questionContainer.addView(chipGroup);
            }
            else {
                // For text and dropdown, use a row layout with a label
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
    }


    private int getLayoutIdForPage(int pageIndex) {
        switch (pageIndex) {
            case 0:
                return R.layout.activity_questions_page;
            case 1:
                return R.layout.activity_questions_page2;
            case 2:
                return R.layout.activity_questions_page3;
            default:
                return R.layout.activity_questions_page;
        }
    }
}
