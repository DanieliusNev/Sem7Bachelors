package com.example.bachelorsrealwear.domain.model;

/**
 * Represents a single row in the Calibrated Tools table.
 * Holds information about the tool description, ID, and expiry date.
 */
public class ToolEntry {
    public String description;
    public String toolNumber;
    public String expiryDate;

    public ToolEntry(String description, String toolNumber, String expiryDate) {
        this.description = description;
        this.toolNumber = toolNumber;
        this.expiryDate = expiryDate;
    }
}

