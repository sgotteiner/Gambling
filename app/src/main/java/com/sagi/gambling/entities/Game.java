package com.sagi.gambling.entities;

import com.sagi.gambling.utilities.Utils;

import java.io.Serializable;

/**
 * Created by User on 19/10/2018.
 */

public class Game implements Serializable {

    private String key;
    private long timeStampStartGame;
    private int chanceWinGroup1;
    private String group1name, group2name;
    private String description, categoryName;
    private String status;
    private int winner;

    public int getWinner() {
        return winner;
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }

    public Game(String key, long timeStampStartGame, int chanceWinGroup1, String group1name, String group2name) {
        this.key = key;
        this.timeStampStartGame = timeStampStartGame;
        this.chanceWinGroup1 = chanceWinGroup1;
        this.group1name = group1name;
        this.group2name = group2name;
        this.winner=0;
    }


    public Game(long timeStampStartGame, int chanceWinGroup1, String group1name, String group2name) {
        this.timeStampStartGame = timeStampStartGame;
        this.chanceWinGroup1 = chanceWinGroup1;
        this.group1name = group1name;
        this.group2name = group2name;
        this.winner=0;
    }

    public boolean isSameGame(Game game) {
        if (!game.getGroup1name().equals(group1name) || !game.getGroup2name().equals(group2name) || game.getChanceWinGroup1() != chanceWinGroup1 || !game.getCategoryName().equals(categoryName) || !game.getDescription().equals(description) || game.getTimeStampStartGame() != timeStampStartGame ||!game.getStatus().equals(status) )
            return false;
        return true;
    }

    public String getStatus() {
        return status;
    }


    public String getTimeAndDate() {
        return Utils.getDateAndTimeFromTimeStamp(timeStampStartGame);
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Game(long timeStampStartGame, int chanceWinGroup1, String group1name, String group2name, String status, String description, String categoryName) {
        this.timeStampStartGame = timeStampStartGame;
        this.chanceWinGroup1 = chanceWinGroup1;
        this.group1name = group1name;
        this.group2name = group2name;
        this.description = description;
        this.categoryName = categoryName;
        this.status = status;
        this.winner=0;
     }

    public Game() {
     }




    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategoryName() {
        return Utils.geteFirstLattersUpperCase(categoryName);
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getTimeStampStartGame() {
        return timeStampStartGame;
    }

    public void setTimeStampStartGame(long timeStampStartGame) {
        this.timeStampStartGame = timeStampStartGame;
    }

    public int getChanceWinGroup1() {
        return chanceWinGroup1;
    }

    public void setChanceWinGroup1(int chanceWinGroup1) {
        this.chanceWinGroup1 = chanceWinGroup1;
    }

    public String getGroup1name() {
        return group1name;
    }

    public void setGroup1name(String group1name) {
        this.group1name = group1name;
    }

    public String getGroup2name() {
        return group2name;
    }

    public void setGroup2name(String group2name) {
        this.group2name = group2name;
    }


    @Override
    public String toString() {
        return "Game{" +
                "key='" + key + '\'' +
                ", timeStampStartGame=" + timeStampStartGame +
                ", chanceWinGroup1=" + chanceWinGroup1 +
                ", group1name='" + group1name + '\'' +
                ", group2name='" + group2name + '\'' +
                ", description='" + description + '\'' +
                ", categoryName='" + categoryName + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
