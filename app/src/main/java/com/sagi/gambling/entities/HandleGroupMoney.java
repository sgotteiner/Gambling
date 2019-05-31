package com.sagi.gambling.entities;

import android.content.Context;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public abstract class HandleGroupMoney {
    protected Context context;
    protected DatabaseReference myRef;
    protected CallbackHandleMoney mListener;

    public HandleGroupMoney(Context context,CallbackHandleMoney  callbackHandleMoney) {
        this.context = context;
        this.myRef = FirebaseDatabase.getInstance().getReference();
        this.mListener=callbackHandleMoney;
        loadAllGamesNotPaid();
    }

    public HandleGroupMoney() {
    }

    abstract void insertMoney(String theGroupWin, int money, String yourSelected, String group1, String group2);

    abstract void loadAllGamesNotPaid();


    public interface CallbackHandleMoney{
        void onLoseTheGame(String theGroupWin,int money,String yourSelected,String group1,String group2);
        void onWinYourGamble(String theGroupWin,int money,String yourSelected,String group1,String group2);
    }

}
