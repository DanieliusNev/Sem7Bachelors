package com.example.bachelorsrealwear.domain.usecase;

import com.example.bachelorsrealwear.domain.model.ChecklistTemplate;
import com.example.bachelorsrealwear.domain.repository.ChecklistRepository;

public class LoadChecklistTemplateUseCase {
    private final ChecklistRepository repository;

    public LoadChecklistTemplateUseCase(ChecklistRepository repository) {
        this.repository = repository;
    }

    public ChecklistTemplate execute(String filename, int index) {
        return repository.loadTemplate(filename, index);
    }
}
