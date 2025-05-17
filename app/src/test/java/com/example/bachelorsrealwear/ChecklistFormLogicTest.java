package com.example.bachelorsrealwear;

import com.example.bachelorsrealwear.data.storage.ChecklistFormState;

import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import static org.junit.Assert.*;

public class ChecklistFormLogicTest {

    private ChecklistFormState formState;

    @Before
    public void setup() {
        formState = ChecklistFormState.getInstance();
        formState.getAllAnswers().clear(); // Clear any previous state
    }

    @Test
    public void testSavingAndRetrievingAnswer() {
        formState.setAnswer("Owner", "John Doe");
        formState.setAnswer("WTG", "WTG 5");

        Map<String, Object> answers = formState.getAllAnswers();

        assertEquals("John Doe", answers.get("Owner"));
        assertEquals("WTG 5", answers.get("WTG"));
    }

    @Test
    public void testToolEntrySimulation() {
        formState.setAnswer("Tool_001", "Hydraulic Wrench – Calibrated");

        String storedValue = (String) formState.getAllAnswers().get("Tool_001");

        assertNotNull(storedValue);
        assertTrue(storedValue.contains("Calibrated"));
    }

    @Test
    public void testAutoFillDateFieldFormat() {
        String fieldId = "InspectionDate";
        boolean autoFill = true;

        String valueToFill = null;
        if (autoFill && fieldId.toLowerCase().contains("date")) {
            valueToFill = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        }

        assertNotNull(valueToFill);
        assertTrue(valueToFill.matches("\\d{4}-\\d{2}-\\d{2}")); // yyyy-MM-dd format
    }
}
