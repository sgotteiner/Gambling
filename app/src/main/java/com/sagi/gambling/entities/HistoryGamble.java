package com.sagi.gambling.entities;

import com.sagi.gambling.utilities.constant.GeneralConstants;

/**
 * Created by User on 04/01/2019.
 */

public class HistoryGamble extends Gamble {
    private String nameGroup1;
    private String nameGroup2;
    private int chanceWin;
    private String status;
    private String description;
    private String category;
    private String groupTheWinn;
    private boolean isMySelectionWin;

    public String getCategory() {
        return category;
    }

    public HistoryGamble() {
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public HistoryGamble(Game game, Gamble gamble) {
        super(gamble.getKeyGame(), gamble.getUserEmail(), gamble.getGroupNameSelected(), gamble.getGambleMoney(), gamble.getTimeStamp());
        this.nameGroup1 = game.getGroup1name();
        this.nameGroup2 = game.getGroup2name();
        this.status = game.getStatus();
        this.description = game.getDescription();
        this.category = game.getCategoryName();
        setPaid(gamble.isPaid());
        setKey(gamble.getKey());

        String theWinner="";
        switch (game.getWinner()) {
            case 1:
                this.chanceWin  = 100-game.getChanceWinGroup1();
                theWinner = game.getGroup1name();
                break;
            case 2:
                this.chanceWin  = game.getChanceWinGroup1();
                theWinner = game.getGroup2name();
                break;
            case 3:
                theWinner = GeneralConstants.TIE;
                break;
        }
         isMySelectionWin = (gamble.getGroupNameSelected().toLowerCase().equals(theWinner.toLowerCase()));
    }

    public String getGroupTheWinn() {
        return groupTheWinn;
    }

    public boolean isMySelectionWin() {
        return isMySelectionWin;
    }

    public String getNameGroup1() {
        return nameGroup1;
    }

    public String getNameGroup2() {
        return nameGroup2;
    }

    public int getChanceWin() {
        return chanceWin;
    }

    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }
}
