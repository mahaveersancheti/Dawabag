package com.dawabag.helpers;

import android.content.Context;
import android.content.SharedPreferences;

public class PrefManager {
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    Context _context;
    // shared pref mode
    int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "dawabag";
    private static final String IS_FIRST_TIME_LAUNCH = "IsFirstTimeLaunch";
    private static final String isLogin = "isLogin";
    private static final String token = "token";
    private static final String userId = "userId";
    private static final String userType = "userType";  //pharmacist, doctor, patient
    private static final String name = "name";
    private static final String phone = "phone";
    private static final String selectedPrescriptions = "selectedPrescriptions";
    private static final String city = "city";
    private static final String pincode = "pincode";

    private static final String imei = "imei";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setFirstTimeLaunch(boolean isFirstTime) {
        editor.putBoolean(IS_FIRST_TIME_LAUNCH, isFirstTime);
        editor.commit();
    }

    public boolean isFirstTimeLaunch() {
        return pref.getBoolean(IS_FIRST_TIME_LAUNCH, true);
    }

    public void clearPreference() {
        editor.clear();
        editor.commit();
    }

    public boolean checkSharedPrefs(String key) {
        if (pref.contains(key)) {
            return true;
        }
        return false;
    }

    public void setUserId(String value) {
        editor.putString(userId, value);
        editor.commit();
    }
    public String getUserId() {
        return pref.getString(userId, "");
    }

    public String getUserType() {
        return pref.getString(userType, "");
    }

    public void setUserType(String value) {
        editor.putString(userType, value);
        editor.commit();
    }

    public String getName() {
        return pref.getString(name, "");
    }
    public void setName(String value) {
        editor.putString(name, value);
        editor.commit();
    }

    public void setIsLogin(boolean value) {
        editor.putBoolean(isLogin, value);
        editor.commit();
    }
    public boolean getIsLogin() {
        return pref.getBoolean(isLogin, false);
    }

    public void setToken(String value) {
        editor.putString(token, value);
        editor.commit();
    }
    public String getToken() {
        return pref.getString(token, "");
    }

    public void setPhone(String value) {
        editor.putString(phone, value);
        editor.commit();
    }
    public String getPhone() {
        return pref.getString(phone, "");
    }

    public void setSelectedPrescriptions(String value) {
        editor.putString(selectedPrescriptions, value);
        editor.commit();
    }
    public String getSelectedPrescriptions() {
        return pref.getString(selectedPrescriptions, "");
    }

    public void setCity(String value) {
        editor.putString(city, value);
        editor.commit();
    }
    public String getCity() {
        return pref.getString(city, "");
    }

    public void setPincode(String value) {
        editor.putString(pincode, value);
        editor.commit();
    }
    public String getPincode() {
        return pref.getString(pincode, "");
    }

    public void setImei(String value) {
        editor.putString(imei, value);
        editor.commit();
    }
    public String getImei() {
        return pref.getString(imei, "");
    }

}
