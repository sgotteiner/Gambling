package com.sagi.gambling.entities;

public class GroupMember {
    private String emailUser;
    private String gambleSelected;


    public GroupMember(String emailUser, String gambleSelected) {
        this.emailUser = emailUser;
        this.gambleSelected = gambleSelected;
    }


    public GroupMember() {
    }

    public String getEmailUser() {
        return emailUser;
    }

    public void setEmailUser(String emailUser) {
        this.emailUser = emailUser;
    }

    public String getGambleSelected() {
        return gambleSelected;
    }

    public void setGambleSelected(String gambleSelected) {
        this.gambleSelected = gambleSelected;
    }
}
