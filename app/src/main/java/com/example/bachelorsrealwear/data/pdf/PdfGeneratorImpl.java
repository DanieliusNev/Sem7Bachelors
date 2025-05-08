package com.example.bachelorsrealwear.data.pdf;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import com.example.bachelorsrealwear.data.storage.ChecklistFormState;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PushbuttonField;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
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

        System.setProperty("com.itextpdf.xmp.disableAutomaticXmpParsing", "true");

        PdfReader reader = new PdfReader(pdfStream);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfStamper stamper = new PdfStamper(reader, baos);

        AcroFields form = stamper.getAcroFields();
        ChecklistFormState formState = ChecklistFormState.getInstance();
        Map<String, Object> answers = formState.getAllAnswers();
        Map<String, Uri> photoAnswers = formState.getAllPhotos();

        Log.d("PDF_GEN", "=== DEBUG: Answers from ChecklistFormState ===");
        for (Map.Entry<String, Object> entry : answers.entrySet()) {
            Log.d("PDF_GEN", "Answer ID: " + entry.getKey() + " = " + entry.getValue());
        }

        String mappingJson = readStreamToString(mappingStream);
        JSONObject mapping = new JSONObject(mappingJson);
        JSONArray fields = mapping.getJSONArray("fields");

        for (int i = 0; i < fields.length(); i++) {
            JSONObject field = fields.getJSONObject(i);
            String pdfId = field.getString("pdfFieldId");
            String checklistId = field.optString("checklistFieldId", "");
            String type = field.optString("type", "text");
            boolean autoFill = field.optBoolean("autoFill", false);

            //  Handle image field
            if (type.equals("photo")) {
                Uri photoUri = photoAnswers.get(checklistId);
                if (photoUri != null) {
                    InputStream imageStream = context.getContentResolver().openInputStream(photoUri);
                    byte[] imageBytes = readAllBytesCompat(imageStream);
                    Image img = Image.getInstance(imageBytes);

                    PushbuttonField btn = form.getNewPushbuttonFromField(pdfId);
                    if (btn != null) {
                        btn.setLayout(PushbuttonField.LAYOUT_ICON_ONLY);
                        btn.setImage(img);
                        form.replacePushbuttonField(pdfId, btn.getField());
                        Log.d("PDF_GEN", "✔ Inserted photo into field: " + pdfId);
                    }
                }
                continue;
            }

            // Handle text or checkbox
            String valueToFill = null;
            Object answer = answers.get(checklistId);

            if (answer != null) {
                if (type.equals("checkbox") && answer instanceof Boolean && (Boolean) answer) {
                    valueToFill = "Yes";
                } else if (answer instanceof String && !((String) answer).isEmpty()) {
                    valueToFill = (String) answer;
                }
            } else if (autoFill) {
                if (pdfId.toLowerCase().contains("date")) {
                    valueToFill = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                } else if (pdfId.toLowerCase().contains("initial")) {
                    valueToFill = "TI";
                }
            }

            if (valueToFill != null) {
                form.setField(pdfId, valueToFill);
                Log.d("PDF_GEN", "Filled " + pdfId + " = " + valueToFill);
            }
        }

        stamper.setFormFlattening(true);
        stamper.getWriter().setXmpMetadata("<?xpacket begin=''?>\n<x:xmpmeta xmlns:x='adobe:ns:meta/'><x:xmpmeta>\n<?xpacket end='w'?>".getBytes());

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

    private byte[] readAllBytesCompat(InputStream inputStream) throws Exception {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        byte[] data = new byte[4096];
        int nRead;
        while ((nRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, nRead);
        }
        return buffer.toByteArray();
    }

    @SuppressWarnings("deprecation")
    private void saveToDownloads(byte[] data, String filename) throws Exception {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, filename);
            values.put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf");
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

            ContentResolver resolver = context.getContentResolver();
            Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);

            if (uri != null) {
                try (OutputStream out = resolver.openOutputStream(uri)) {
                    if (out != null) {
                        out.write(data);
                    }
                }
            }
        } else {
            File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            if (!downloadsDir.exists()) downloadsDir.mkdirs();

            File file = new File(downloadsDir, filename);
            try (FileOutputStream fos = new FileOutputStream(file)) {
                fos.write(data);
            }

            MediaScannerConnection.scanFile(context, new String[]{file.getAbsolutePath()}, null, null);
        }

        Log.d("PDF_GEN", "PDF saved to Downloads: " + filename);
    }
}
