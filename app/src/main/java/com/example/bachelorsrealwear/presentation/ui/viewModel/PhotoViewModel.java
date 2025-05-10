package com.example.bachelorsrealwear.presentation.ui.viewModel;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bachelorsrealwear.data.storage.ChecklistFormState;

import java.util.ArrayList;
import java.util.List;

public class PhotoViewModel extends ViewModel {

    private final MutableLiveData<List<Uri>> photoUris = new MutableLiveData<>(new ArrayList<>());
    private final ChecklistFormState formState = ChecklistFormState.getInstance();

    public LiveData<List<Uri>> getPhotoUris() {
        return photoUris;
    }

    public void addPhoto(Uri uri) {
        List<Uri> current = new ArrayList<>(photoUris.getValue());
        if (current.size() < 6) {
            current.add(uri);
            photoUris.setValue(current);
            updateFormState(current);
        }
    }

    public void removePhoto(Uri uri) {
        List<Uri> current = new ArrayList<>(photoUris.getValue());
        int index = current.indexOf(uri);
        if (index != -1) {
            current.remove(index);
            photoUris.setValue(current);
            updateFormState(current);
        }
    }

    private void updateFormState(List<Uri> uris) {
        formState.getAllPhotos().clear(); // Clear previous
        for (int i = 0; i < uris.size(); i++) {
            String fieldId = "Picture " + (i + 1);
            formState.setPhoto(fieldId, uris.get(i));
        }
    }

    public boolean isMaxReached() {
        List<Uri> current = photoUris.getValue();
        return current != null && current.size() >= 6;
    }

    public void clearPhotos() {
        photoUris.setValue(new ArrayList<>());
        formState.getAllPhotos().clear();
    }
}
