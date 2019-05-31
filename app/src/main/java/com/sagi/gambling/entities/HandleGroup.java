package com.sagi.gambling.entities;

public class HandleGroup {
    private String keyGroup;
    private String gambleSelected;
    private boolean isPaid;

    public HandleGroup(String keyGroup, String gambleSelected, boolean isPaid) {
        this.keyGroup = keyGroup;
        this.gambleSelected = gambleSelected;
        this.isPaid = isPaid;
    }

    public HandleGroup() {
    }

    public String getKeyGroup() {
        return keyGroup;
    }

    public String getGambleSelected() {
        return gambleSelected;
    }
}
