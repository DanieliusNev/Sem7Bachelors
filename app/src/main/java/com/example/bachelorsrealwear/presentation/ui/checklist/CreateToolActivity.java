package com.example.bachelorsrealwear.presentation.ui.checklist;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.example.bachelorsrealwear.R;
import com.example.bachelorsrealwear.data.storage.ToolDataStore;
import com.example.bachelorsrealwear.domain.model.ToolEntry;

public class CreateToolActivity extends AppCompatActivity {

    private Spinner spinnerDescription;
    private EditText etToolNumber, etExpiryDate;
    private Button btnSaveTool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tool);

        spinnerDescription = findViewById(R.id.spinner_description);
        etToolNumber = findViewById(R.id.et_tool_number);
        etExpiryDate = findViewById(R.id.et_expiry_date);
        btnSaveTool = findViewById(R.id.btn_save_tool);

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

            ToolEntry tool = new ToolEntry(description, toolNumber, expiryDate);
            ToolDataStore.addTool(this, tool);  // ✅ Save to shared preferences

            Toast.makeText(this, "Tool saved", Toast.LENGTH_SHORT).show();

            setResult(RESULT_OK);
            finish();
        });
    }
}
