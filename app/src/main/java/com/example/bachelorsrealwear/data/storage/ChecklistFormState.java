package com.example.bachelorsrealwear.data.storage;

import android.net.Uri;

import java.util.*;

public class ChecklistFormState {
    private static final ChecklistFormState instance = new ChecklistFormState();

    // Generic answer storage: fieldId → value
    private final Map<String, Object> formAnswers = new HashMap<>();

    // Photo URIs (only used for Page 7)
    private final List<Uri> photoUris = new ArrayList<>();

    private ChecklistFormState() {}

    public static ChecklistFormState getInstance() {
        return instance;
    }

    public void setAnswer(String fieldId, Object value) {
        formAnswers.put(fieldId, value);
    }

    public Object getAnswer(String fieldId) {
        return formAnswers.get(fieldId);
    }

    public Map<String, Object> getAllAnswers() {
        return new HashMap<>(formAnswers);
    }

    public void clear() {
        formAnswers.clear();
        photoUris.clear();
    }

    public void addPhoto(Uri uri) {
        if (photoUris.size() < 4) {
            photoUris.add(uri);
        }
    }

    public void removePhoto(Uri uri) {
        photoUris.remove(uri);
    }

    public List<Uri> getPhotos() {
        return new ArrayList<>(photoUris);
    }
}
