package com.sagi.gambling.broadcast_recivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sagi.gambling.entities.User;
import com.sagi.gambling.utilities.SharedPreferencesHelper;
import com.sagi.gambling.utilities.constant.FireBaseConstant;

public class PowerConnectionReciever extends BroadcastReceiver {

    private Context context;
    private DatabaseReference myRef= FirebaseDatabase.getInstance().getReference();;


    public PowerConnectionReciever() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        this.context=context;

        if (intent.getAction().equals(Intent.ACTION_POWER_CONNECTED)) {
//            Toast.makeText(context, "The device is charging", Toast.LENGTH_SHORT).show();
            giveMoneyToUser();
//        } else {
//            intent.getAction().equals(Intent.ACTION_POWER_DISCONNECTED);
//            Toast.makeText(context, "The device is not charging", Toast.LENGTH_SHORT).show();
        }
    }

    private void giveMoneyToUser() {
        Toast.makeText(context, "Thank You For Using Our App. You Earned 5$", Toast.LENGTH_LONG).show();
        User user = SharedPreferencesHelper.getInstance(context).getUser();
        user.addMoney(5);
        myRef.child(FireBaseConstant.USERS_TABLE).child(user.textEmailForFirebase()).child("totalMoney").setValue(user.getTotalMoney());
        SharedPreferencesHelper.getInstance(context).setUser(user);
    }

    public IntentFilter getIntentFilter(){
        IntentFilter ifilter = new IntentFilter();
        ifilter.addAction(Intent.ACTION_POWER_CONNECTED);
        ifilter.addAction(Intent.ACTION_POWER_DISCONNECTED);
        return ifilter;
    }


}