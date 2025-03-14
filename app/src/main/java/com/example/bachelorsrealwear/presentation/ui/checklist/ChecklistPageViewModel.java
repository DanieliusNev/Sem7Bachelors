package com.example.bachelorsrealwear.presentation.ui.checklist;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.bachelorsrealwear.domain.model.ChecklistTemplate;
import com.example.bachelorsrealwear.domain.usecase.LoadChecklistTemplateUseCase;

public class ChecklistPageViewModel extends ViewModel {
    private final MutableLiveData<ChecklistTemplate> templateLiveData = new MutableLiveData<>();
    private final LoadChecklistTemplateUseCase loadTemplateUseCase;

    public ChecklistPageViewModel(LoadChecklistTemplateUseCase useCase) {
        this.loadTemplateUseCase = useCase;
    }

    public void loadTemplate(String filename, int index) {
        ChecklistTemplate template = loadTemplateUseCase.execute(filename, index);
        templateLiveData.setValue(template);
    }

    public LiveData<ChecklistTemplate> getTemplate() {
        return templateLiveData;
    }
}
