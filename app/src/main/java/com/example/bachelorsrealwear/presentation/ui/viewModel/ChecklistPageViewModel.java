package com.example.bachelorsrealwear.presentation.ui.viewModel;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.bachelorsrealwear.data.repository.ChecklistRepositoryImpl;
import com.example.bachelorsrealwear.data.storage.ChecklistFormState;
import com.example.bachelorsrealwear.data.storage.ToolDataStore;
import com.example.bachelorsrealwear.domain.model.ChecklistField;
import com.example.bachelorsrealwear.domain.model.ChecklistPage;
import com.example.bachelorsrealwear.domain.model.ChecklistTemplate;
import com.example.bachelorsrealwear.domain.model.ToolEntry;
import com.example.bachelorsrealwear.domain.usecase.LoadChecklistTemplateUseCase;
import com.example.bachelorsrealwear.presentation.adapter.ToolListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ChecklistPageViewModel extends ViewModel {

    private final MutableLiveData<List<String>> pageFieldIds = new MutableLiveData<>();
    private final MutableLiveData<List<String>> pageFieldLabels = new MutableLiveData<>();
    private final MutableLiveData<List<String>> pageFieldTypes = new MutableLiveData<>();
    private final MutableLiveData<List<List<String>>> pageFieldOptions = new MutableLiveData<>();
    private final MutableLiveData<List<String>> pageFieldPlaceholders = new MutableLiveData<>();
    private final MutableLiveData<String> pageTitleLiveData = new MutableLiveData<>();
    private final MutableLiveData<List<String>> toolDisplayList = new MutableLiveData<>();
    private final MutableLiveData<ArrayAdapter<ToolEntry>> toolAdapterLiveData = new MutableLiveData<>();

    private final ChecklistRepositoryImpl repository;
    private final LoadChecklistTemplateUseCase useCase;
    private final ChecklistFormState formState = ChecklistFormState.getInstance();

    private final Context context;
    private ChecklistTemplate template;

    public ChecklistPageViewModel(Context context) {
        this.context = context.getApplicationContext();
        this.repository = new ChecklistRepositoryImpl(this.context);
        this.useCase = new LoadChecklistTemplateUseCase(repository);
    }

    public void loadTemplate(String filename, int templateIndex, int pageIndex) {
        template = useCase.execute(filename, templateIndex);
        if (template != null && pageIndex < template.pages.size()) {
            ChecklistPage page = template.pages.get(pageIndex);
            pageTitleLiveData.setValue(page.title);

            List<String> ids = new ArrayList<>();
            List<String> labels = new ArrayList<>();
            List<String> types = new ArrayList<>();
            List<List<String>> options = new ArrayList<>();
            List<String> placeholders = new ArrayList<>();

            for (ChecklistField field : page.fields) {
                ids.add(field.id);
                labels.add(field.label);
                types.add(field.type);
                options.add(field.options);
                placeholders.add(field.placeholder);
            }

            pageFieldIds.setValue(ids);
            pageFieldLabels.setValue(labels);
            pageFieldTypes.setValue(types);
            pageFieldOptions.setValue(options);
            pageFieldPlaceholders.setValue(placeholders);
        }
    }


    public LiveData<ArrayAdapter<ToolEntry>> getToolAdapter() {
        return toolAdapterLiveData;
    }

    public void loadTools(Context context) {
        List<ToolEntry> tools = ToolDataStore.loadTools(context);
        ToolListAdapter adapter = new ToolListAdapter(context, tools);
        toolAdapterLiveData.setValue(adapter);
    }

    public LiveData<List<String>> getToolDisplayList() {
        return toolDisplayList;
    }

    public LiveData<String> getPageTitle() {
        return pageTitleLiveData;
    }

    public LiveData<List<String>> getFieldIds() {
        return pageFieldIds;
    }

    public LiveData<List<String>> getFieldLabels() {
        return pageFieldLabels;
    }

    public LiveData<List<String>> getFieldTypes() {
        return pageFieldTypes;
    }

    public LiveData<List<List<String>>> getFieldOptions() {
        return pageFieldOptions;
    }

    public LiveData<List<String>> getFieldPlaceholders() {
        return pageFieldPlaceholders;
    }

    public Object getAnswer(String fieldId) {
        return formState.getAnswer(fieldId);
    }

    public void saveAnswers(Map<String, Object> answers) {
        for (Map.Entry<String, Object> entry : answers.entrySet()) {
            formState.setAnswer(entry.getKey(), entry.getValue());
        }
    }

    public ChecklistTemplate getTemplate() {
        return template;
    }
}
