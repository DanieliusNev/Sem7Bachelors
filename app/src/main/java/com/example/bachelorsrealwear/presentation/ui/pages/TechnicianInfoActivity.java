package com.example.bachelorsrealwear.presentation.ui.pages;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.bachelorsrealwear.R;
import com.example.bachelorsrealwear.presentation.ui.viewModel.TechnicianInfoViewModel;

public class TechnicianInfoActivity extends AppCompatActivity {

    private TechnicianInfoViewModel viewModel;

    private EditText nameField;
    private EditText employeeField;
    private EditText companyField;
    private EditText initialsField;
    private Button continueButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_technician_info);

        viewModel = new ViewModelProvider(this,
                ViewModelProvider.AndroidViewModelFactory.getInstance(getApplication()))
                .get(TechnicianInfoViewModel.class);

        // Bind UI elements
        nameField = findViewById(R.id.editTextTechnicianName);
        employeeField = findViewById(R.id.editTextEmployeeNo);
        companyField = findViewById(R.id.editTextCompanyName);
        initialsField = findViewById(R.id.editTextInitials);
        continueButton = findViewById(R.id.buttonContinue);

        // Optionally pre-fill values if going back from later screens
        nameField.setText(viewModel.getTechnicianName());

        employeeField.setText(viewModel.getEmployeeNo());
        companyField.setText(viewModel.getCompanyName());
        initialsField.setText(viewModel.getInitials());

        // Button click listener
        continueButton.setOnClickListener(v -> {
            String name = nameField.getText().toString().trim();
            String employeeNo = employeeField.getText().toString().trim();
            String companyName = companyField.getText().toString().trim();
            String initials = initialsField.getText().toString().trim();

            viewModel.saveTechnicianInfo(name, employeeNo, companyName, initials);

            // Move to the checklist page
            Intent intent = new Intent(TechnicianInfoActivity.this, ChecklistSelectorActivity.class);
            startActivity(intent);
        });
    }
}
