package com.example.bachelorsrealwear.presentation.ui.viewModel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.bachelorsrealwear.data.storage.ToolDataStore;
import com.example.bachelorsrealwear.domain.model.ToolEntry;
import com.example.bachelorsrealwear.domain.repository.ToolRepository;

import java.util.List;

public class ToolViewModel extends AndroidViewModel {

    private final MutableLiveData<List<ToolEntry>> toolListLiveData = new MutableLiveData<>();
    private final ToolRepository toolRepository;
    public ToolViewModel(@NonNull Application application) {
        super(application);
        this.toolRepository = new ToolDataStore(); // Inject via interface
        loadTools();
    }

    public LiveData<List<ToolEntry>> getToolList() {
        return toolListLiveData;
    }

    public void loadTools() {
        List<ToolEntry> tools = toolRepository.loadTools(getApplication());
        toolListLiveData.setValue(tools);
    }

    public void saveTool(String description, String toolNumber, String expiryDate) {
        ToolEntry tool = new ToolEntry(description, toolNumber, expiryDate);
        toolRepository.addTool(getApplication(), tool);
        loadTools(); // refresh
    }
}

