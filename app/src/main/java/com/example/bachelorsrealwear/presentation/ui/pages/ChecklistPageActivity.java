package com.example.bachelorsrealwear.presentation.ui.pages;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.bachelorsrealwear.R;
import com.example.bachelorsrealwear.presentation.ui.viewModel.ChecklistPageViewModel;
import com.example.bachelorsrealwear.presentation.ui.viewModel.ChecklistPageViewModelFactory;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChecklistPageActivity extends AppCompatActivity {

    private ChecklistPageViewModel viewModel;
    private LinearLayout questionContainer;
    private TextView pageTitleView;
    private final Map<String, View> inputViews = new HashMap<>();

    private int templateIndex;
    private int pageIndex;
    private ListView toolListView;
    private Button createToolButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        templateIndex = getIntent().getIntExtra("template_index", 0);
        pageIndex = getIntent().getIntExtra("page_index", 0);
        ChecklistPageViewModelFactory factory = new ChecklistPageViewModelFactory(getApplicationContext());
        viewModel = new ViewModelProvider(this, factory).get(ChecklistPageViewModel.class);

        setContentView(getLayoutIdForPage(pageIndex));
        questionContainer = findViewById(R.id.questions_container);
        pageTitleView = findViewById(R.id.tv_page_title);

        viewModel.loadTemplate("all_checklists.json", templateIndex, pageIndex);

        viewModel.getPageTitle().observe(this, title -> pageTitleView.setText(title));

        viewModel.getFieldIds().observe(this, ids -> {
            List<String> labels = viewModel.getFieldLabels().getValue();
            List<String> types = viewModel.getFieldTypes().getValue();
            List<List<String>> options = viewModel.getFieldOptions().getValue();
            List<String> placeholders = viewModel.getFieldPlaceholders().getValue();

            if (ids != null && labels != null && types != null && options != null && placeholders != null &&
                    ids.size() == labels.size() && ids.size() == types.size() &&
                    ids.size() == options.size() && ids.size() == placeholders.size()) {
                renderFields(ids, labels, types, options, placeholders);
            }
        });

        Button nextButton = findViewById(R.id.nextStep);
        if (nextButton != null) {
            nextButton.setOnClickListener(v -> onNextClicked());
        }

        Button backButton = findViewById(R.id.backCheck);
        if (backButton != null) {
            backButton.setOnClickListener(v -> {
                viewModel.saveAnswers(collectAnswersFromViews());
                finish();
            });
        }

        if (pageIndex == 1) {
            createToolButton = findViewById(R.id.btn_create_tool);
            toolListView = findViewById(R.id.lv_tool_table);

            createToolButton.setOnClickListener(v -> {
                Intent intent = new Intent(this, CreateToolActivity.class);
                startActivityForResult(intent, 101);
            });

            viewModel.getToolAdapter().observe(this, adapter -> {
                toolListView.setAdapter(adapter);
            });


            viewModel.loadTools(this);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && pageIndex == 1) {
            viewModel.loadTools(this);
        }
    }

    private void onNextClicked() {
        viewModel.saveAnswers(collectAnswersFromViews());
        int totalPages = viewModel.getTemplate().pages.size();
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

    private Map<String, Object> collectAnswersFromViews() {
        Map<String, Object> result = new HashMap<>();
        for (Map.Entry<String, View> entry : inputViews.entrySet()) {
            View view = entry.getValue();
            if (view instanceof EditText) {
                result.put(entry.getKey(), ((EditText) view).getText().toString());
            } else if (view instanceof Spinner) {
                result.put(entry.getKey(), ((Spinner) view).getSelectedItem().toString());
            } else if (view instanceof CheckBox) {
                result.put(entry.getKey(), ((CheckBox) view).isChecked());
            } else if (view instanceof ChipGroup) {
                ChipGroup group = (ChipGroup) view;
                int selectedId = group.getCheckedChipId();
                if (selectedId != -1) {
                    Chip chip = group.findViewById(selectedId);
                    if (chip != null) result.put(entry.getKey(), chip.getText().toString());
                }
            }
        }
        return result;
    }

    private void renderFields(List<String> ids, List<String> labels, List<String> types, List<List<String>> optionsList, List<String> placeholders) {
        int childCount = questionContainer.getChildCount();
        if (childCount > 2) {
            questionContainer.removeViews(2, childCount - 2);
        }

        inputViews.clear();
        for (int i = 0; i < ids.size(); i++) {
            String fieldId = ids.get(i);
            String label = labels.get(i);
            String type = types.get(i);
            List<String> options = optionsList.get(i);
            String placeholder = placeholders.get(i);

            Object savedValue = viewModel.getAnswer(fieldId);

            if ("checkbox".equalsIgnoreCase(type)) {
                CheckBox checkBox = new CheckBox(this);
                checkBox.setText(label);
                checkBox.setChecked(savedValue instanceof Boolean && (Boolean) savedValue);
                inputViews.put(fieldId, checkBox);
                questionContainer.addView(checkBox);

            } else if ("yesno".equalsIgnoreCase(type)) {
                TextView labelView = new TextView(this);
                labelView.setText(label);
                labelView.setTextSize(16);
                questionContainer.addView(labelView);

                ChipGroup chipGroup = new ChipGroup(this);
                chipGroup.setSingleSelection(true);

                Chip yes = new Chip(this);
                yes.setText("Yes");
                yes.setCheckable(true);
                chipGroup.addView(yes);

                Chip no = new Chip(this);
                no.setText("No");
                no.setCheckable(true);
                chipGroup.addView(no);

                if ("Yes".equals(savedValue)) yes.setChecked(true);
                else if ("No".equals(savedValue)) no.setChecked(true);

                inputViews.put(fieldId, chipGroup);
                questionContainer.addView(chipGroup);

            } else {
                LinearLayout row = new LinearLayout(this);
                row.setOrientation(LinearLayout.HORIZONTAL);
                row.setLayoutParams(new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT));
                row.setPadding(0, 8, 0, 8);

                TextView labelView = new TextView(this);
                labelView.setText(label);
                labelView.setTextSize(16);
                labelView.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));
                row.addView(labelView);

                if ("text".equalsIgnoreCase(type)) {
                    EditText input = new EditText(this);
                    input.setHint(placeholder != null ? placeholder : label);
                    if (savedValue instanceof String) input.setText((String) savedValue);
                    input.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f));
                    input.setMinWidth(250);
                    inputViews.put(fieldId, input);
                    row.addView(input);

                } else if ("dropdown".equalsIgnoreCase(type)) {
                    Spinner spinner = new Spinner(this);
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                            android.R.layout.simple_spinner_dropdown_item, options);
                    spinner.setAdapter(adapter);
                    if (savedValue instanceof String) {
                        int index = options.indexOf(savedValue);
                        if (index >= 0) spinner.setSelection(index);
                    }
                    spinner.setLayoutParams(new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 2f));
                    inputViews.put(fieldId, spinner);
                    row.addView(spinner);
                }

                questionContainer.addView(row);
            }
        }
    }

    private int getLayoutIdForPage(int index) {
        switch (index) {
            case 0: return R.layout.activity_questions_page;
            case 1: return R.layout.activity_questions_page2;
            case 2: return R.layout.activity_questions_page3;
            case 3: return R.layout.activity_questions_page4;
            case 4: return R.layout.activity_questions_page5;
            case 5: return R.layout.activity_questions_page6;
            default: return R.layout.activity_questions_page;
        }
    }
}
