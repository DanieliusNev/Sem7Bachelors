package com.example.bachelorsrealwear.presentation.ui.viewModel;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bachelorsrealwear.data.pdf.PdfGeneratorImpl;
import com.example.bachelorsrealwear.domain.service.PdfService;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class PdfSaveViewModel extends ViewModel {

    private String generatedFileName;
    private final MutableLiveData<Boolean> isPdfReady = new MutableLiveData<>(false);

    public void prepareFileName(int templateIndex) {
        String time = new SimpleDateFormat("yyyy-MM-dd_HHmmss", Locale.getDefault()).format(new Date());
        generatedFileName = time + "_ZCH_Template_" + (templateIndex + 1) + ".pdf";
    }

    public String getGeneratedFileName() {
        return generatedFileName;
    }

    public LiveData<Boolean> getIsPdfReady() {
        return isPdfReady;
    }

    public void resetPdfReadyState() {
        isPdfReady.setValue(false); // ⛔ Reset when leaving the screen
    }

    public void generateAndSavePdf(Context context, int templateIndex) {
        new Thread(() -> {
            try {
                PdfService pdfGenerator = new PdfGeneratorImpl(context);

                String pdfTemplate = "ZCH Template " + (templateIndex + 1) + ".pdf";
                String mappingJson = "pdf_mapping_template" + (templateIndex + 1) + ".json";

                pdfGenerator.fillAndSavePdf(pdfTemplate, mappingJson, generatedFileName);

                Log.d("PDF_GEN", "PDF saved to: " +
                        new File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), generatedFileName).getAbsolutePath());

                isPdfReady.postValue(true); // ✅ Enable the button

            } catch (Exception e) {
                Log.e("PDF_GEN", "Failed to generate PDF", e);
                new android.os.Handler(android.os.Looper.getMainLooper()).post(() ->
                        Toast.makeText(context, "PDF error: " + e.getMessage(), Toast.LENGTH_LONG).show()
                );
            }
        }).start();
    }
}
