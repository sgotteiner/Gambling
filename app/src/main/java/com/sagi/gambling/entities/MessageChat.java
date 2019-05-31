package com.sagi.gambling.entities;

import android.graphics.Color;

import java.security.Timestamp;

/**
 * Created by User on 14/12/2018.
 */

public class MessageChat {
    private String messageBody;
    private long timeStamp;
    private String fullNameOfUser;
    private String userKey;
    private int color;

    public MessageChat(String messageBody, long timeStamp, String fullNameOfUser, String userKey ) {
        this.messageBody = messageBody;
        this.timeStamp = timeStamp;
        this.fullNameOfUser = fullNameOfUser;
        this.userKey = userKey;
     }

    public String getUserKey() {
        return userKey;
    }

    public void setUserKey(String userKey) {
        this.userKey = userKey;
    }

    public MessageChat() {
    }
    public void setColorUser(int coloe) {
        this.color = coloe;
    }

    public int getColorUser() {
        return this.color;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getFullNameOfUser() {
        return fullNameOfUser;
    }

    public void setFullNameOfUser(String fullNameOfUser) {
        this.fullNameOfUser = fullNameOfUser;
    }
}
