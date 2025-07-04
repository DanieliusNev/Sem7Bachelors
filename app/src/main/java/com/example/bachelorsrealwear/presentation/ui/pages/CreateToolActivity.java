package com.example.bachelorsrealwear.presentation.ui.pages;

import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.bachelorsrealwear.R;
import com.example.bachelorsrealwear.presentation.ui.viewModel.ToolViewModel;

public class CreateToolActivity extends AppCompatActivity {

    private Spinner spinnerDescription;
    private EditText etToolNumber, etExpiryDate;
    private Button btnSaveTool, btnCancelTool;
    private ToolViewModel toolViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tool);

        toolViewModel = new ViewModelProvider(this).get(ToolViewModel.class);

        spinnerDescription = findViewById(R.id.spinner_description);
        etToolNumber = findViewById(R.id.et_tool_number);
        etExpiryDate = findViewById(R.id.et_expiry_date);
        btnSaveTool = findViewById(R.id.btn_save_tool);
        Button btnCancelTool = findViewById(R.id.btn_cancel_tool);

        String[] toolOptions = {
                "Hydraulic pump", "Pressure gauge", "Torque and tension heads", "Pump model X",
                "Tensioner set", "Torque wrench", "Hydraulic hose", "Digital gauge",
                "Calibration kit", "Oil reservoir"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                toolOptions
        );
        spinnerDescription.setAdapter(adapter);

        btnSaveTool.setOnClickListener(v -> {
            String description = spinnerDescription.getSelectedItem().toString();
            String toolNumber = etToolNumber.getText().toString().trim();
            String expiryDate = etExpiryDate.getText().toString().trim();

            if (toolNumber.isEmpty() || expiryDate.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            toolViewModel.saveTool(description, toolNumber, expiryDate);

            Toast.makeText(this, "Tool saved", Toast.LENGTH_SHORT).show();

            setResult(RESULT_OK);
            finish();
        });
        btnCancelTool.setOnClickListener(v -> {
            finish(); // Just close the activity and return to previous
        });

    }
}
