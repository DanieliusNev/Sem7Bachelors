package com.example.bachelorsrealwear.presentation.ui.checklist;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.bachelorsrealwear.R;
import com.example.bachelorsrealwear.data.storage.ToolDataHolder;
import com.example.bachelorsrealwear.domain.model.ToolEntry;
import com.example.bachelorsrealwear.domain.repository.ToolRepository;

public class CreateToolActivity extends AppCompatActivity {

    private Spinner spinnerDescription;
    private EditText etToolNumber, etExpiryDate;
    private Button btnSaveTool;
    private ToolRepository toolRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_tool);

        // Initialize views
        spinnerDescription = findViewById(R.id.spinner_description);
        etToolNumber = findViewById(R.id.et_tool_number);
        etExpiryDate = findViewById(R.id.et_expiry_date);
        btnSaveTool = findViewById(R.id.btn_save_tool);

        // Sample dropdown options from PDF
        String[] toolOptions = {
                "Hydraulic pump",
                "Pressure gauge",
                "Torque and tension heads",
                "Pump model X",
                "Tensioner set",
                "Torque wrench",
                "Hydraulic hose",
                "Digital gauge",
                "Calibration kit",
                "Oil reservoir"
        };

        // Set up spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                toolOptions
        );
        spinnerDescription.setAdapter(adapter);

        // Get repository
        toolRepository = ToolDataHolder.getInstance().getRepository();

        // Handle save button
        btnSaveTool.setOnClickListener(v -> {
            String description = spinnerDescription.getSelectedItem().toString();
            String toolNumber = etToolNumber.getText().toString().trim();
            String expiryDate = etExpiryDate.getText().toString().trim();

            if (toolNumber.isEmpty() || expiryDate.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            ToolEntry tool = new ToolEntry(description, toolNumber, expiryDate);
            toolRepository.addTool(tool);

            Toast.makeText(this, "Tool saved", Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
