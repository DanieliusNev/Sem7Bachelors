package com.example.bachelorsrealwear.data.repository;

import com.example.bachelorsrealwear.domain.model.ToolEntry;
import com.example.bachelorsrealwear.domain.repository.ToolRepository;
import java.util.ArrayList;
import java.util.List;

/**
 * In-memory implementation of ToolRepository.
 * This stores the list of tools temporarily.
 */
public class ToolRepositoryImpl implements ToolRepository {
    private final List<ToolEntry> tools = new ArrayList<>();

    @Override
    public void addTool(ToolEntry tool) {
        tools.add(tool);
    }

    @Override
    public List<ToolEntry> getAllTools() {
        return new ArrayList<>(tools);
    }
}