package com.example.bachelorsrealwear.data.storage;

import android.net.Uri;
import java.util.HashMap;
import java.util.Map;

public class ChecklistFormState {
    private static final ChecklistFormState instance = new ChecklistFormState();

    // Stores text/checkbox/chip/dropdown answers
    private final Map<String, Object> formAnswers = new HashMap<>();

    // Stores photo answers by fieldId (e.g., "Picture 1", "Picture 2", ...)
    private final Map<String, Uri> photoAnswers = new HashMap<>();

    private ChecklistFormState() {}

    public static ChecklistFormState getInstance() {
        return instance;
    }

    // === Generic Answers ===
    public void setAnswer(String fieldId, Object value) {
        formAnswers.put(fieldId, value);
    }

    public Object getAnswer(String fieldId) {
        return formAnswers.get(fieldId);
    }

    public Map<String, Object> getAllAnswers() {
        return new HashMap<>(formAnswers);
    }

    // === Photo Answers ===
    public void setPhoto(String fieldId, Uri uri) {
        photoAnswers.put(fieldId, uri);
    }

    public Uri getPhoto(String fieldId) {
        return photoAnswers.get(fieldId);
    }

    public Map<String, Uri> getAllPhotos() {
        return new HashMap<>(photoAnswers);
    }

    public void removePhoto(String fieldId) {
        photoAnswers.remove(fieldId);
    }

    // === Reset All ===
    public void clear() {
        formAnswers.clear();
        photoAnswers.clear();
    }
}
