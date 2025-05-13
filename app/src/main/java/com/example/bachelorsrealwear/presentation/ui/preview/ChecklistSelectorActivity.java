package com.example.bachelorsrealwear.presentation.ui.preview;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bachelorsrealwear.R;
import com.example.bachelorsrealwear.presentation.ui.pages.ChecklistPageActivity;

public class ChecklistSelectorActivity extends AppCompatActivity {

    private Spinner spinnerTemplates;
    private Button btnContinue;

    private String[] checklistOptions = {
            "Checklist 1: Tip End – C-yoke",
            "Checklist 2: Radio Remote Control (RRC)",
            "Checklist 3: Rigging and Root End"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checklist_selector);

        spinnerTemplates = findViewById(R.id.spinner_templates);
        btnContinue = findViewById(R.id.btn_continue);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                checklistOptions
        );
        spinnerTemplates.setAdapter(adapter);

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selectedIndex = spinnerTemplates.getSelectedItemPosition();
                String selectedTemplate = checklistOptions[selectedIndex];

                // Pass the index or ID of the selected checklist to the next screen
                Intent intent = new Intent(ChecklistSelectorActivity.this, ChecklistPageActivity.class);
                intent.putExtra("template_index", selectedIndex);
                startActivity(intent);
            }
        });
    }
}
