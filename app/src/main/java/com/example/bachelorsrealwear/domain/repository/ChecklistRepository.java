package com.example.bachelorsrealwear.domain.repository;

import com.example.bachelorsrealwear.domain.model.ChecklistTemplate;

public interface ChecklistRepository {
    ChecklistTemplate loadTemplate(String filename, int index);
}
