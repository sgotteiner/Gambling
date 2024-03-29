package com.sagi.gambling.utilities;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import com.sagi.gambling.entities.User;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by User on 14/09/2018.
 */

public class SharedPreferencesHelper {

    private static SharedPreferences preferences;
    private static SharedPreferencesHelper mInstance;

    private final String SETTINGS_APP = "SETTINGS_APP";
    private final String IS_ALREADY_LOGIN = "IS_ALREADY_LOGIN";
    private final String FIRST_NAME = "FIRST_NAME";
    private final String LAST_NAME = "LAST_NAME";
    private final String EMAIL = "EMAIL";
    private final String URL_PROFILE = "URL_PROFILE";
    private final String BIRTHDAY = "BIRTHDAY";
    private final String LAST_TIME_SEEN = "LAST_TIME_SEEN";
    private final String TOTAL_MONEY="TOTAL_MONEY";
    private final String LAST_COUNT_REQUEST="LAST_COUNT_REQUEST";
//    private final String LEVEL="LEVEL";
    private final String IS_MANAGER_APP="IS_MANAGER_APP";

    private SharedPreferencesHelper(Context context) {
        preferences = context.getSharedPreferences(SETTINGS_APP, MODE_PRIVATE);
    }

    public static SharedPreferencesHelper getInstance(Context context) {

        if (mInstance == null)
            mInstance = new SharedPreferencesHelper(context);

        return mInstance;
    }

    public boolean isAlreadyLogin() {
        return preferences.getBoolean(IS_ALREADY_LOGIN, false);
    }

    public int getLastCountRequest() {
        return preferences.getInt(LAST_COUNT_REQUEST, 0);
    }

    public void setIsAlreadyLogin(boolean isAlreadyLogin) {
        preferences.edit().putBoolean(IS_ALREADY_LOGIN, isAlreadyLogin).commit();
    }

    public User getUser() {
        String firstName = preferences.getString(FIRST_NAME, null);
        String lastName = preferences.getString(LAST_NAME, null);
        String email = preferences.getString(EMAIL, null);
        String urlProfile = preferences.getString(URL_PROFILE, null);
        long birthday = preferences.getLong(BIRTHDAY, -1);

        long lastTimeSeen = preferences.getLong(LAST_TIME_SEEN, -1);
//        float level=preferences.getFloat(LEVEL,0);
        boolean isManagerApp=preferences.getBoolean(IS_MANAGER_APP,false);
        int totalMoney=preferences.getInt(TOTAL_MONEY,0);

        if (email == null || birthday == -1)
            return null;

        return new User(firstName, lastName, email,birthday,lastTimeSeen,totalMoney,isManagerApp);
    }

    public void setUser(User user) {
        preferences.edit().putString(FIRST_NAME, user.getFirstName()).commit();
        preferences.edit().putString(LAST_NAME, user.getLastName()).commit();
        preferences.edit().putString(EMAIL, user.getEmail()).commit();
        preferences.edit().putString(URL_PROFILE, user.getEmail()).commit();
        preferences.edit().putLong(BIRTHDAY, user.getBirthDay()).commit();
        preferences.edit().putLong(LAST_TIME_SEEN, user.getLastTimeSeen()).commit();
//        preferences.edit().putFloat(LEVEL,user.getLevelUser()).commit();
        preferences.edit().putBoolean(IS_MANAGER_APP,user.isManagerApp()).commit();
        setTotalMoney(user.getTotalMoney());
    }

    public void setTotalMoney(int totalMoney) {
        preferences.edit().putInt(TOTAL_MONEY,totalMoney).commit();
    }
    public void setLastCountRequest(int total) {
        preferences.edit().putInt(LAST_COUNT_REQUEST,total).commit();
    }


    public void resetSharedPreferences() {
            preferences.edit().clear().commit();
    }
}
