package com.example.bachelorsrealwear.presentation.ui.viewModel;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.ViewModel;

import com.example.bachelorsrealwear.data.pdf.PdfGeneratorImpl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PdfSaveViewModel extends ViewModel {

    public void generateAndSavePdf(Context context, int templateIndex) {
        new Thread(() -> {
            try {
                PdfGeneratorImpl pdfGenerator = new PdfGeneratorImpl(context);

                // Determine filenames based on selected template
                String pdfTemplate = "ZCH Template " + (templateIndex + 1) + ".pdf";
                String mappingJson = "pdf_mapping_template" + (templateIndex + 1) + ".json";

                // Create dynamic output name
                String time = new SimpleDateFormat("yyyy-MM-dd_HHmmss", Locale.getDefault()).format(new Date());
                String outputName = time + "_ZCH_Template_" + (templateIndex + 1) + ".pdf";

                pdfGenerator.fillAndSavePdf(pdfTemplate, mappingJson, outputName);
            } catch (Exception e) {
                Log.e("PDF_GEN", "Failed to generate PDF", e);
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() ->
                        Toast.makeText(context, "PDF error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }
}
