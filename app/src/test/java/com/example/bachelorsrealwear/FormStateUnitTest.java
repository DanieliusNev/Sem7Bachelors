package com.example.bachelorsrealwear;

import com.example.bachelorsrealwear.data.storage.ChecklistFormState;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

public class FormStateUnitTest {

    private ChecklistFormState formState;

    @Before
    public void setup() {
        formState = ChecklistFormState.getInstance();
        formState.clear(); // if you have such method, or manually clear map
    }

    @Test
    public void testSavingAndRetrievingAnswers() {
        formState.setAnswer("Owner", "John Doe");
        formState.setAnswer("WTG", "WTG 1");

        Map<String, Object> savedAnswers = formState.getAllAnswers();

        assertEquals("John Doe", savedAnswers.get("Owner"));
        assertEquals("WTG 1", savedAnswers.get("WTG"));
    }

    @Test
    public void testOverwritingAnswer() {
        formState.setAnswer("Project", "Initial");
        formState.setAnswer("Project", "Updated");

        assertEquals("Updated", formState.getAllAnswers().get("Project"));
    }

    @Test
    public void testEmptyFormInitially() {
        formState = ChecklistFormState.getInstance();
        assertTrue(formState.getAllAnswers().isEmpty());
    }
}
