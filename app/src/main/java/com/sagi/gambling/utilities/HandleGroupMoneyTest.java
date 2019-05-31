package com.sagi.gambling.utilities;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sagi.gambling.entities.Game;
import com.sagi.gambling.entities.Group;
import com.sagi.gambling.entities.HandleGroup;
import com.sagi.gambling.utilities.constant.FireBaseConstant;
import com.sagi.gambling.utilities.constant.GeneralConstants;

import java.util.ArrayList;

public class HandleGroupMoneyTest {

    private DatabaseReference myRef;
    private Context context;

    public HandleGroupMoneyTest(){
        myRef = FirebaseDatabase.getInstance().getReference();
        handleGroupGambles();
    }

    private void handleGroupGambles() {
        myRef.child(FireBaseConstant.MY_GROUPS_CHAT_TABLE).child(SharedPreferencesHelper.getInstance(context).getUser().textEmailForFirebase()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ArrayList<String> arrGroupsKeys = new ArrayList<>();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    HandleGroup handleGroup = snapshot.getValue(HandleGroup.class);
                    arrGroupsKeys.add(handleGroup.getKeyGroup());
                }
                loadMyGamesKeys(arrGroupsKeys);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void loadMyGamesKeys(ArrayList<String> arrGroupsKeys) {
        final ArrayList<String> arrGamesKeys = new ArrayList<>();
        for (int i = 0; i < arrGroupsKeys.size(); i++) {
            myRef.child(FireBaseConstant.GROUPS_CHAT_TABLE).child(arrGroupsKeys.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    Group group = dataSnapshot.getValue(Group.class);
                    arrGamesKeys.add(group.getKeyGame());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        listenerChangeGame(arrGamesKeys);
    }

    private void listenerChangeGame(final ArrayList<String> arrGamesKeys) {
        for (int i = 0; i < arrGamesKeys.size(); i++) {
            final int finalI = i;
            myRef.child(FireBaseConstant.GAMBLES_TABLE).child(arrGamesKeys.get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Game game = dataSnapshot.getValue(Game.class);
                    if (game.getStatus().equals(GeneralConstants.STATUS_ENDED))
                        giveMoneyToUser(arrGamesKeys.get(finalI), game);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
    }

    private void giveMoneyToUser(String groupKey, Game game) {
        Toast.makeText(context, "money", Toast.LENGTH_SHORT);
    }


}
