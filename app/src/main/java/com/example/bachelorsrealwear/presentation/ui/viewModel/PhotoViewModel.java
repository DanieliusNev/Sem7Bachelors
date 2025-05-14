package com.example.bachelorsrealwear.presentation.ui.viewModel;

import android.net.Uri;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

public class PhotoViewModel extends ViewModel {

    private final MutableLiveData<List<Uri>> photoUris = new MutableLiveData<>(new ArrayList<>());

    public LiveData<List<Uri>> getPhotoUris() {
        return photoUris;
    }

    public void addPhoto(Uri uri) {
        List<Uri> current = new ArrayList<>(photoUris.getValue());
        if (current.size() < 4) {
            current.add(uri);
            photoUris.setValue(current);
        }
    }

    public void removePhoto(Uri uri) {
        List<Uri> current = new ArrayList<>(photoUris.getValue());
        current.remove(uri);
        photoUris.setValue(current);
    }

    public boolean isMaxReached() {
        return photoUris.getValue() != null && photoUris.getValue().size() >= 4;
    }
}
