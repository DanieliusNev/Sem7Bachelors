package com.example.bachelorsrealwear.domain.repository;

import com.example.bachelorsrealwear.domain.model.ToolEntry;
import java.util.List;

/**
 * Interface defining operations for managing tool entries.
 */
public interface ToolRepository {
    void addTool(ToolEntry tool);
    List<ToolEntry> getAllTools();
}