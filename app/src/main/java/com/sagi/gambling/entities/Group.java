package com.sagi.gambling.entities;

import com.sagi.gambling.utilities.Utils;

import java.io.Serializable;

/**
 * Created by User on 06/02/2019.
 */

public class Group implements Serializable {

    private String groupName;
    private int entryAmount;
    private String groupKey;
    private String status;
    private String userManagerKey;
    private long timestamp;
    private long countUsers;
    private String myGroupGamble;

    private String keyGame;
    private String groupName1;
    private String groupName2;



    public String getKeyGame() {
        return keyGame;
    }

    public void setKeyGame(String keyGame) {
        this.keyGame = keyGame;
    }

    public String getGroupName1() {
        return groupName1;
    }

    public void setGroupName1(String groupName1) {
        this.groupName1 = groupName1;
    }

    public String getGroupName2() {
        return groupName2;
    }

    public void setGroupName2(String groupName2) {
        this.groupName2 = groupName2;
    }

    public String loadMyGroupGamble() {
        return myGroupGamble;
    }

    public void setMyGroupGamble(String myGroupGamble) {
        this.myGroupGamble = myGroupGamble;
    }

    public long getCountUsers() {
        return countUsers;
    }

    public void setCountUsers(long countUsers) {
        this.countUsers = countUsers;
    }

    public Group() {
        countUsers = 0;
    }

    public String getUserManagerKey() {
        return userManagerKey;
    }

    public void setUserManagerKey(String userManagerKey) {
        this.userManagerKey = userManagerKey;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getTimeAndDate() {
        return Utils.getDateFromTimeStamp(timestamp);
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public int getEntryAmount() {
        return entryAmount;
    }

    public void setEntryAmount(int entryAmount) {
        this.entryAmount = entryAmount;
    }

    public String getGroupKey() {
        return groupKey;
    }

    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
