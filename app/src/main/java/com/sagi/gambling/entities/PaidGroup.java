package com.sagi.gambling.entities;

import android.content.Context;
import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.sagi.gambling.utilities.SharedPreferencesHelper;
import com.sagi.gambling.utilities.constant.FireBaseConstant;
import com.sagi.gambling.utilities.constant.GeneralConstants;

public class PaidGroup extends HandleGroupMoney {

    public PaidGroup(Context context, CallbackHandleMoney callbackHandleMoney) {
        super(context, callbackHandleMoney);
    }

    public PaidGroup() {
    }

    @Override
    void insertMoney(String theGroupWin,int money, String yourSelected, String group1, String group2) {
        User user = SharedPreferencesHelper.getInstance(context).getUser();
        user.addMoney(money);
        SharedPreferencesHelper.getInstance(context).setTotalMoney(user.getTotalMoney());
        if (mListener != null) {
            mListener.onWinYourGamble(theGroupWin,money, yourSelected, group1, group2);
        }
    }

    @Override
    void loadAllGamesNotPaid() {
        myRef.child(FireBaseConstant.MY_GROUPS_CHAT_TABLE).child(SharedPreferencesHelper.getInstance(context).getUser().textEmailForFirebase()).orderByChild("paid").equalTo(false).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    HandleGroup handleGroup = snapshot.getValue(HandleGroup.class);
                    loadGroup(handleGroup);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadGroup(final HandleGroup handleGroup) {
        myRef.child(FireBaseConstant.GROUPS_CHAT_TABLE).child(handleGroup.getKeyGroup()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Group group = dataSnapshot.getValue(Group.class);
                loadGame(group, handleGroup);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void addListenerChangeGame(final Group group, final HandleGroup handleGroup) {
        loadGame(group,handleGroup);
    }
    private void loadGame(final Group group, final HandleGroup handleGroup) {
        myRef.child(FireBaseConstant.GAMES_TABLE).child(group.getKeyGame()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Game game = dataSnapshot.getValue(Game.class);
                if (game.getStatus().equals(GeneralConstants.STATUS_ENDED)) {


                    String theWinner = "";
                    switch (game.getWinner()) {
                        case 1:
                            theWinner = game.getGroup1name();
                            break;
                        case 2:
                            theWinner = game.getGroup2name();
                            break;
                        case 3:
                            theWinner = GeneralConstants.TIE;
                            break;
                    }
                    boolean isMySelectionWin = (handleGroup.getGambleSelected().toLowerCase().equals(theWinner.toLowerCase()));

                    myRef.child(FireBaseConstant.GAMES_TABLE).child(group.getKeyGame()).removeEventListener(this);

                    // if (gameWinnerISelectedIs == game.getWinner()) {
                    if (isMySelectionWin) {
                        loadHowMuchINeedGet(theWinner,group, theWinner, handleGroup);
                    } else {
                        handleDontNeedToPaid(handleGroup.getKeyGroup());
                        mListener.onLoseTheGame(theWinner,group.getEntryAmount(), handleGroup.getGambleSelected(), group.getGroupName1(), group.getGroupName2());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void loadHowMuchINeedGet(final String theWinner, final Group group, String winnerGroup, final HandleGroup handleGroup) {
        myRef.child(FireBaseConstant.MEMBERS_GROUP_TABLE).child(group.getGroupKey()).orderByChild("gambleSelected").equalTo(winnerGroup).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                long countWinners = dataSnapshot.getChildrenCount();
                handleDontNeedToPaid(group.getGroupKey());
                int winMoney = (int) ((group.getEntryAmount() * group.getCountUsers()) / countWinners);
                insertMoney(theWinner,winMoney, handleGroup.getGambleSelected(), group.getGroupName1(), group.getGroupName2());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }


    private void handleDontNeedToPaid(String keyGroup) {
        myRef.child(FireBaseConstant.MY_GROUPS_CHAT_TABLE).child(SharedPreferencesHelper.getInstance(context).getUser().textEmailForFirebase()).child(keyGroup).child("paid").setValue(true);
    }
}