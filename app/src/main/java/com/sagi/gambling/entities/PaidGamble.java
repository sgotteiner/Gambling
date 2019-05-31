package com.sagi.gambling.entities;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.sagi.gambling.utilities.SharedPreferencesHelper;
import com.sagi.gambling.utilities.constant.FireBaseConstant;
import com.sagi.gambling.utilities.constant.GeneralConstants;

public class PaidGamble extends HandleGroupMoney {


    public PaidGamble(Context context, CallbackHandleMoney callbackHandleMoney) {
        super(context, callbackHandleMoney);
    }

    public PaidGamble() {
    }

    @Override
    void insertMoney(String theGroupWin,int money, String yourSelected, String group1, String group2) {
        User user = SharedPreferencesHelper.getInstance(context).getUser();
        user.addMoney(money);
        SharedPreferencesHelper.getInstance(context).setTotalMoney(user.getTotalMoney());
        updateTotalMoney(user);

        if (mListener != null) {
            mListener.onWinYourGamble(theGroupWin,money, yourSelected, group1, group2);
        }
    }

    @Override
    void loadAllGamesNotPaid() {
        myRef.child(FireBaseConstant.HISTORY_GAMBLES_TABLE).child(SharedPreferencesHelper.getInstance(context).getUser().textEmailForFirebase()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String gambleKey = snapshot.getValue(String.class);
                    loadMyGamble(gambleKey);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public  void addListenerChangeGame(final Gamble gamble) {
        listenerChangeGame(gamble);
    }
    private void listenerChangeGame(final Gamble gamble) {
        myRef.child(FireBaseConstant.GAMES_TABLE).child(gamble.getKeyGame()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Game game = dataSnapshot.getValue(Game.class);
                if (game.getStatus().equals(GeneralConstants.STATUS_ENDED)) {
                    myRef.child(FireBaseConstant.GAMES_TABLE).child(gamble.getKeyGame()).removeEventListener(this);


                    final int GROUP_1_WIN = 1;
                    final int GROUP_2_WIN = 2;
                    final int GROUPS_TIE = 3;


                    float money = 0;
                    String groupWin = "";

                    switch (game.getWinner()) {
                        case GROUP_1_WIN:
                            groupWin = game.getGroup1name();
                            money = ((200 - game.getChanceWinGroup1()) / (float) 100) * gamble.getGambleMoney();
                            break;
                        case GROUP_2_WIN:
                            groupWin = game.getGroup2name();
                            money = ((100 + game.getChanceWinGroup1()) / (float) 100) * gamble.getGambleMoney();
                            break;
                        case GROUPS_TIE:
                            groupWin = GeneralConstants.TIE;
                            money = 1.5f * gamble.getGambleMoney();
                            break;
                    }

                    if (gamble.getGroupNameSelected().equals(groupWin)) {
                        insertMoney(groupWin,(int) money, gamble.getGroupNameSelected(), game.getGroup1name(), game.getGroup2name());
                    } else {
                        mListener.onLoseTheGame(groupWin ,gamble.getGambleMoney(),gamble.getGroupNameSelected(), game.getGroup1name(), game.getGroup2name());
                     }
                    updateMyGamblePaid(gamble);
                }


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void updateTotalMoney(User user) {
        myRef.child(FireBaseConstant.USERS_TABLE).child(user.textEmailForFirebase()).child("totalMoney").setValue(user.getTotalMoney());
    }

    private void updateMyGamblePaid(Gamble gamble) {
        gamble.setPaid(true);
        myRef.child(FireBaseConstant.GAMBLES_TABLE).child(gamble.getKey()).child("paid").setValue(true);
    }

    private void loadMyGamble(String gambleKey) {


        myRef.child(FireBaseConstant.GAMBLES_TABLE).child(gambleKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Gamble gamble = dataSnapshot.getValue(Gamble.class);
                if (!gamble.isPaid())
                    listenerChangeGame(gamble);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
