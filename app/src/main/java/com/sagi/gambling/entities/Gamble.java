package com.sagi.gambling.entities;

import com.sagi.gambling.utilities.Utils;

/**
 * Created by User on 19/10/2018.
 */

public class Gamble {

    private String key, keyGame, userEmail, groupNameSelected;
    private int gambleMoney;
    private boolean isPaid=false;
    private long timeStamp;


    public boolean isPaid() {
        return isPaid;
    }

    public void setPaid(boolean paid) {
        isPaid = paid;
    }

    public Gamble() {
    }

    public long getTimeStamp() {
        return timeStamp;
    }
    public String getTimeAndDate() {
        return Utils.getDateAndTimeFromTimeStamp(timeStamp);
    }
    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public Gamble(String key, String keyGame, String userEmail, String groupNameSelected, int gambleMoney) {
        this.key = key;
        this.keyGame = keyGame;
        this.userEmail = userEmail;
        this.groupNameSelected = groupNameSelected;
        this.gambleMoney = gambleMoney;
    }

    public Gamble(String keyGame, String userEmail, String groupNameSelected, int gambleMoney, long timeStamp) {
        this.keyGame = keyGame;
        this.userEmail = userEmail;
        this.timeStamp=timeStamp;
        this.groupNameSelected = groupNameSelected;
        this.gambleMoney = gambleMoney;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKeyGame() {
        return keyGame;
    }

    public void setKeyGame(String keyGame) {
        this.keyGame = keyGame;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getGroupNameSelected() {
        return groupNameSelected;
    }

    public void setGroupNameSelected(String groupNameSelected) {
        this.groupNameSelected = groupNameSelected;
    }

    public int getGambleMoney() {
        return gambleMoney;
    }

    public void setGambleMoney(int gambleMoney) {
        this.gambleMoney = gambleMoney;
    }
}
