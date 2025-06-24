package com.example.bachelorsrealwear.presentation.ui.viewModel;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.example.bachelorsrealwear.data.storage.ChecklistFormState;

public class TechnicianInfoViewModel extends AndroidViewModel {

    private static final String PREF_NAME = "TechnicianInfoPrefs";
    private static final String KEY_NAME = "technician_name";
    private static final String KEY_EMP_NO = "employee_no";
    private static final String KEY_COMPANY = "company_name";
    private static final String KEY_INITIALS = "initials";

    private final ChecklistFormState formState = ChecklistFormState.getInstance();
    private final SharedPreferences prefs;

    public TechnicianInfoViewModel(@NonNull Application application) {
        super(application);
        prefs = application.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        loadFromPrefs(); // Load once when ViewModel is created
    }

    public void saveTechnicianInfo(String name, String employeeNo, String companyName, String initials) {
        formState.setAnswer("TECH_Technicians_Name_1", name);
        formState.setAnswer("TECH_Employee_No_1", employeeNo);
        formState.setAnswer("TECH_Company_name_1", companyName);
        formState.setAnswer("TECH_Initials_1", initials);

        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMP_NO, employeeNo);
        editor.putString(KEY_COMPANY, companyName);
        editor.putString(KEY_INITIALS, initials);
        editor.apply();
    }

    private void loadFromPrefs() {
        formState.setAnswer("TECH_Technicians_Name_1", prefs.getString(KEY_NAME, ""));
        formState.setAnswer("TECH_Employee_No_1", prefs.getString(KEY_EMP_NO, ""));
        formState.setAnswer("TECH_Company_name_1", prefs.getString(KEY_COMPANY, ""));
        formState.setAnswer("TECH_Initials_1", prefs.getString(KEY_INITIALS, ""));
    }

    public String getTechnicianName() {
        return prefs.getString(KEY_NAME, "");
    }

    public String getEmployeeNo() {
        return prefs.getString(KEY_EMP_NO, "");
    }

    public String getCompanyName() {
        return prefs.getString(KEY_COMPANY, "");
    }

    public String getInitials() {
        return prefs.getString(KEY_INITIALS, "");
    }
}
