package com.example.bachelorsrealwear.domain.model;

import java.util.List;

public class ChecklistField {
    public String type; // "text", "dropdown"
    public String label;
    public String placeholder; // only for text
    public List<String> options; // only for dropdown
}
