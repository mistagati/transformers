package com.surpassplus.transformers.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import java.util.Map;

public class PreferencesHandler {

    private static final String SHARED_PREFERENCES_KEY = "com.halifax.branchlocator";
    private Context mContext;
    static PreferencesHandler mThis;

    private PreferencesHandler(Context c) {

        mContext = c.getApplicationContext();
    }

    public static PreferencesHandler getInstance(Context c) {

        if (mThis == null) {
            mThis = new PreferencesHandler(c);
        }
        return mThis;
    }

    private SharedPreferences getAppSharedPreferencesObject() {

        return mContext.getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE);
    }

    public Object getValue(String key) {

        if (key == null) {
            return null;
        }
        Map<String, ?> allPreferences;
        SharedPreferences preferences = getAppSharedPreferencesObject();
        allPreferences = preferences.getAll();
        for (Map.Entry<String, ?> entry : allPreferences.entrySet()) {
            if (entry.getKey().equals(key))
                return entry.getValue();
        }
        return null;
    }


    public boolean saveSharedPreferences(String key, String object) {

        if ((key == null) || (object == null)) {
            return false;
        }
        SharedPreferences preferences = getAppSharedPreferencesObject();
        Editor sharedPreferencesEditor = preferences.edit();
        sharedPreferencesEditor.putString(key, object);
        return sharedPreferencesEditor.commit();
    }

    public Boolean isFilteringByCity() {
        if (getFilterOption().toLowerCase().equals("city"))
            return true;
        else
            return false;
    }

    public void setFilterOptionToCity() {
        saveFilterOption("City");
    }

    public void setFilterOptionToAllLocations() {
        saveFilterOption("All Locations");
    }

    private void saveFilterOption(String filterOption) {
        saveSharedPreferences("filterOption", filterOption);
    }

    public String getFilterOption() {
        Object prefObj = getValue("filterOption");
        if (prefObj != null) {
            return (String) prefObj;
        }
        return "All Locations";
    }


    public void setFilterLocation(String filterLocation) {
        saveSharedPreferences("filterLocation", filterLocation);
    }

    public String getFilterLocation() {
        Object prefObj = getValue("filterLocation");
        if (prefObj != null) {
            return (String) prefObj;
        }
        return "All Locations";
    }

}
