package com.example.bachelorsrealwear.data.storage;


import com.example.bachelorsrealwear.data.repository.ToolRepositoryImpl;
import com.example.bachelorsrealwear.domain.repository.ToolRepository;

/**
 * Singleton holder to provide shared access to ToolRepository across activities.
 */
public class ToolDataHolder {
    private static ToolDataHolder instance;
    private final ToolRepository repository;

    private ToolDataHolder() {
        repository = new ToolRepositoryImpl();
    }

    public static synchronized ToolDataHolder getInstance() {
        if (instance == null) {
            instance = new ToolDataHolder();
        }
        return instance;
    }

    public ToolRepository getRepository() {
        return repository;
    }
}
