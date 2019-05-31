package com.sagi.gambling.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.internal.Util;
import com.sagi.gambling.R;
import com.sagi.gambling.adapters.AdapterGroups;
import com.sagi.gambling.adapters.AdapterResultFreinds;
import com.sagi.gambling.adapters.AdapterTag;
import com.sagi.gambling.entities.Gamble;
import com.sagi.gambling.entities.Group;
import com.sagi.gambling.entities.GroupMember;
import com.sagi.gambling.entities.HandleGroup;
import com.sagi.gambling.entities.User;
import com.sagi.gambling.interfaces.IGroupsFragment;
import com.sagi.gambling.utilities.SharedPreferencesHelper;
import com.sagi.gambling.utilities.constant.FireBaseConstant;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.IOException;
import java.util.ArrayList;

import static com.sagi.gambling.utilities.constant.GeneralConstants.STATUS_PRIVATE;
import static com.sagi.gambling.utilities.constant.GeneralConstants.STATUS_PUBLIC;


public class MyGroupsFragment extends Fragment implements AdapterGroups.CallBackAdapterGroups  {

    private OnFragmentInteractionListener mListener;

    private ProgressDialog progressDialog = null;
    private RecyclerView recyclerViewGroups;
    private ArrayList<Group> arrGroups;
    private AdapterGroups adapterGroups;
    private DatabaseReference myRef;

    public MyGroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_my_groups, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        myRef = FirebaseDatabase.getInstance().getReference();

        recyclerViewGroups = view.findViewById(R.id.recyclerViewGroups);
        arrGroups = new ArrayList<Group>();
        loadAllMyGroups();
        adapterGroups = new AdapterGroups(arrGroups, getContext(), false, this);
        recyclerViewGroups.setHasFixedSize(true);
        recyclerViewGroups.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerViewGroups.setAdapter(adapterGroups);
    }


    private void loadAllMyGroups() {
        myRef.child(FireBaseConstant.MY_GROUPS_CHAT_TABLE).child(SharedPreferencesHelper.getInstance(getContext()).getUser().textEmailForFirebase()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    HandleGroup handleGroup = snapshot.getValue(HandleGroup.class);
                    loadGroupFromKey(handleGroup);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private ValueEventListener mListenerChangeGroup;
    private String lastGroupKeyTheRemoved = "";

    private void loadGroupFromKey(final HandleGroup handleGroup) {

        mListenerChangeGroup = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                Group group = dataSnapshot.getValue(Group.class);
                if (lastGroupKeyTheRemoved.equals(group.getGroupKey()))
                    return;

                group.setMyGroupGamble(handleGroup.getGambleSelected());
                boolean isEdit = false;
                for (int i = 0; i < arrGroups.size(); i++) {
                    if (arrGroups.get(i).getGroupKey().equals(group.getGroupKey())) {
                        arrGroups.get(i).setCountUsers(group.getCountUsers());
                        isEdit = true;
                    }
                }
                if (!isEdit)
                    arrGroups.add(0, group);
                adapterGroups.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };

        myRef.child(FireBaseConstant.GROUPS_CHAT_TABLE).child(handleGroup.getKeyGroup()).addValueEventListener(mListenerChangeGroup);
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void joinGroup(Group group) {

    }

    @Override
    public void removeFromRequestGroup(Group group) {

    }


    private void decrementGroupUsers(final Group group) {
        myRef.child(FireBaseConstant.GROUPS_CHAT_TABLE).child(group.getGroupKey()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Group groupUpdated = dataSnapshot.getValue(Group.class);
                group.setCountUsers(groupUpdated.getCountUsers() - 1);
                myRef.child(FireBaseConstant.GROUPS_CHAT_TABLE).child(group.getGroupKey()).setValue(group);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onClickItem(Group group) {
        //TODO
        Toast.makeText(getContext(), "dsg", Toast.LENGTH_LONG).show();
        mListener.showChatGroup(group);
    }

    @Override
    public void removeFromGroup(final Group group) {
        lastGroupKeyTheRemoved = group.getGroupKey();
        myRef.child(FireBaseConstant.GROUPS_CHAT_TABLE).child(group.getGroupKey()).removeEventListener(mListenerChangeGroup);

        for (int i = 0; i < arrGroups.size(); i++) {
            if (arrGroups.get(i).getGroupKey().equals(group.getGroupKey()))
                arrGroups.remove(i);
        }
        adapterGroups.notifyDataSetChanged();

        decrementGroupUsers(group);

        myRef.child(FireBaseConstant.MY_GROUPS_CHAT_TABLE).child(SharedPreferencesHelper.getInstance(getContext()).getUser().textEmailForFirebase()).child(group.getGroupKey()).removeValue();
        myRef.child(FireBaseConstant.MEMBERS_GROUP_TABLE).child(group.getGroupKey()).child(SharedPreferencesHelper.getInstance(getContext()).getUser().textEmailForFirebase()).removeValue();


    }
    @Override
    public void addFriend(Group group) {
        mListener.showAddFriendsFragment(group);
    }


    @Override
    public void showListGroupFriends(Group group) {
        mListener.showListGroupFriends(group.getGroupKey());
    }



    public interface OnFragmentInteractionListener {
        void showChatGroup(Group group);

        void showAddFriendsFragment(Group group);

        void showListGroupFriends(String groupKey);
    }
}
