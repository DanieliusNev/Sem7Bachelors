package com.example.bachelorsrealwear.presentation.ui.viewModel;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class ChecklistPageViewModelFactory implements ViewModelProvider.Factory {

    private final Context context;

    public ChecklistPageViewModelFactory(Context context) {
        this.context = context.getApplicationContext();
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ChecklistPageViewModel.class)) {
            return (T) new ChecklistPageViewModel(context);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}
