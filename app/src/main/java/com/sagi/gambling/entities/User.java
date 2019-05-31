package com.sagi.gambling.entities;

import android.graphics.Color;

import com.google.firebase.storage.internal.Util;
import com.sagi.gambling.utilities.Utils;

import java.io.Serializable;
import java.util.Random;

/**
 * Created by User on 21/09/2018.
 */

public class User implements Serializable {

    private String firstName;
    private String lastName;
    private String email;
    private long birthDay;
    private long lastTimeSeen;
    private int totalMoney;
    private boolean isManagerApp = false;



    public boolean isManagerApp() {
        return isManagerApp;
    }

    public void setManagerApp(boolean managerApp) {
        isManagerApp = managerApp;
    }

    public int getTotalMoney() {
        return totalMoney;
    }


    public void setTotalMoney(int totalMoney) {
        this.totalMoney = totalMoney;
    }

    public void addMoney(int money) {
        this.totalMoney += money;
    }

    public void removeMoney(int money) {
        this.totalMoney -= money;
    }


    public  String textEmailForFirebase() {
       return  Utils.textEmailForFirebase(email);
    }

    public User(String firstName, String lastName, String email, long birthDay, long lastTimeSeen, int totalMoney, boolean isManagerApp) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthDay = birthDay;
        this.lastTimeSeen = lastTimeSeen;
        this.totalMoney = totalMoney;
        this.isManagerApp = isManagerApp;
    }


    public User(String firstName, String lastName, String email, long birthDay, long lastTimeSeen) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.birthDay = birthDay;
        this.lastTimeSeen = lastTimeSeen;
    }

    public User() {
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public long getBirthDay() {
        return birthDay;
    }

    public void setBirthDay(long birthDay) {
        this.birthDay = birthDay;
    }

    public long getLastTimeSeen() {
        return lastTimeSeen;
    }
}
