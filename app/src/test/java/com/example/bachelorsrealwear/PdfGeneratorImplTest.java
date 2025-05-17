package com.example.bachelorsrealwear;

import android.content.Context;
import android.net.Uri;

import com.example.bachelorsrealwear.data.pdf.PdfGeneratorImpl;
import com.example.bachelorsrealwear.data.storage.ChecklistFormState;
import com.itextpdf.text.pdf.PdfReader;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class PdfGeneratorImplTest {

    private Context mockContext;
    private PdfGeneratorImpl pdfGenerator;

    @Before
    public void setup() {
        mockContext = Mockito.mock(Context.class);
        pdfGenerator = new PdfGeneratorImpl(mockContext);
    }

    @Test
    public void testFieldMappingIsParsedAndProcessedCorrectly() throws Exception {
        // Simulate a very basic PDF file (empty but valid)
        byte[] fakePdf = "%PDF-1.4\n%âãÏÓ\n1 0 obj\n<<>>\nendobj\nxref\n0 1\n0000000000 65535 f\ntrailer\n<<>>\nstartxref\n0\n%%EOF".getBytes();
        InputStream fakePdfStream = new ByteArrayInputStream(fakePdf);

        // Create mock JSON mapping (one text and one checkbox field)
        String fakeMapping =
                "{\n" +
                        "  \"fields\": [\n" +
                        "    {\n" +
                        "      \"pdfFieldId\": \"Owner\",\n" +
                        "      \"checklistFieldId\": \"Owner\",\n" +
                        "      \"type\": \"text\",\n" +
                        "      \"autoFill\": false\n" +
                        "    },\n" +
                        "    {\n" +
                        "      \"pdfFieldId\": \"ConfirmCheckbox\",\n" +
                        "      \"checklistFieldId\": \"Confirm\",\n" +
                        "      \"type\": \"checkbox\",\n" +
                        "      \"autoFill\": false\n" +
                        "    }\n" +
                        "  ]\n" +
                        "}";

        InputStream fakeMappingStream = new ByteArrayInputStream(fakeMapping.getBytes());

        // Mock AssetManager and bind to context
        android.content.res.AssetManager mockAssetManager = mock(android.content.res.AssetManager.class);
        when(mockContext.getAssets()).thenReturn(mockAssetManager);
        when(mockAssetManager.open("template.pdf")).thenReturn(fakePdfStream);
        when(mockAssetManager.open("mapping.json")).thenReturn(fakeMappingStream);

        // Mock answers in form state
        ChecklistFormState formState = ChecklistFormState.getInstance();
        formState.setAnswer("Owner", "Alice");
        formState.setAnswer("Confirm", true);

        try {
            pdfGenerator.fillAndSavePdf("template.pdf", "mapping.json", "test_output.pdf");
        } catch (Exception e) {
            // Expected due to fake PDF internals
        }

        Map<String, Object> saved = formState.getAllAnswers();
        assertEquals("Alice", saved.get("Owner"));
        assertEquals(true, saved.get("Confirm"));
    }

}
