package com.example.bachelorsrealwear.presentation.ui.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bachelorsrealwear.data.storage.ChecklistFormState;
import com.example.bachelorsrealwear.domain.model.ChecklistField;
import com.example.bachelorsrealwear.domain.model.ChecklistTemplate;
import com.example.bachelorsrealwear.domain.usecase.LoadChecklistTemplateUseCase;

import java.util.List;

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

    public void saveAnswer(String fieldId, Object value) {
        ChecklistFormState.getInstance().setAnswer(fieldId, value);
    }

    public Object getSavedAnswer(String fieldId) {
        return ChecklistFormState.getInstance().getAnswer(fieldId);
    }

    public void saveAllAnswers(List<ChecklistField> fields, java.util.Map<String, android.view.View> inputViews) {
        for (ChecklistField field : fields) {
            android.view.View view = inputViews.get(field.id);
            if (view == null) continue;

            if (view instanceof android.widget.EditText) {
                String value = ((android.widget.EditText) view).getText().toString();
                saveAnswer(field.id, value);
            } else if (view instanceof android.widget.Spinner) {
                String value = ((android.widget.Spinner) view).getSelectedItem().toString();
                saveAnswer(field.id, value);
            } else if (view instanceof android.widget.CheckBox) {
                boolean value = ((android.widget.CheckBox) view).isChecked();
                saveAnswer(field.id, value);
            } else if (view instanceof com.google.android.material.chip.ChipGroup) {
                com.google.android.material.chip.ChipGroup chipGroup = (com.google.android.material.chip.ChipGroup) view;
                int selectedId = chipGroup.getCheckedChipId();
                if (selectedId != -1) {
                    com.google.android.material.chip.Chip chip = chipGroup.findViewById(selectedId);
                    if (chip != null) {
                        saveAnswer(field.id, chip.getText().toString());
                    }
                }
            }
        }
    }
}
