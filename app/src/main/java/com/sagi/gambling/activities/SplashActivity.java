package com.sagi.gambling.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sagi.gambling.R;
import com.sagi.gambling.entities.User;
import com.sagi.gambling.utilities.SharedPreferencesHelper;
import com.sagi.gambling.utilities.constant.FireBaseConstant;

import java.util.HashMap;

public class SplashActivity extends AppCompatActivity {

    private static final int TIME_SPLASH = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        handleIsManagerApp();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                showCurrentScreen();
            }
        }, TIME_SPLASH);
    }


    private void handleIsManagerApp() {
        final User user = SharedPreferencesHelper.getInstance(this).getUser();
        if (user == null)
            return;
        DatabaseReference myRef = FirebaseDatabase.getInstance().getReference();
        myRef.child(FireBaseConstant.USERS_TABLE).child(user.textEmailForFirebase()).child(FireBaseConstant.IS_MANAGER_APP).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isManager = dataSnapshot.getValue(Boolean.class);
                user.setManagerApp(isManager);
                SharedPreferencesHelper.getInstance(SplashActivity.this).setUser(user);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    private void showCurrentScreen() {
        Intent intent;
        if (isAlreadyLogin()) {
            intent = new Intent(SplashActivity.this, MainActivity.class);
        } else {
            intent = new Intent(SplashActivity.this, RegisterLoginActivity.class);
        }
        startActivity(intent);
        finish();
    }

    private boolean isAlreadyLogin() {
        return SharedPreferencesHelper.getInstance(this).isAlreadyLogin();
    }
}
