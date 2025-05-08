package com.example.bachelorsrealwear.presentation.ui.viewModel;

import androidx.lifecycle.ViewModel;

import com.example.bachelorsrealwear.data.storage.ChecklistFormState;

public class TechnicianInfoViewModel extends ViewModel {

    private final ChecklistFormState formState = ChecklistFormState.getInstance();

    // These are the PDF field IDs
    private static final String FIELD_TECH_NAME = "TECH_Technicians_Name_1";
    private static final String FIELD_EMPLOYEE_NO = "TECH_Employee_No_1";
    private static final String FIELD_COMPANY_NAME = "TECH_Company_name_1";
    private static final String FIELD_INITIALS = "TECH_Initials_1";

    public void saveTechnicianInfo(String name, String employeeNo, String companyName, String initials) {
        formState.setAnswer(FIELD_TECH_NAME, name);
        formState.setAnswer(FIELD_EMPLOYEE_NO, employeeNo);
        formState.setAnswer(FIELD_COMPANY_NAME, companyName);
        formState.setAnswer(FIELD_INITIALS, initials);
    }

    public String getTechnicianName() {
        Object value = formState.getAnswer(FIELD_TECH_NAME);
        return value != null ? value.toString() : "";
    }

    public String getEmployeeNo() {
        Object value = formState.getAnswer(FIELD_EMPLOYEE_NO);
        return value != null ? value.toString() : "";
    }

    public String getCompanyName() {
        Object value = formState.getAnswer(FIELD_COMPANY_NAME);
        return value != null ? value.toString() : "";
    }

    public String getInitials() {
        Object value = formState.getAnswer(FIELD_INITIALS);
        return value != null ? value.toString() : "";
    }
}
