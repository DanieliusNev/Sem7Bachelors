package com.example.bachelorsrealwear.presentation.ui.viewModel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.example.bachelorsrealwear.data.pdf.PdfGeneratorImpl;

public class PdfSaveViewModel extends ViewModel {

    public void generateAndSavePdf(Context context) {
        new Thread(() -> {
            try {
                PdfGeneratorImpl pdfGenerator = new PdfGeneratorImpl(context);
                pdfGenerator.fillAndSavePdf("ZCH Template 1.pdf", "pdf_mapping_template1.json", "Checklist_Output.pdf");
            } catch (Exception e) {
                Log.e("PDF_GEN", "Failed to generate PDF", e);
            }
        }).start();
    }
}
