package com.example.bachelorsrealwear.data.pdf;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.example.bachelorsrealwear.data.storage.ChecklistFormState;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class PdfGeneratorImpl {
    private final Context context;

    public PdfGeneratorImpl(Context context) {
        this.context = context;
    }

    public void fillAndSavePdf(String pdfTemplateName, String mappingFileName, String outputFileName) throws Exception {
        InputStream pdfStream = context.getAssets().open(pdfTemplateName);
        InputStream mappingStream = context.getAssets().open(mappingFileName);

        PdfReader reader = new PdfReader(pdfStream);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfStamper stamper = new PdfStamper(reader, baos);

        AcroFields form = stamper.getAcroFields();
        ChecklistFormState formState = ChecklistFormState.getInstance();

        String mappingJson = readStreamToString(mappingStream);
        JSONObject mapping = new JSONObject(mappingJson);
        JSONArray fields = mapping.getJSONArray("fields");

        for (int i = 0; i < fields.length(); i++) {
            JSONObject field = fields.getJSONObject(i);
            String pdfId = field.getString("pdfFieldId");
            String type = field.getString("type");
            boolean autoFill = field.optBoolean("autoFill", false);
            String checklistId = field.optString("checklistFieldId", "");

            String valueToFill = "";

            if (autoFill) {
                if (pdfId.toLowerCase().contains("date")) {
                    valueToFill = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                } else if (pdfId.toLowerCase().contains("initial")) {
                    valueToFill = "TI"; // placeholder for initials
                } else {
                    continue;
                }
            } else {
                Object answer = formState.getAnswer(checklistId);
                if (answer instanceof Boolean && (Boolean) answer) {
                    valueToFill = "Yes";
                } else if (answer instanceof String) {
                    valueToFill = (String) answer;
                } else {
                    continue; // Skip null or unchecked fields
                }
            }

            form.setField(pdfId, valueToFill);
        }

        stamper.setFormFlattening(true);
        stamper.close();
        reader.close();

        saveToDownloads(baos.toByteArray(), outputFileName);
    }

    private String readStreamToString(InputStream is) throws Exception {
        StringBuilder builder = new StringBuilder();
        int ch;
        while ((ch = is.read()) != -1) {
            builder.append((char) ch);
        }
        return builder.toString();
    }

    private void saveToDownloads(byte[] data, String filename) throws Exception {
        File downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        if (!downloads.exists()) downloads.mkdirs();
        File file = new File(downloads, filename);

        FileOutputStream fos = new FileOutputStream(file);
        fos.write(data);
        fos.flush();
        fos.close();

        Log.d("PDF_GEN", "Saved PDF to: " + file.getAbsolutePath());
    }
}
