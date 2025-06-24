package com.example.bachelorsrealwear.presentation.ui.pages;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.lifecycle.ViewModelProvider;

import com.example.bachelorsrealwear.R;
import com.example.bachelorsrealwear.presentation.ui.viewModel.PdfSaveViewModel;

import java.io.File;

public class PdfSaveActivity extends AppCompatActivity {

    private PdfSaveViewModel viewModel;
    private static final int STORAGE_PERMISSION_CODE = 2001;
    private int templateIndex;
    private static final int PICK_PDF_FILE = 1002;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf_save);

        viewModel = new ViewModelProvider(this).get(PdfSaveViewModel.class);
        templateIndex = getIntent().getIntExtra("template_index", 0);

        // ✅ Prepare the filename once when activity starts
        viewModel.prepareFileName(templateIndex);

        // ✅ Show the filename in TextView
        TextView fileNameTv = findViewById(R.id.tv_pdf_filename);
        fileNameTv.setText("Checklist will be saved as: " + viewModel.getGeneratedFileName());

        Button downloadBtn = findViewById(R.id.btn_download_pdf);
        downloadBtn.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(
                            this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            STORAGE_PERMISSION_CODE
                    );
                } else {
                    viewModel.generateAndSavePdf(this, templateIndex);
                }
            } else {
                viewModel.generateAndSavePdf(this, templateIndex);
            }
        });
        Button openBtn = findViewById(R.id.btn_open_pdf);
        openBtn.setEnabled(false); // ⛔ Disabled by default

        viewModel.getIsPdfReady().observe(this, isReady -> {
            openBtn.setEnabled(isReady != null && isReady);
        });
        openBtn.setOnClickListener(v -> openFilePicker());





        Button newChecklistBtn = findViewById(R.id.btn_new_checklist);
        newChecklistBtn.setOnClickListener(v -> {
            viewModel.resetPdfReadyState(); // ⛔ Reset flag
            Intent intent = new Intent(this, ChecklistSelectorActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        });

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                viewModel.generateAndSavePdf(this, templateIndex);
            } else {
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/pdf"); // Filter for PDFs only
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true); // Optional
        startActivityForResult(intent, PICK_PDF_FILE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_PDF_FILE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                Intent openIntent = new Intent(Intent.ACTION_VIEW);
                openIntent.setDataAndType(uri, "application/pdf");
                openIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                try {
                    startActivity(openIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(this, "No PDF viewer found", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }


}
