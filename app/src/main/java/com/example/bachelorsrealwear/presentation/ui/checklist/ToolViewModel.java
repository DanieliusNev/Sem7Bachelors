package com.example.bachelorsrealwear.presentation.ui.checklist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bachelorsrealwear.domain.model.ToolEntry;
import com.example.bachelorsrealwear.domain.repository.ToolRepository;

import java.util.List;

/**
 * ViewModel for the calibrated tool entry feature.
 * Exposes LiveData for observing changes in the tool list.
 */
public class ToolViewModel extends ViewModel {
    private final ToolRepository toolRepository;
    private final MutableLiveData<List<ToolEntry>> toolsLiveData = new MutableLiveData<>();

    public ToolViewModel(ToolRepository toolRepository) {
        this.toolRepository = toolRepository;
        toolsLiveData.setValue(toolRepository.getAllTools());
    }

    public LiveData<List<ToolEntry>> getTools() {
        return toolsLiveData;
    }

    public void addTool(String description, String toolNo, String expiryDate) {
        ToolEntry newTool = new ToolEntry(description, toolNo, expiryDate);
        toolRepository.addTool(newTool);
        toolsLiveData.setValue(toolRepository.getAllTools());
    }
}